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
 * Copyright (c) 2001 - 2016 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.DefaultImageReference;
import org.pentaho.reporting.engine.classic.core.ImageContainer;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.RenderUtility;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.AbstractTableOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellBackground;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellBackgroundProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellMarker;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableContentProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableRectangle;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.RotatedTextDrawable;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableWrapper;

import java.awt.Image;
import java.awt.Shape;
import java.io.IOException;
import java.io.OutputStream;

public class ExcelPrinter extends ExcelPrinterBase {
  private static final Log logger = LogFactory.getLog( ExcelPrinter.class );

  private OutputStream outputStream;
  private Workbook workbook;
  private Sheet sheet;
  private CellBackgroundProducer cellBackgroundProducer;
  private ExcelTextExtractor textExtractor;
  private ResourceManager resourceManager;
  private int sheetRow = 0;

  public ExcelPrinter( final OutputStream outputStream, final ResourceManager resourceManager ) {
    if ( outputStream == null ) {
      throw new NullPointerException();
    }
    if ( resourceManager == null ) {
      throw new NullPointerException();
    }
    this.resourceManager = resourceManager;
    this.outputStream = outputStream;
  }

  public void init( final OutputProcessorMetaData metaData ) {
    if ( metaData == null ) {
      throw new NullPointerException();
    }

    super.init( metaData, resourceManager );
    this.cellBackgroundProducer =
        new CellBackgroundProducer( metaData
            .isFeatureSupported( AbstractTableOutputProcessor.TREAT_ELLIPSE_AS_RECTANGLE ), metaData
            .isFeatureSupported( OutputProcessorFeature.UNALIGNED_PAGEBANDS ) );
  }

  public Sheet getSheet() {
    return sheet;
  }

  private void addNewSheet( final LogicalPageBox logicalPage, final TableContentProducer contentProducer, final SheetPropertySource excelTableContentProducer ) {
    // make sure a new patriarch is created if needed.
    sheet = openSheet( contentProducer.getSheetName() );
    // Start a new page.
    configureSheetPaperSize( sheet, logicalPage.getPageGrid().getPage( 0, 0 ) );
    configureSheetColumnWidths( sheet, contentProducer.getSheetLayout(), contentProducer.getColumnCount() );
    configureSheetProperties( sheet, excelTableContentProducer );
  }

