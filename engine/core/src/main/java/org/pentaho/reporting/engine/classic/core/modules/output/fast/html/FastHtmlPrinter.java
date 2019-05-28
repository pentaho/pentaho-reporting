/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2019 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.fast.html;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderCorner;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderEdge;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinitionFactory;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.FastGridLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.SheetPropertyCollector;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellBackground;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlRowBackgroundStruct;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.URLRewriteException;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.AbstractHtmlPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.ContentUrlReWriteService;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.DefaultStyleBuilderFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.StyleBuilder;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.WriterService;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.engine.classic.core.util.LongList;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.repository.ContentItem;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.NameGenerator;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

public class FastHtmlPrinter extends AbstractHtmlPrinter implements ContentUrlReWriteService {
  private static final Log logger = LogFactory.getLog( FastHtmlPrinter.class );

  private final SheetLayout sharedSheetLayout;
  private final FastHtmlContentItems contentItems;
  private final BoxDefinitionFactory boxDefinitionFactory;
  private ContentItem documentContentItem;
  private OutputProcessorMetaData metaData;
  private WriterService writer;
  private ReportAttributeMap reportAttributes;
  private String sheetName;
  private FastHtmlTextExtractor textExtractor;
  private int rowOffset;

  public FastHtmlPrinter( final SheetLayout sharedSheetLayout, final ResourceManager resourceManager,
      final FastHtmlContentItems contentItems ) {
    super( resourceManager );
    this.sharedSheetLayout = sharedSheetLayout;
    this.contentItems = contentItems;
    boxDefinitionFactory = new BoxDefinitionFactory();
  }

  public String rewriteContentDataItem( final ContentItem item ) throws URLRewriteException {
    return contentItems.getUrlRewriter().rewrite( documentContentItem, item );
  }

  protected ContentUrlReWriteService getContentReWriteService() {
    return this;
  }

  public void close() throws IOException, ContentIOException {
    if ( writer != null ) {
      performCloseFile( sheetName, reportAttributes, writer );
      try {
        writer.close();
      } catch ( IOException e ) {
        // ignored ..
        logger.error( "Failed to close writer instance", e );
      }
    }
    textExtractor = null;
    writer = null;
    documentContentItem = null;

  }

  public void init( final OutputProcessorMetaData metaData, final ReportDefinition report ) {
    this.metaData = metaData;
    this.reportAttributes = report.getAttributes();
    initialize( metaData.getConfiguration() );
  }

