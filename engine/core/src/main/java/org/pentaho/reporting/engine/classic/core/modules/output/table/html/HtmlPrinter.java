/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001 - 2020 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.html;

import java.awt.Color;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderCorner;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderEdge;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.AbstractTableOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellBackground;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellBackgroundProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellMarker;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableContentProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.AbstractHtmlPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.ContentUrlReWriteService;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.DefaultHtmlContentGenerator;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.DefaultStyleBuilderFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.StyleBuilder;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.WriterService;
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

/**
 * This class is the actual HTML-emitter.
 *
 * @author Thomas Morgner
 * @noinspection HardCodedStringLiteral
 */
public abstract class HtmlPrinter extends AbstractHtmlPrinter implements ContentUrlReWriteService {

  private static final Log logger = LogFactory.getLog( HtmlPrinter.class );

  private boolean assumeZeroMargins;
  private boolean assumeZeroBorders;
  private boolean assumeZeroPaddings;

  private ContentLocation contentLocation;
  private NameGenerator contentNameGenerator;

  private URLRewriter urlRewriter;
  private ContentItem documentContentItem;

  private HtmlTextExtractor textExtractor;
  private CellBackgroundProducer cellBackgroundProducer;
  private WriterService writer;

  protected HtmlPrinter( final ResourceManager resourceManager ) {
    super( resourceManager );

    assumeZeroMargins = true;
    assumeZeroBorders = true;
    assumeZeroPaddings = true;

    // this primitive implementation assumes that the both repositories are
    // the same ..
    urlRewriter = new FileSystemURLRewriter();

  }

  public String rewriteContentDataItem( final ContentItem item ) throws URLRewriteException {
    return urlRewriter.rewrite( documentContentItem, item );
  }

  protected boolean isAssumeZeroMargins() {
    return assumeZeroMargins;
  }

  protected void setAssumeZeroMargins( final boolean assumeZeroMargins ) {
    this.assumeZeroMargins = assumeZeroMargins;
  }

  protected boolean isAssumeZeroBorders() {
    return assumeZeroBorders;
  }

  protected void setAssumeZeroBorders( final boolean assumeZeroBorders ) {
    this.assumeZeroBorders = assumeZeroBorders;
  }

  protected boolean isAssumeZeroPaddings() {
    return assumeZeroPaddings;
  }

  protected void setAssumeZeroPaddings( final boolean assumeZeroPaddings ) {
    this.assumeZeroPaddings = assumeZeroPaddings;
  }

  public ContentLocation getContentLocation() {
    return contentLocation;
  }

  public NameGenerator getContentNameGenerator() {
    return contentNameGenerator;
  }

  protected ContentUrlReWriteService getContentReWriteService() {
    return this;
  }

  public void setContentWriter( final ContentLocation contentLocation, final NameGenerator contentNameGenerator ) {
    this.contentNameGenerator = contentNameGenerator;
    this.contentLocation = contentLocation;
  }

  public URLRewriter getUrlRewriter() {
    return urlRewriter;
  }

  public void setUrlRewriter( final URLRewriter urlRewriter ) {
    if ( urlRewriter == null ) {
      throw new NullPointerException();
    }
    this.urlRewriter = urlRewriter;
  }

  public ContentItem getDocumentContentItem() {
    return documentContentItem;
  }

  protected void setDocumentContentItem( final ContentItem documentContentItem ) {
    this.documentContentItem = documentContentItem;
  }