  public void print( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage,
      final TableContentProducer contentProducer, final boolean incremental ) {
    if ( workbook == null ) {
      workbook = createWorkbook();
      initializeStyleProducers( workbook );
      this.textExtractor =
          new ExcelTextExtractor( getMetaData(), getFontColorProducer(), workbook.getCreationHelper(),
              getCellStyleProducer().getFontFactory() );
    }

    final int startRow = contentProducer.getFinishedRows();
    final int finishRow = contentProducer.getFilledRows();
    if ( incremental && startRow == finishRow ) {
      return;
    }

    // PRD-6134: Always update sheet properties so that header and footer are correctly handled
    final SheetPropertySource excelTableContentProducer = (SheetPropertySource) contentProducer;

    if ( sheet == null ) {
      addNewSheet( logicalPage, contentProducer, excelTableContentProducer );
    }

    // and finally the content ..
    final SheetLayout sheetLayout = contentProducer.getSheetLayout();
    final int colCount = sheetLayout.getColumnCount();

    for ( int contentRow = startRow; contentRow < finishRow; contentRow++ ) {
      if ( getMaxSheetRowCount() != -1 && sheetRow >= getMaxSheetRowCount() ) {
        addNewSheet( logicalPage, contentProducer, excelTableContentProducer );
        sheetRow = 0;
      }
      final Row hssfRow = getRowAt( sheetRow );
      final double lastRowHeight = StrictGeomUtility.toExternalValue( sheetLayout.getRowHeight( contentRow ) );
      hssfRow.setHeightInPoints( (float) ( lastRowHeight ) );

      for ( int col = 0; col < colCount; col++ ) {
        final CellMarker.SectionType sectionType = contentProducer.getSectionType( contentRow, col );
        final RenderBox content = contentProducer.getContent( contentRow, col );

        if ( content == null ) {
          final RenderBox backgroundBox = contentProducer.getBackground( contentRow, col );
          final CellBackground background;
          if ( backgroundBox != null ) {
            background =
                cellBackgroundProducer.getBackgroundForBox( logicalPage, sheetLayout, col, contentRow, 1, 1, true,
                    sectionType, backgroundBox );
          } else {
            background = cellBackgroundProducer.getBackgroundAt( logicalPage, sheetLayout, col, contentRow, true, sectionType );
          }
          if ( background == null ) {
            if ( contentRow == 0 && col == 0 ) {
              // create a single cell, so that we dont run into nullpointer inside POI..
              getCellAt( col, contentRow );
            }
            // An empty cell .. ignore
            continue;
          }

          // A empty cell with a defined background ..
          final Cell cell = getCellAt( col, sheetRow );
          final CellStyle style = getCellStyleProducer().createCellStyle( null, null, background );
          if ( style != null ) {
            cell.setCellStyle( style );
          }
          continue;
        }

        if ( content.isCommited() == false ) {
          throw new InvalidReportStateException( "Uncommited content encountered" );
        }

        final long contentOffset = contentProducer.getContentOffset( contentRow, col );
        final TableRectangle rectangle =
            sheetLayout.getTableBounds( content.getX(), content.getY() + contentOffset, content.getWidth(), content
                .getHeight(), null );
        if ( rectangle.isOrigin( col, contentRow ) == false ) {
          // A spanned cell ..
          continue;
        }

        final CellBackground fastBackground =
            cellBackgroundProducer.getBackgroundForBox( logicalPage, sheetLayout, rectangle.getX1(), rectangle.getY1(),
                rectangle.getColumnSpan(), rectangle.getRowSpan(), false, sectionType, content );
        // export the cell and all content ..

        final Cell cell = getCellAt( col, sheetRow );
        final CellStyle style =
            getCellStyleProducer().createCellStyle( content.getInstanceId(), content.getStyleSheet(), fastBackground );
        if ( style != null ) {
          cell.setCellStyle( style );
        }

        if ( applyCellValue( content, cell, sheetLayout, rectangle, contentOffset ) ) {
          mergeCellRegion( rectangle, sheetRow, col, sheetLayout, logicalPage, content, contentProducer );
        }

        content.setFinishedTable( true );
      }
      sheetRow++;
    }

    if ( incremental == false ) {
      // cleanup ..
      sheet = null;
    }
  }

  private void mergeCellRegion( final TableRectangle rectangle, final int row, final int col,
      final SheetLayout sheetLayout, final LogicalPageBox logicalPage, final RenderBox content,
      final TableContentProducer contentProducer ) {
    if ( content == null ) {
      throw new NullPointerException();
    }

    final int rowSpan = rectangle.getRowSpan();
    final int columnSpan = rectangle.getColumnSpan();
    if ( rowSpan <= 1 && columnSpan <= 1 ) {
      return;
    }

    sheet.addMergedRegionUnsafe( new CellRangeAddress( row, ( row + rowSpan - 1 ), col, ( col + columnSpan - 1 ) ) );
    final int rectX = rectangle.getX1();
    final int rectY = rectangle.getY1();

    for ( int spannedRow = 0; spannedRow < rowSpan; spannedRow += 1 ) {
      for ( int spannedCol = 0; spannedCol < columnSpan; spannedCol += 1 ) {
        final CellMarker.SectionType sectionType = contentProducer.getSectionType( row, col );
        final CellBackground bg =
            cellBackgroundProducer.getBackgroundForBox( logicalPage, sheetLayout, rectX + spannedCol, rectY
                + spannedRow, 1, 1, false, sectionType, content );
        final Cell regionCell = getCellAt( ( col + spannedCol ), row + spannedRow );
        final CellStyle spannedStyle =
            getCellStyleProducer().createCellStyle( content.getInstanceId(), content.getStyleSheet(), bg );
        if ( spannedStyle != null ) {
          regionCell.setCellStyle( spannedStyle );
        }
      }
    }
  }

