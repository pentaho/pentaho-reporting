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
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.fast.xls;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.DefaultImageReference;
import org.pentaho.reporting.engine.classic.core.ImageContainer;
import org.pentaho.reporting.engine.classic.core.PageDefinition;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.layout.model.PhysicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.RenderUtility;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.CellLayoutInfo;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.FastSheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.SheetPropertyCollector;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellBackground;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableRectangle;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper.CellStyleProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper.ExcelPrinterBase;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
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
import java.util.ArrayList;

@SuppressWarnings( "HardCodedStringLiteral" )
public class FastExcelPrinter extends ExcelPrinterBase {
  private static final Log logger = LogFactory.getLog( FastExcelPrinter.class );

  private Workbook workbook;
  private Sheet sheet;
  private PageDefinition pageDefinition;
  private int rowOffset;
  private long rowHeightOffset;
  private FastExcelTextExtractor textExtractor;
  private FastSheetLayout sheetLayout;
  private long[] cellHeights;

  public FastExcelPrinter( final SheetLayout sheetLayout ) {
    this.sheetLayout = new FastSheetLayout( sheetLayout );
  }

  public void init( final OutputProcessorMetaData metaData, final ResourceManager resourceManager,
      final ReportDefinition report ) {
    this.pageDefinition = report.getPageDefinition();
    super.init( metaData, resourceManager );
    workbook = createWorkbook();
    initializeStyleProducers( workbook );
    textExtractor =
        new FastExcelTextExtractor( getColorProducer(), getCellStyleProducer().getFontFactory(), workbook
            .getCreationHelper() );
  }

  protected Sheet getSheet() {
    return sheet;
  }

  public Workbook getWorkbook() {
    return workbook;
  }

  public void startSection( final Band band, final long[] cellHeights ) {
    this.cellHeights = cellHeights;
    this.sheetLayout.reinit( rowHeightOffset, cellHeights );

    if ( band.getComputedStyle().getBooleanStyleProperty( BandStyleKeys.PAGEBREAK_BEFORE ) ) {
      closeSheet();
    }

    if ( cellHeights.length > 0 && sheet == null ) {
      SheetPropertyCollector collector = new SheetPropertyCollector();
      String sheetName = collector.compute( band );
      sheet = openSheet( sheetName );
      configureSheetColumnWidths( sheet, sheetLayout, sheetLayout.getColumnCount() );
      configureSheetPaperSize( sheet, new PhysicalPageBox( pageDefinition.getPageFormat( 0 ), 0, 0 ) );
      configureSheetProperties( sheet, collector );
      rowOffset = 0;
    }

    for ( int r = 0; r < cellHeights.length; r += 1 ) {
      getRowAt( r + rowOffset ).setHeightInPoints( (float) StrictGeomUtility.toExternalValue( cellHeights[r] ) );
    }
  }

  public void endSection( final Band band, final ArrayList<CellLayoutInfo> backgroundCells ) {
    for ( final CellLayoutInfo layoutInfo : backgroundCells ) {
      int col = layoutInfo.getX1();
      int row = layoutInfo.getY1() + rowOffset;
      final Cell cell = getCellAt( col, row );
      final CellStyle style = getCellStyleProducer().createCellStyle( null, null, layoutInfo.getBackground() );
      if ( style != null ) {
        cell.setCellStyle( style );
      }

    }

    if ( band.getComputedStyle().getBooleanStyleProperty( BandStyleKeys.PAGEBREAK_AFTER ) ) {
      closeSheet();
    }

    this.rowOffset += cellHeights.length;
    for ( int i = 0; i < cellHeights.length; i++ ) {
      this.rowHeightOffset += cellHeights[i];
    }
  }

  protected CellStyleProducer createCellStyleProducer( final Workbook workbook ) {
    return new FastExcelCellStyleProducer( super.createCellStyleProducer( workbook ) );
  }

  public void closeSheet() {
    sheet = null;
  }