  private HtmlRowBackgroundStruct getCommonBackground( final LogicalPageBox logicalPageBox,
      final SheetLayout sheetLayout, final int row, final TableContentProducer tableContentProducer ) {
    Color color = null;
    BorderEdge topEdge = BorderEdge.EMPTY;
    BorderEdge bottomEdge = BorderEdge.EMPTY;

    final int columnCount = sheetLayout.getColumnCount();
    for ( int col = 0; col < columnCount; col += 1 ) {
      final CellMarker.SectionType sectionType = tableContentProducer.getSectionType( row, col );
      final RenderBox content = tableContentProducer.getContent( row, col );
      final CellBackground backgroundAt;
      if ( content == null ) {
        final RenderBox background = tableContentProducer.getBackground( row, col );
        if ( background != null ) {
          backgroundAt =
              cellBackgroundProducer.getBackgroundForBox( logicalPageBox, sheetLayout, col, row, 1, 1, false,
                  sectionType, background );
        } else {
          backgroundAt =
              cellBackgroundProducer.getBackgroundAt( logicalPageBox, sheetLayout, col, row, false, sectionType );
        }
      } else {
        final long contentOffset = tableContentProducer.getContentOffset( row, col );
        final int colSpan = sheetLayout.getColSpan( col, content.getX() + content.getWidth() );
        final int rowSpan = sheetLayout.getRowSpan( row, content.getY() + content.getHeight() + contentOffset );
        backgroundAt =
            cellBackgroundProducer.getBackgroundForBox( logicalPageBox, sheetLayout, col, row, colSpan, rowSpan, false,
                sectionType, content );
      }
      if ( backgroundAt == null ) {
        HtmlRowBackgroundStruct struct = new HtmlRowBackgroundStruct();
        struct.fail();
        return struct;
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
        HtmlRowBackgroundStruct struct = new HtmlRowBackgroundStruct();
        struct.fail();
        return struct;
      }
    }
    HtmlRowBackgroundStruct struct = new HtmlRowBackgroundStruct();
    struct.set( color, topEdge, bottomEdge );
    return struct;
  }

  public void print( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage,
      final TableContentProducer contentProducer, final OutputProcessorMetaData metaData, final boolean incremental )
    throws ContentProcessingException {
    print( logicalPageKey, logicalPage, contentProducer, metaData, incremental, true );

  }