  public void print( final ExpressionRuntime runtime, final FastGridLayout gridLayout,
      final HashMap<InstanceID, ReportElement> elements, final HashMap<InstanceID, FastHtmlImageBounds> recordedBounds,
      final FastHtmlStyleCache styleCache ) {
    if ( gridLayout.getRowCount() == 0 ) {
      return;
    }

    try {
      XmlWriter xmlWriter;

      if ( documentContentItem == null ) {
        ContentLocation contentLocation = contentItems.getContentLocation();
        NameGenerator contentNameGenerator = contentItems.getContentNameGenerator();
        documentContentItem = contentLocation.createItem( contentNameGenerator.generateName( null, "text/html" ) );

        this.writer = createWriterService( documentContentItem.getOutputStream() );
        xmlWriter = writer.getXmlWriter();

        setDataWriter( this.contentItems.getDataLocation(), this.contentItems.getDataNameGenerator() );
        openSheet( reportAttributes, sheetName, metaData, sharedSheetLayout, xmlWriter );
        textExtractor = new FastHtmlTextExtractor( metaData, xmlWriter, getContentGenerator(), getTagHelper() );
      } else {
        xmlWriter = writer.getXmlWriter();
      }

      final boolean emptyCellsUseCSS = getTagHelper().isEmptyCellsUseCSS();

      final int rowCount = gridLayout.getRowCount();
      final int colCount = gridLayout.getColumnCount();
      for ( int row = 0; row < rowCount; row++ ) {
        AttributeList rowAttributes = styleCache.getRowAttributes( row );
        if ( rowAttributes == null ) {
          final int rowHeight = (int) StrictGeomUtility.toExternalValue( gridLayout.getCellHeights().get( row ) );
          final HtmlRowBackgroundStruct struct = getCommonBackground( gridLayout, colCount, row );
          rowAttributes = getTagHelper().createRowAttributes( rowHeight, struct );
          styleCache.putRowAttributes( row, rowAttributes );
        }

        xmlWriter.writeTag( HtmlPrinter.XHTML_NAMESPACE, "tr", rowAttributes, XmlWriterSupport.OPEN );

        for ( int col = 0; col < colCount; col++ ) {
          FastGridLayout.GridCell gridCell = gridLayout.get( row, col );
          if ( gridCell == null ) {
            // spanned content cell
            continue;
          }

          if ( gridCell.getInstanceId() == null ) {
            // background cell
            CellBackground background = gridCell.getLayoutInfo().getBackground();
            writeBackgroundCell( background, xmlWriter );
            continue;
          }

          ReportElement content = elements.get( gridCell.getInstanceId() );
          FastHtmlStyleCache.CellStyle cellStyle = computeCellAttributes( styleCache, row, col, gridCell, content );

          if ( content == null ) {
            xmlWriter.writeTag( HtmlPrinter.XHTML_NAMESPACE, "td", cellStyle.getCellAttributeList(),
                XmlWriterSupport.OPEN );
            if ( emptyCellsUseCSS == false ) {
              xmlWriter.writeText( "&nbsp;" );
            }
            xmlWriter.writeCloseTag();
            continue;
          }

          xmlWriter.writeTag( HtmlPrinter.XHTML_NAMESPACE, "td", cellStyle.getCellAttributeList(),
              XmlWriterSupport.OPEN );

          final Object rawContent =
              content.getAttribute( AttributeNames.Html.NAMESPACE, AttributeNames.Html.EXTRA_RAW_CONTENT );
          if ( rawContent != null ) {
            xmlWriter.writeText( String.valueOf( rawContent ) );
          }

          writeAnchors( xmlWriter, content );
          if ( Boolean.TRUE.equals( content.getAttributes().getAttribute( AttributeNames.Html.NAMESPACE,
              AttributeNames.Html.SUPPRESS_CONTENT ) ) == false ) {
            // the style of the content-box itself is already contained in the <td> tag. So there is no need
            // to duplicate the style here
            if ( textExtractor.performOutput( content, cellStyle.getCellStyle(), recordedBounds, runtime ) == false ) {
              if ( emptyCellsUseCSS == false ) {
                xmlWriter.writeText( "&nbsp;" );
              }
            }
          }

          final Object rawFooterContent =
              content.getAttributes().getAttribute( AttributeNames.Html.NAMESPACE,
                  AttributeNames.Html.EXTRA_RAW_FOOTER_CONTENT );
          if ( rawFooterContent != null ) {
            xmlWriter.writeText( String.valueOf( rawFooterContent ) );
          }

          xmlWriter.writeCloseTag();

        }

        xmlWriter.writeCloseTag();
      }

    } catch ( ContentIOException e ) {
      throw new InvalidReportStateException( e );
    } catch ( IOException e ) {
      throw new InvalidReportStateException( e );
    } catch ( URLRewriteException e ) {
      throw new InvalidReportStateException( e );
    } catch ( ContentProcessingException e ) {
      throw new InvalidReportStateException( e );
    }
  }

  private FastHtmlStyleCache.CellStyle computeCellAttributes( final FastHtmlStyleCache styleCache, final int row,
      final int col, final FastGridLayout.GridCell gridCell, final ReportElement content ) {
    StyleBuilder styleBuilder = getStyleBuilder();
    DefaultStyleBuilderFactory styleBuilderFactory = getStyleBuilderFactory();
    FastHtmlStyleCache.CellStyle cellStyleCache = styleCache.getCellAttributes( row, col );
    if ( cellStyleCache == null ) {
      final CellBackground realBackground = gridCell.getLayoutInfo().getBackground();
      final int colSpan = gridCell.getLayoutInfo().getColumnSpan();
      final int rowSpan = gridCell.getLayoutInfo().getRowSpan();

      if ( content == null ) {
        final StyleBuilder cellStyle = styleBuilderFactory.createCellStyle( styleBuilder, realBackground, null, null );
        final AttributeList cellAttributes =
            getTagHelper().createCellAttributes( colSpan, rowSpan, null, null, realBackground, cellStyle );
        cellStyleCache = new FastHtmlStyleCache.CellStyle( cellAttributes, cellStyle.toArray() );
      } else {
        BoxDefinition boxDefinition = boxDefinitionFactory.getBoxDefinition( content.getComputedStyle() );
        final StyleBuilder cellStyle =
            styleBuilderFactory.createCellStyle( styleBuilder, content.getComputedStyle(), boxDefinition,
                realBackground, null, null );
        final AttributeList cellAttributes =
            getTagHelper().createCellAttributes( colSpan, rowSpan, content.getAttributes(), content.getComputedStyle(),
                realBackground, cellStyle );
        cellStyleCache = new FastHtmlStyleCache.CellStyle( cellAttributes, cellStyle.toArray() );
      }
      if ( shouldCacheStyle( content ) ) {
        styleCache.putCellAttributes( row, col, cellStyleCache );
      }
    }
    return cellStyleCache;
  }