  public void print( final CellLayoutInfo tableRectangle, final ReportElement element, final ExpressionRuntime runtime )
    throws ContentProcessingException {
    TableRectangle rect = new TableRectangle();
    rect.setRect( tableRectangle.getX1(), tableRectangle.getY1() + rowOffset, tableRectangle.getX2(), tableRectangle
        .getY2()
        + rowOffset );

    Cell cellAt = getCellAt( rect.getX1(), rect.getY1() );
    CellBackground bg = tableRectangle.getBackground();
    CellStyle cellStyle =
        getCellStyleProducer().createCellStyle( element.getObjectID(), element.getComputedStyle(), bg );
    if ( cellStyle != null ) {
      cellAt.setCellStyle( cellStyle );
    }
    if ( applyCellValue( element, cellAt, rect, runtime ) ) {
      mergeCellRegion( rect, cellStyle );
    }
  }

  private void mergeCellRegion( final TableRectangle rectangle, final CellStyle spannedStyle ) {
    final int rowSpan = rectangle.getRowSpan();
    final int columnSpan = rectangle.getColumnSpan();
    if ( rowSpan <= 1 && columnSpan <= 1 ) {
      return;
    }

    int row = rectangle.getY1();
    int col = rectangle.getX1();

    sheet.addMergedRegion( new CellRangeAddress( row, ( row + rowSpan - 1 ), col, ( col + columnSpan - 1 ) ) );

    for ( int spannedRow = 0; spannedRow < rowSpan; spannedRow += 1 ) {
      for ( int spannedCol = 0; spannedCol < columnSpan; spannedCol += 1 ) {
        final Cell regionCell = getCellAt( ( col + spannedCol ), row + spannedRow );
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
   * @return true, if the cell may to be put into a merged region, false otherwise.
   */
  private boolean applyCellValue( final ReportElement content, final Cell cell, final TableRectangle rectangle,
      final ExpressionRuntime runtime ) throws ContentProcessingException {
    final Object value = textExtractor.compute( content, runtime );

    if ( handleImageValues( content, rectangle, value ) ) {
      return false;
    }

    final String linkTarget = (String) content.getComputedStyle().getStyleProperty( ElementStyleKeys.HREF_TARGET );
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

      logger
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

      logger
          .warn( "Excel-Cells cannot contain formulas longer than 1023 characters. Converting excel formula into plain text" );
    }

    handleValueType( cell, getValueIfVisible( content, value ), workbook );
    return true;
  }

  /**
   * Determines if the element is marked as hidden and returns either the original value or null if hidden. This happens
   * when an element is hidden and consumes space.
   *
   * @return The original value or null if the element is marked as hidden.
   */
  protected Object getValueIfVisible( final ReportElement content, final Object value ) {
    if ( content instanceof Element ) {
      SimpleStyleSheet style = ( (Element) content ).getComputedStyle();
      Boolean visible = (Boolean) style.getStyleProperty( ElementStyleKeys.VISIBLE, Boolean.TRUE );

      return visible ? value : null;
    }

    return value;
  }

  private boolean handleImageValues( final ReportElement content, final TableRectangle rectangle, final Object value ) {
    final StyleSheet rawSource = content.getComputedStyle();

    if ( RotatedTextDrawable.extract( value ) != null ) {
      return false;
    }

    if ( value instanceof Image ) {
      try {
        final StrictBounds contentBounds = sheetLayout.getBounds( rectangle );
        final ImageContainer imageContainer = new DefaultImageReference( (Image) value );
        createImageCell( rawSource, imageContainer, sheetLayout, rectangle, contentBounds );
      } catch ( final IOException ioe ) {
        // Should not happen.
        logger.warn( "Failed to process AWT-Image in Excel-Export", ioe );
      }
      return true;
    } else if ( value instanceof ImageContainer ) {
      final ImageContainer imageContainer = (ImageContainer) value;
      final StrictBounds contentBounds = sheetLayout.getBounds( rectangle );
      createImageCell( rawSource, imageContainer, sheetLayout, rectangle, contentBounds );
      return true;
    } else if ( value instanceof DrawableWrapper ) {
      final DrawableWrapper drawable = (DrawableWrapper) value;
      final StrictBounds contentBounds = sheetLayout.getBounds( rectangle );
      final ImageContainer imageFromDrawable =
          RenderUtility.createImageFromDrawable( drawable, contentBounds, content.getComputedStyle(), getMetaData() );
      createImageCell( rawSource, imageFromDrawable, sheetLayout, rectangle, contentBounds );
      return true;
    } else if ( value instanceof Shape ) {
      // We *could* do this as well ... but for now we dont.
      return true;
    }
    return false;
  }

  public void closeWorkbook( final OutputStream outputStream ) throws IOException {
    workbook.write( outputStream );
  }
}