  public void print( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage,
      final TableContentProducer contentProducer, final OutputProcessorMetaData metaData, final boolean incremental, final boolean writeAttrs )
    throws ContentProcessingException {
    try {
      final SheetLayout sheetLayout = contentProducer.getSheetLayout();
      final int startRow = contentProducer.getFinishedRows();
      final int finishRow = contentProducer.getFilledRows();
      if ( incremental && startRow == finishRow ) {
        return;
      }

      DefaultHtmlContentGenerator contentGenerator = getContentGenerator();
      XmlWriter xmlWriter;

      if ( documentContentItem == null ) {
        this.cellBackgroundProducer =
            new CellBackgroundProducer( metaData
                .isFeatureSupported( AbstractTableOutputProcessor.TREAT_ELLIPSE_AS_RECTANGLE ), metaData
                .isFeatureSupported( OutputProcessorFeature.UNALIGNED_PAGEBANDS ) );
        initialize( metaData.getConfiguration() );

        documentContentItem = contentLocation.createItem( contentNameGenerator.generateName( null, "text/html" ) );

        this.writer = createWriterService( documentContentItem.getOutputStream() );
        xmlWriter = writer.getXmlWriter();

        openSheet( logicalPage.getAttributes(), contentProducer.getSheetName(), metaData, sheetLayout, xmlWriter );
      } else {
        xmlWriter = writer.getXmlWriter();
      }

      final int colCount = sheetLayout.getColumnCount();
      final boolean emptyCellsUseCSS = getTagHelper().isEmptyCellsUseCSS();
      StyleBuilder styleBuilder = getStyleBuilder();
      DefaultStyleBuilderFactory styleBuilderFactory = getStyleBuilderFactory();

      if ( textExtractor == null ) {
        textExtractor = new HtmlTextExtractor( metaData, xmlWriter, contentGenerator, getTagHelper() );
      }

      for ( int row = startRow; row < finishRow; row++ ) {
        final int rowHeight = (int) StrictGeomUtility.toExternalValue( sheetLayout.getRowHeight( row ) );
        final HtmlRowBackgroundStruct struct = getCommonBackground( logicalPage, sheetLayout, row, contentProducer );
        xmlWriter.writeTag( HtmlPrinter.XHTML_NAMESPACE, "tr", getTagHelper().createRowAttributes( rowHeight, struct ),
            XmlWriterSupport.OPEN );

        for ( int col = 0; col < colCount; col++ ) {
          final RenderBox content = contentProducer.getContent( row, col );
          final CellMarker.SectionType sectionType = contentProducer.getSectionType( row, col );
          if ( content == null ) {
            final RenderBox backgroundBox = contentProducer.getBackground( row, col );
            final CellBackground background;
            if ( backgroundBox != null ) {
              background =
                  cellBackgroundProducer.getBackgroundForBox( logicalPage, sheetLayout, col, row, 1, 1, true,
                      sectionType, backgroundBox );
            } else {
              background =
                  cellBackgroundProducer.getBackgroundAt( logicalPage, sheetLayout, col, row, true, sectionType );
            }
            writeBackgroundCell( background, xmlWriter );
            continue;
          }

          if ( content.isCommited() == false ) {
            throw new InvalidReportStateException( "Uncommited content encountered: " + row + ", " + col + ' '
                + content );
          }

          final long contentOffset = contentProducer.getContentOffset( row, col );

          final long colPos = sheetLayout.getXPosition( col );
          final long rowPos = sheetLayout.getYPosition( row );
          if ( content.getX() != colPos || ( content.getY() + contentOffset ) != rowPos ) {
            // A spanned cell ..
            if ( content.isFinishedTable() ) {
              continue;
            }
          }

          final int colSpan = sheetLayout.getColSpan( col, content.getX() + content.getWidth() );
          final int rowSpan = sheetLayout.getRowSpan( row, content.getY() + content.getHeight() + contentOffset );

          final CellBackground realBackground =
              cellBackgroundProducer.getBackgroundForBox( logicalPage, sheetLayout, col, row, colSpan, rowSpan, true,
                  sectionType, content );

          final StyleBuilder cellStyle =
              styleBuilderFactory.createCellStyle( styleBuilder, content.getStyleSheet(), content.getBoxDefinition(),
                  realBackground, null, null );
          final AttributeList cellAttributes =
              getTagHelper().createCellAttributes( colSpan, rowSpan, content.getAttributes(), content.getStyleSheet(),
                  realBackground, cellStyle );
          xmlWriter.writeTag( HtmlPrinter.XHTML_NAMESPACE, "td", cellAttributes, XmlWriterSupport.OPEN );

          final Object rawContent =
              content.getAttributes().getAttribute( AttributeNames.Html.NAMESPACE,
                  AttributeNames.Html.EXTRA_RAW_CONTENT );
          if ( rawContent != null ) {
            xmlWriter.writeText( String.valueOf( rawContent ) );
          }

          if ( realBackground != null ) {
            final String[] anchors = realBackground.getAnchors();
            for ( int i = 0; i < anchors.length; i++ ) {
              final String anchor = anchors[i];
              xmlWriter.writeTag( HtmlPrinter.XHTML_NAMESPACE, "a", "name", anchor, XmlWriterSupport.CLOSE );
            }
          }

          if ( Boolean.TRUE.equals( content.getAttributes().getAttribute( AttributeNames.Html.NAMESPACE,
              AttributeNames.Html.SUPPRESS_CONTENT ) ) == false ) {
            // the style of the content-box itself is already contained in the <td> tag. So there is no need
            // to duplicate the style here
            // already injected cellAttributes on the td tag - don't need to write Attrs again here
            if ( !textExtractor.performOutput( content, cellStyle.toArray(), writeAttrs ) ) {
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
          content.setFinishedTable( true );
        }
        xmlWriter.writeCloseTag();
      }

      if ( incremental == false ) {
        performCloseFile( contentProducer.getSheetName(), logicalPage.getAttributes(), writer );

        try {
          writer.close();
        } catch ( IOException e ) {
          // ignored ..
          logger.error( "Failed to close writer instance", e );
        }
        textExtractor = null;
        writer = null;
        documentContentItem = null;
      }
    } catch ( IOException ioe ) {
      try {
        if ( writer != null ) {
          writer.close();
        }
      } catch ( IOException e ) {
        // ignored ..
      }
      writer = null;
      documentContentItem = null;
      textExtractor = null;

      // ignore for now ..
      throw new ContentProcessingException( "IOError while creating content", ioe );
    } catch ( ContentIOException e ) {
      try {
        if ( writer != null ) {
          writer.close();
        }
      } catch ( IOException ex ) {
        // ignored ..
      }
      writer = null;
      documentContentItem = null;
      textExtractor = null;

      throw new ContentProcessingException( "Content-IOError while creating content", e );
    } catch ( URLRewriteException e ) {
      try {
        if ( writer != null ) {
          writer.close();
        }
      } catch ( IOException ex ) {
        // ignored ..
      }
      writer = null;
      documentContentItem = null;
      textExtractor = null;

      throw new ContentProcessingException( "Cannot create URL for external stylesheet", e );
    }
  }

}