  /**
   * Applies the cell value and determines whether the cell should be merged. Merging will only take place if the cell
   * has a row or colspan greater than one. Images will never be merged, as image content is rendered into an anchored
   * frame on top of the cells.
   *
   * @param content
   * @param cell
   * @param sheetLayout
   * @param rectangle
   * @return true, if the cell may to be put into a merged region, false otherwise.
   */
  private boolean applyCellValue( final RenderBox content, final Cell cell, final SheetLayout sheetLayout,
      final TableRectangle rectangle, final long contentOffset ) {
    final Object value = textExtractor.compute( content );

    if ( handleImageValues( content, sheetLayout, rectangle, contentOffset, value ) ) {
      return false;
    }

    final String linkTarget = (String) content.getStyleSheet().getStyleProperty( ElementStyleKeys.HREF_TARGET );
    if ( linkTarget != null ) {
      // this may be wrong if we have quotes inside. We should escape them ..
      final RotatedTextDrawable extracted = RotatedTextDrawable.extract( value );
      final String linkText = extracted == null ? textExtractor.getText() : extracted.getText();
      final String formula =
          "HYPERLINK(" + splitAndQuoteExcelFormula( linkTarget ) + ","
              + splitAndQuoteExcelFormula( linkText ) + ")";
      if ( formula.length() < 1024 ) {
        cell.setCellFormula( formula );
        return true;
      }

      ExcelPrinter.logger
          .warn( "Excel-Cells cannot contain formulas longer than 1023 characters. Converting hyperlink into plain text" );
    }

    final Object attr1 =
        content.getAttributes().getAttribute( AttributeNames.Excel.NAMESPACE, AttributeNames.Excel.FIELD_FORMULA );
    if ( attr1 != null ) {
      final String formula = String.valueOf( attr1 );
      if ( formula.length() < 1024 ) {
        cell.setCellFormula( formula );
        return true;
      }

      ExcelPrinter.logger
          .warn( "Excel-Cells cannot contain formulas longer than 1023 characters. Converting excel formula into plain text" );
    }
    handleValueType( cell, value, workbook );
    return true;
  }

  private boolean handleImageValues( final RenderBox content, final SheetLayout sheetLayout,
      final TableRectangle rectangle, final long contentOffset, final Object value ) {

    if ( value instanceof RotatedTextDrawable ) {
      return false;
    }

    if ( value instanceof DrawableWrapper && ( (DrawableWrapper) value ).getBackend() instanceof RotatedTextDrawable ) {
      return false;
    }

    if ( value instanceof Image ) {
      try {
        final ImageContainer imageContainer = new DefaultImageReference( (Image) value );
        final StyleSheet rawSource = textExtractor.getRawSource().getStyleSheet();
        final StrictBounds contentBounds =
            new StrictBounds( content.getX(), content.getY() + contentOffset, content.getWidth(), content.getHeight() );
        createImageCell( rawSource, imageContainer, sheetLayout, rectangle, contentBounds );
      } catch ( IOException ioe ) {
        // Should not happen.
        ExcelPrinter.logger.warn( "Failed to process AWT-Image in Excel-Export", ioe );
      }
      return true;
    } else if ( value instanceof ImageContainer ) {
      final ImageContainer imageContainer = (ImageContainer) value;
      // todo: this is wrong ..
      final StyleSheet rawSource = textExtractor.getRawSource().getStyleSheet();
      final StrictBounds contentBounds =
          new StrictBounds( content.getX(), content.getY() + contentOffset, content.getWidth(), content.getHeight() );
      createImageCell( rawSource, imageContainer, sheetLayout, rectangle, contentBounds );
      return true;
    } else if ( value instanceof DrawableWrapper ) {
      final DrawableWrapper drawable = (DrawableWrapper) value;
      final RenderNode rawSource = textExtractor.getRawSource();
      final StrictBounds contentBounds =
          new StrictBounds( rawSource.getX(), rawSource.getY() + contentOffset, rawSource.getWidth(), rawSource
              .getHeight() );
      final ImageContainer imageFromDrawable =
          RenderUtility.createImageFromDrawable( drawable, contentBounds, content, getMetaData() );
      createImageCell( rawSource.getStyleSheet(), imageFromDrawable, sheetLayout, rectangle, contentBounds );
      return true;
    } else if ( value instanceof Shape ) {
      // We *could* do this as well ... but for now we dont.
      return true;
    }
    return false;
  }

  public void close() {
    final long start = System.currentTimeMillis();
    logger.info( "Closing workbook and writing content to disk." );
    if ( workbook != null ) {
      try {
        workbook.write( outputStream );
        // cleanup..
        sheet = null;
        outputStream.flush();
      } catch ( IOException e ) {
        ExcelPrinter.logger.warn( "could not write xls data. Message:", e );
      } finally {
        workbook = null;
      }
    }
    final long end = System.currentTimeMillis();
    logger.info( "Closing workbook finished in " + ( ( end - start ) / 1000f ) + "s" );

  }

  public Workbook getWorkbook() {
    return workbook;
  }

}
