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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.helper;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineInfo;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderEdge;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PhysicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.AbstractTableOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellBackground;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellBackgroundProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellMarker;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableContentProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.itext.PatchRtfCell;
import org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.itext.PatchRtfWriter2;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.NoCloseOutputStream;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.rtf.table.RtfBorder;

/**
 * Creation-Date: 09.05.2007, 14:52:05
 *
 * @author Thomas Morgner
 */
public class RTFPrinter {
  private static final Log logger = LogFactory.getLog( RTFPrinter.class );

  private static final String CREATOR = ClassicEngineInfo.getInstance().getName() + " version "
      + ClassicEngineInfo.getInstance().getVersion();

  private OutputStream outputStream;
  private Configuration config;
  private ResourceManager resourceManager;
  private RTFImageCache imageCache;
  private Document document;
  private Table table;
  private CellBackgroundProducer cellBackgroundProducer;

  public RTFPrinter() {
  }

  public void init( final Configuration config, final OutputStream outputStream, final ResourceManager resourceManager ) {
    this.outputStream = outputStream;
    this.config = config;
    this.resourceManager = resourceManager;
  }

  /**
   * @noinspection IOResourceOpenedButNotSafelyClosed
   */
  public void print( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage,
      final TableContentProducer contentProducer, final RTFOutputProcessorMetaData metaData, final boolean incremental )
    throws ContentProcessingException {
    final int startRow = contentProducer.getFinishedRows();
    final int finishRow = contentProducer.getFilledRows();
    if ( incremental && startRow == finishRow ) {
      return;
    }

    if ( document == null ) {
      this.cellBackgroundProducer =
          new CellBackgroundProducer( metaData
              .isFeatureSupported( AbstractTableOutputProcessor.TREAT_ELLIPSE_AS_RECTANGLE ), metaData
              .isFeatureSupported( OutputProcessorFeature.UNALIGNED_PAGEBANDS ) );

      final PhysicalPageBox pageFormat = logicalPage.getPageGrid().getPage( 0, 0 );
      final float urx = (float) StrictGeomUtility.toExternalValue( pageFormat.getWidth() );
      final float ury = (float) StrictGeomUtility.toExternalValue( pageFormat.getHeight() );

      final float marginLeft = (float) StrictGeomUtility.toExternalValue( pageFormat.getImageableX() );
      final float marginRight =
          (float) StrictGeomUtility.toExternalValue( pageFormat.getWidth() - pageFormat.getImageableWidth()
              - pageFormat.getImageableX() );
      final float marginTop = (float) StrictGeomUtility.toExternalValue( pageFormat.getImageableY() );
      final float marginBottom =
          (float) StrictGeomUtility.toExternalValue( pageFormat.getHeight() - pageFormat.getImageableHeight()
              - pageFormat.getImageableY() );
      final Rectangle pageSize = new Rectangle( urx, ury );

      document = new Document( pageSize, marginLeft, marginRight, marginTop, marginBottom );
      imageCache = new RTFImageCache( resourceManager );

      // rtf does not support PageFormats or other meta data...
      final PatchRtfWriter2 instance = PatchRtfWriter2.getInstance( document, new NoCloseOutputStream( outputStream ) );
      instance.getDocumentSettings().setAlwaysUseUnicode( true );

      final String author =
          config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.Author" );
      if ( author != null ) {
        document.addAuthor( author );
      }

      final String title =
          config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.Title" );
      if ( title != null ) {
        document.addTitle( title );
      }

      document.addProducer();
      document.addCreator( RTFPrinter.CREATOR );

      try {
        document.addCreationDate();
      } catch ( Exception e ) {
        RTFPrinter.logger.debug( "Unable to add creation date. It will have to work without it.", e );
      }

      document.open();
    }

    // Start a new page.
    try {
      final SheetLayout sheetLayout = contentProducer.getSheetLayout();
      final int columnCount = contentProducer.getColumnCount();
      if ( table == null ) {
        final int rowCount = contentProducer.getRowCount();
        table = new Table( columnCount, rowCount );
        table.setAutoFillEmptyCells( false );
        table.setWidth( 100 ); // span the full page..
        // and finally the content ..

        final float[] cellWidths = new float[columnCount];
        for ( int i = 0; i < columnCount; i++ ) {
          cellWidths[i] = (float) StrictGeomUtility.toExternalValue( sheetLayout.getCellWidth( i, i + 1 ) );
        }
        table.setWidths( cellWidths );
      }

      // logger.debug ("Processing: " + startRow + " " + finishRow + " " + incremental);

      for ( int row = startRow; row < finishRow; row++ ) {
        for ( short col = 0; col < columnCount; col++ ) {
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
            if ( background == null ) {
              // An empty cell .. ignore
              final PatchRtfCell cell = new PatchRtfCell();
              cell.setBorderWidth( 0 );
              cell.setMinimumHeight( (float) StrictGeomUtility.toExternalValue( sheetLayout.getRowHeight( row ) ) );
              table.addCell( cell, row, col );
              continue;
            }

            // A empty cell with a defined background ..
            final PatchRtfCell cell = new PatchRtfCell();
            cell.setBorderWidth( 0 );
            cell.setMinimumHeight( (float) StrictGeomUtility.toExternalValue( sheetLayout.getRowHeight( row ) ) );
            updateCellStyle( cell, background );
            table.addCell( cell, row, col );
            continue;
          }

          if ( content.isCommited() == false ) {
            throw new InvalidReportStateException( "Uncommited content encountered" );
          }

          final long contentOffset = contentProducer.getContentOffset( row, col );
          final long colPos = sheetLayout.getXPosition( col );
          final long rowPos = sheetLayout.getYPosition( row );
          if ( content.getX() != colPos || ( content.getY() + contentOffset ) != rowPos ) {
            // A spanned cell ..
            continue;
          }

          final int colSpan = sheetLayout.getColSpan( col, content.getX() + content.getWidth() );
          final int rowSpan = sheetLayout.getRowSpan( row, content.getY() + content.getHeight() + contentOffset );

          final CellBackground realBackground =
              cellBackgroundProducer.getBackgroundForBox( logicalPage, sheetLayout, col, row, colSpan, rowSpan, false,
                  sectionType, content );

          final PatchRtfCell cell = new PatchRtfCell();
          cell.setRowspan( rowSpan );
          cell.setColspan( colSpan );
          cell.setBorderWidth( 0 );
          cell.setMinimumHeight( (float) StrictGeomUtility.toExternalValue( sheetLayout.getRowHeight( row ) ) );
          if ( realBackground != null ) {
            updateCellStyle( cell, realBackground );
          }

          computeCellStyle( content, cell );

          // export the cell and all content ..
          final RTFTextExtractor etx = new RTFTextExtractor( metaData );
          etx.compute( content, cell, imageCache );

          table.addCell( cell, row, col );
          content.setFinishedTable( true );
          // logger.debug("set Finished to cell (" + col + ", " + row + "," + content.getName() + ")");
        }

      }

      if ( incremental == false ) {
        document.add( table );
        table = null;
      }
    } catch ( DocumentException e ) {
      throw new ContentProcessingException( "Failed to generate RTF-Document", e );
    }
  }

  private void computeCellStyle( final RenderBox content, final Cell cell ) {
    final ElementAlignment verticalAlign = content.getNodeLayoutProperties().getVerticalAlignment();
    if ( ElementAlignment.BOTTOM.equals( verticalAlign ) ) {
      cell.setVerticalAlignment( Element.ALIGN_BOTTOM );
    } else if ( ElementAlignment.MIDDLE.equals( verticalAlign ) ) {
      cell.setVerticalAlignment( Element.ALIGN_MIDDLE );
    } else {
      cell.setVerticalAlignment( Element.ALIGN_TOP );
    }

    final ElementAlignment textAlign =
        (ElementAlignment) content.getStyleSheet().getStyleProperty( ElementStyleKeys.ALIGNMENT );
    if ( ElementAlignment.RIGHT.equals( textAlign ) ) {
      cell.setHorizontalAlignment( Element.ALIGN_RIGHT );
    } else if ( ElementAlignment.JUSTIFY.equals( textAlign ) ) {
      cell.setHorizontalAlignment( Element.ALIGN_JUSTIFIED );
    } else if ( ElementAlignment.CENTER.equals( textAlign ) ) {
      cell.setHorizontalAlignment( Element.ALIGN_CENTER );
    } else {
      cell.setHorizontalAlignment( Element.ALIGN_LEFT );
    }
  }

  public void close() {
    if ( document != null ) {
      // cleanup..
      document.close();
      try {
        outputStream.flush();
      } catch ( IOException e ) {
        RTFPrinter.logger.info( "Failed to flush the RTF-Output stream." );
      } finally {
        document = null;
      }
    }
  }

  private int translateBorderStyle( final BorderStyle borderStyle ) {
    if ( BorderStyle.DASHED.equals( borderStyle ) ) {
      return RtfBorder.BORDER_DASHED;
    }
    if ( BorderStyle.DOT_DASH.equals( borderStyle ) ) {
      return RtfBorder.BORDER_DOT_DASH;
    }
    if ( BorderStyle.DOT_DOT_DASH.equals( borderStyle ) ) {
      return RtfBorder.BORDER_DOT_DOT_DASH;
    }
    if ( BorderStyle.DOTTED.equals( borderStyle ) ) {
      return RtfBorder.BORDER_DOTTED;
    }
    if ( BorderStyle.DOUBLE.equals( borderStyle ) ) {
      return RtfBorder.BORDER_DOUBLE;
    }
    if ( BorderStyle.HIDDEN.equals( borderStyle ) ) {
      return RtfBorder.BORDER_NONE;
    }
    if ( BorderStyle.NONE.equals( borderStyle ) ) {
      return RtfBorder.BORDER_NONE;
    }
    if ( BorderStyle.GROOVE.equals( borderStyle ) ) {
      return RtfBorder.BORDER_ENGRAVE;
    }
    if ( BorderStyle.RIDGE.equals( borderStyle ) ) {
      return RtfBorder.BORDER_EMBOSS;
    }
    if ( BorderStyle.INSET.equals( borderStyle ) ) {
      return RtfBorder.BORDER_SINGLE;
    }
    if ( BorderStyle.OUTSET.equals( borderStyle ) ) {
      return RtfBorder.BORDER_SINGLE;
    }
    if ( BorderStyle.SOLID.equals( borderStyle ) ) {
      return RtfBorder.BORDER_SINGLE;
    }
    if ( BorderStyle.WAVE.equals( borderStyle ) ) {
      return RtfBorder.BORDER_WAVY;
    }
    return RtfBorder.BORDER_NONE;
  }

  private void updateCellStyle( final Cell cell, final CellBackground background ) {

    final Color backgroundColor = background.getBackgroundColor();
    if ( backgroundColor != null ) {
      cell.setBackgroundColor( backgroundColor );
    }
    final BorderEdge top = background.getTop();
    if ( BorderEdge.EMPTY.equals( top ) == false ) {
      cell.setBorderColorTop( top.getColor() );
      cell.setBorderWidthTop( (float) StrictGeomUtility.toExternalValue( top.getWidth() ) );
    }

    final BorderEdge left = background.getLeft();
    if ( BorderEdge.EMPTY.equals( left ) == false ) {
      cell.setBorderColorLeft( left.getColor() );
      cell.setBorderWidthLeft( (float) StrictGeomUtility.toExternalValue( left.getWidth() ) );
    }

    final BorderEdge bottom = background.getBottom();
    if ( BorderEdge.EMPTY.equals( bottom ) == false ) {
      cell.setBorderColorBottom( bottom.getColor() );
      cell.setBorderWidthBottom( (float) StrictGeomUtility.toExternalValue( bottom.getWidth() ) );
    }

    final BorderEdge right = background.getRight();
    if ( BorderEdge.EMPTY.equals( right ) == false ) {
      cell.setBorderColorRight( right.getColor() );
      cell.setBorderWidthRight( (float) StrictGeomUtility.toExternalValue( right.getWidth() ) );
    }
  }
}