  @VisibleForTesting
  protected boolean shouldCacheStyle( final ReportElement content ) {
    // If any of the cell attributes is present on the content expressions we should not cache it since it can change
    if ( content != null ) {
      for ( String attributeNS : content.getAttributeExpressionNamespaces() ) {
        for ( String attributeName : content.getAttributeExpressionNames( attributeNS ) ) {
          if ( content.getAttribute( attributeNS, attributeName ) != null ) {
            return false;
          }
        }
      }
    }

    //by default the style should be cached
    return true;
  }

  private void writeAnchors( final XmlWriter xmlWriter, final ReportElement realBackground ) throws IOException {
    if ( realBackground != null ) {
      final String[] anchors = new String[0]; // realBackground.getAnchors();
      for ( int i = 0; i < anchors.length; i++ ) {
        final String anchor = anchors[i];
        xmlWriter.writeTag( HtmlPrinter.XHTML_NAMESPACE, "a", "name", anchor, XmlWriterSupport.CLOSE );
      }
    }
  }

  private HtmlRowBackgroundStruct getCommonBackground( final FastGridLayout gridLayout, final int columnCount,
      final int row ) {
    final HtmlRowBackgroundStruct bg = new HtmlRowBackgroundStruct();
    BorderEdge topEdge = BorderEdge.EMPTY;
    BorderEdge bottomEdge = BorderEdge.EMPTY;
    Color color = null;
    for ( int col = 0; col < columnCount; col += 1 ) {
      FastGridLayout.GridCell gridCell = gridLayout.get( col, row );
      if ( gridCell == null ) {
        // spanned cell
        continue;
      }

      CellBackground backgroundAt = gridCell.getLayoutInfo().getBackground();
      if ( backgroundAt == null ) {
        bg.fail();
        return bg;
      }

      boolean fail = false;
      if ( col == 0 ) {
        color = backgroundAt.getBackgroundColor();
        topEdge = backgroundAt.getTop();
        bottomEdge = backgroundAt.getBottom();
      } else {
        if ( ObjectUtilities.equal( color, backgroundAt.getBackgroundColor() ) == false ) {
          fail = true;
        }
        if ( ObjectUtilities.equal( topEdge, backgroundAt.getTop() ) == false ) {
          fail = true;
        }
        if ( ObjectUtilities.equal( bottomEdge, backgroundAt.getBottom() ) == false ) {
          fail = true;
        }
      }

      if ( BorderCorner.EMPTY.equals( backgroundAt.getBottomLeft() ) == false ) {
        fail = true;
      }
      if ( BorderCorner.EMPTY.equals( backgroundAt.getBottomRight() ) == false ) {
        fail = true;
      }
      if ( BorderCorner.EMPTY.equals( backgroundAt.getTopLeft() ) == false ) {
        fail = true;
      }
      if ( BorderCorner.EMPTY.equals( backgroundAt.getTopRight() ) == false ) {
        fail = true;
      }
      if ( fail ) {
        bg.fail();
        break;
      }

    }
    bg.set( color, topEdge, bottomEdge );
    return bg;
  }

  public void startSection( final Band band ) {
    SheetPropertyCollector collector = new SheetPropertyCollector();
    sheetName = collector.compute( band );

  }

  public void endSection( final Band band, final FastGridLayout gridLayout ) {
    LongList cellHeights = gridLayout.getCellHeights();
    this.rowOffset += cellHeights.size();
  }
}
