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
 *  Copyright (c) 2006 - 2024 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.pentaho.reporting.engine.classic.core.ImageContainer;
import org.pentaho.reporting.engine.classic.core.layout.model.PhysicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SlimSheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableRectangle;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.IntegerCache;
import org.pentaho.reporting.engine.classic.core.util.RotatedTextDrawable;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;

public abstract class ExcelPrinterBase {
  private static final Log logger = LogFactory.getLog( ExcelPrinterBase.class );
  /*
    Default MAX number of rows that can be printed to an Excel sheet
   */
  public static final int SHEET_ROW_LIMIT = 1048576;
  private final HashMap<String, Integer> sheetNamesCount;
  private Configuration config;
  private OutputProcessorMetaData metaData;
  private double scaleFactor;
  private InputStream templateInputStream;
  private ExcelColorProducer colorProducer;
  private ExcelColorProducer fontColorProducer;
  private boolean useXlsxFormat;
  private CellStyleProducer cellStyleProducer;
  private ExcelImageHandler imageHandler;
  private Drawing patriarch;
  private double maxSheetRowCount = SHEET_ROW_LIMIT;

  public ExcelPrinterBase() {
    this.sheetNamesCount = new HashMap<String, Integer>();
  }

  public boolean isUseXlsxFormat() {
    return useXlsxFormat;
  }

  public void setUseXlsxFormat( final boolean useXlsxFormat ) {
    this.useXlsxFormat = useXlsxFormat;
  }

  public void setMaxSheetRowCount( double rowCount ) {
    if ( rowCount > 0 && rowCount < SHEET_ROW_LIMIT ) {
      maxSheetRowCount = rowCount;
    } else {
      // Set the row limit to the Excel limit if the configuration used falls outside the normal limits
      maxSheetRowCount = SHEET_ROW_LIMIT;
    }
  }

  public double getMaxSheetRowCount() {
    return maxSheetRowCount;
  }

  public boolean isInitialized() {
    return metaData != null;
  }

  protected void init( final OutputProcessorMetaData metaData, final ResourceManager resourceManager ) {
    if ( metaData == null ) {
      throw new NullPointerException();
    }

    this.metaData = metaData;
    this.config = metaData.getConfiguration();
    this.imageHandler = new ExcelImageHandler( resourceManager, this );

    try {
      final String scaleFactorText =
          config
              .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.xls.CellWidthScaleFactor" );
      if ( scaleFactorText == null ) {
        scaleFactor = 50;
      } else {
        scaleFactor = Double.parseDouble( scaleFactorText );
      }
      applySheetRowLimitConfig( this.metaData );
    } catch ( Exception e ) {
      this.scaleFactor = 50;
    }
  }

  private void applySheetRowLimitConfig( OutputProcessorMetaData metaData ) {
    double sheetRowLimit = metaData.getNumericFeatureValue( OutputProcessorFeature.SHEET_ROW_LIMIT );
    setMaxSheetRowCount( sheetRowLimit );
  }

  public InputStream getTemplateInputStream() {
    return templateInputStream;
  }

  public void setTemplateInputStream( final InputStream templateInputStream ) {
    this.templateInputStream = templateInputStream;
  }

  protected String makeUnique( final String name ) {
    if ( name == null ) {
      throw new NullPointerException();
    }

    final Integer count = sheetNamesCount.get( name );
    if ( count == null ) {
      sheetNamesCount.put( name, IntegerCache.getInteger( 1 ) );
      return name;
    }

    final int value = count.intValue() + 1;
    sheetNamesCount.put( name, IntegerCache.getInteger( value ) );
    return makeUnique( name + ' ' + value );
  }

  protected boolean isValidSheetName( final String sheetname ) {
    if ( ( sheetname.indexOf( '/' ) > -1 ) || ( sheetname.indexOf( '\\' ) > -1 ) || ( sheetname.indexOf( '?' ) > -1 )
        || ( sheetname.indexOf( '*' ) > -1 ) || ( sheetname.indexOf( ']' ) > -1 ) || ( sheetname.indexOf( '[' ) > -1 )
        || ( sheetname.indexOf( ':' ) > -1 ) ) {
      return false;
    }

    return true;
  }

  protected Cell getCellAt( final int x, final int y ) {
    final Row row = getRowAt( y );
    final Cell cell = row.getCell( x );
    if ( cell != null ) {
      return cell;
    }
    return row.createCell( x );
  }

  protected Row getRowAt( final int y ) {
    Sheet sheet = getSheet();
    final Row row = sheet.getRow( y );
    if ( row != null ) {
      return row;
    }
    return sheet.createRow( y );
  }

  protected abstract Sheet getSheet();

  protected boolean isHeaderFooterValid( final String left, final String center, final String right ) {
    int length = 0;
    if ( left != null ) {
      length += left.length();
    }
    if ( center != null ) {
      length += center.length();
    }
    if ( right != null ) {
      length += right.length();
    }
    return length < 255;
  }

  public double getScaleFactor() {
    return scaleFactor;
  }

  public CellStyleProducer getCellStyleProducer() {
    return cellStyleProducer;
  }

  protected Workbook createWorkbook() {
    // Not opened yet. Lets do this now.
    if ( templateInputStream != null ) {
      // do some preprocessing ..
      try {
        final Workbook workbook = WorkbookFactory.create( templateInputStream );

        // OK, we have a workbook, but we can't stop here..
        final int sheetCount = workbook.getNumberOfSheets();
        for ( int i = 0; i < sheetCount; i++ ) {
          final String sheetName = workbook.getSheetName( i );
          // make sure that that name is marked as used ..
          makeUnique( sheetName );
        }

        return workbook;
      } catch ( IOException e ) {
        logger.warn( "Unable to read predefined xls-data.", e );
      }
    }
    if ( isUseXlsxFormat() ) {
      return new SXSSFWorkbook();
    } else {
      return new HSSFWorkbook();
    }
  }

  protected void initializeStyleProducers( final Workbook workbook ) {
    if ( workbook instanceof HSSFWorkbook ) {
      final boolean dynamicColors =
          "true".equals( config
              .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.xls.DynamicColors" ) );
      if ( dynamicColors ) {
        final HSSFWorkbook hssfWorkbook = (HSSFWorkbook) workbook;
        colorProducer = new CachingExcelColorSupport( new DynamicExcelColorProducer( hssfWorkbook ) );
      } else {
        colorProducer = new CachingExcelColorSupport( new StaticExcelColorSupport() );
      }
      fontColorProducer = colorProducer;
    } else {
      colorProducer = new XSSFExcelColorProducer();
      fontColorProducer = new CachingExcelColorSupport( new StaticExcelColorSupport() );
    }

    cellStyleProducer = createCellStyleProducer( workbook );
  }

  protected CellStyleProducer createCellStyleProducer( final Workbook workbook ) {
    final boolean hardLimit =
        "true".equals( getConfig().getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.modules.output.table.xls.HardStyleCountLimit" ) );
    return new HSSFCellStyleProducer( workbook, hardLimit, colorProducer, fontColorProducer );
  }

  protected Sheet openSheet( final String sheetName ) {
    patriarch = null;

    Workbook workbook = getWorkbook();
    if ( sheetName == null ) {
      return workbook.createSheet();
    } else {
      final String uniqueSheetname = makeUnique( sheetName );
      if ( uniqueSheetname.length() == 0 || uniqueSheetname.length() > 31 ) {
        logger.warn( "A sheet name must not be empty and greater than 31 characters" );
        return workbook.createSheet();
      } else if ( isValidSheetName( uniqueSheetname ) == false ) {
        logger.warn( "A sheet name must not contain any of ':/\\*?[]'" );
        // OpenOffice is even more restrictive and only allows Letters,
        // Digits, Spaces and the Underscore
        return workbook.createSheet();
      } else {
        return workbook.createSheet( uniqueSheetname );
      }
    }
  }

  protected void configureSheetProperties( final Sheet sheet, final SheetPropertySource excelTableContentProducer ) {
    final String pageHeaderCenter = excelTableContentProducer.getPageHeaderCenter();
    final String pageFooterCenter = excelTableContentProducer.getPageFooterCenter();
    final String pageHeaderLeft = excelTableContentProducer.getPageHeaderLeft();
    final String pageFooterLeft = excelTableContentProducer.getPageFooterLeft();
    final String pageHeaderRight = excelTableContentProducer.getPageHeaderRight();
    final String pageFooterRight = excelTableContentProducer.getPageFooterRight();

    if ( isHeaderFooterValid( pageHeaderLeft, pageHeaderCenter, pageHeaderRight ) ) {
      if ( pageHeaderLeft != null ) {
        sheet.getHeader().setLeft( pageHeaderLeft );
      }
      if ( pageHeaderCenter != null ) {
        sheet.getHeader().setCenter( pageHeaderCenter );
      }
      if ( pageHeaderRight != null ) {
        sheet.getHeader().setRight( pageHeaderRight );
      }
    } else {
      logger
          .warn( "Page-Header exceeds the maximum length of 255 characters. No page-header will be added to the sheet." );
    }
    if ( isHeaderFooterValid( pageFooterLeft, pageFooterCenter, pageFooterRight ) ) {
      if ( pageFooterCenter != null ) {
        sheet.getFooter().setCenter( pageFooterCenter );
      }
      if ( pageFooterLeft != null ) {
        sheet.getFooter().setLeft( pageFooterLeft );
      }
      if ( pageFooterRight != null ) {
        sheet.getFooter().setRight( pageFooterRight );
      }
    } else {
      logger
          .warn( "Page-Footer exceeds the maximum length of 255 characters. No page-footer will be added to the sheet." );
    }

    int sheetFreezeTop = excelTableContentProducer.getFreezeTop();
    int sheetFreezeLeft = excelTableContentProducer.getFreezeLeft();
    if ( sheetFreezeTop > 0 || sheetFreezeLeft > 0 ) {
      sheet.createFreezePane( sheetFreezeLeft, sheetFreezeTop );
    }
  }

  protected void configureSheetPaperSize( final Sheet sheet, final PhysicalPageBox page ) {
    Configuration config = getConfig();
    final String paper =
        config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.xls.Paper" );
    final String orientation =
        config
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.xls.PaperOrientation" );
    final short scale =
        (short) ParserUtil
            .parseInt(
                config
                    .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.xls.PrintScaleFactor" ),
                100 );
    final short hres =
        (short) ParserUtil
            .parseInt(
                config
                    .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.xls.PrintHorizontalResolution" ),
                -1 );
    final short vres =
        (short) ParserUtil
            .parseInt(
                config
                    .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.xls.PrintVerticalResolution" ),
                -1 );
    final boolean noColors =
        "true".equals( config
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.xls.PrintNoColors" ) );
    final boolean notes =
        "true".equals( config
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.xls.PrintNotes" ) );
    final boolean usePage =
        "true".equals( config
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.xls.PrintUsePage" ) );
    final boolean draft =
        "true".equals( config
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.xls.PrintDraft" ) );

    final PrintSetup printSetup = sheet.getPrintSetup();
    ExcelPrintSetupFactory.performPageSetup( printSetup, page, paper, orientation );
    printSetup.setScale( scale );
    printSetup.setNoColor( noColors );
    printSetup.setNotes( notes );
    printSetup.setUsePage( usePage );
    if ( hres > 0 ) {
      printSetup.setHResolution( hres );
    }
    if ( vres > 0 ) {
      printSetup.setVResolution( vres );
    }
    printSetup.setDraft( draft );

    final boolean displayGridLines =
        "true"
            .equals( config
                .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.xls.GridLinesDisplayed" ) );
    final boolean printGridLines =
        "true"
            .equals( config
                .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.xls.GridLinesPrinted" ) );
    sheet.setDisplayGridlines( displayGridLines );
    sheet.setPrintGridlines( printGridLines );
  }

  protected void configureSheetColumnWidths( Sheet sheet, SlimSheetLayout sheetLayout, int columnCount ) {
    // Set column widths ..
    for ( int col = 0; col < columnCount; col++ ) {
      final double cellWidth = StrictGeomUtility.toExternalValue( sheetLayout.getCellWidth( col, col + 1 ) );
      final double poiCellWidth = ( cellWidth * getScaleFactor() );
      sheet.setColumnWidth( col, Math.min( 255 * 256, (int) poiCellWidth ) );
    }
  }

  public abstract Workbook getWorkbook();

  protected ExcelColorProducer getColorProducer() {
    return colorProducer;
  }

  protected ExcelColorProducer getFontColorProducer() {
    return fontColorProducer;
  }

  protected Configuration getConfig() {
    return config;
  }

  public OutputProcessorMetaData getMetaData() {
    return metaData;
  }

  protected static String splitAndQuoteExcelFormula( final String s ) {
    final StringBuilder b = new StringBuilder();
    b.append( '"' );
    final char[] chars = s.toCharArray();
    int count = 0;
    for ( int i = 0; i < chars.length; i++ ) {
      final char c = chars[i];
      if ( c == '"' ) {
        b.append( '"' );
        b.append( '"' );
        count += 2;
      } else {
        b.append( c );
        count += 1;
      }
      if ( count > 252 ) {
        count = 0;
        b.append( "\" & \"" );
      }
    }
    b.append( '"' );
    return b.toString();
  }

  public Drawing getDrawingPatriarch() {
    if ( patriarch == null ) {
      patriarch = getSheet().createDrawingPatriarch();
    }
    return patriarch;
  }

  protected void createImageCell( final StyleSheet rawSource, final ImageContainer imageContainer,
      final SlimSheetLayout sheetLayout, final TableRectangle rectangle, final StrictBounds contentBounds ) {
    imageHandler.createImageCell( rawSource, imageContainer, sheetLayout, rectangle, contentBounds );
  }

  protected void handleValueType( final Cell cell, final Object value, final Workbook workbook ) {
    if ( value instanceof RichTextString ) {
      cell.setCellValue( (RichTextString) value );
    } else if ( value instanceof Date ) {
      cell.setCellValue( (Date) value );
    } else if ( value instanceof Number ) {
      final Number number = (Number) value;
      cell.setCellValue( number.doubleValue() );
    } else if ( value instanceof Boolean ) {
      cell.setCellValue( Boolean.TRUE.equals( value ) );
    } else if ( RotatedTextDrawable.extract( value ) != null ) {
      final RotatedTextDrawable rotatedTextDrawable = RotatedTextDrawable.extract( value );
      cell.setCellValue( rotatedTextDrawable.getText() );
    } else { // Something we can't handle.
      if ( value == null ) {
        cell.setCellValue( "" );
      } else {
        cell.setCellValue( String.valueOf( value ) );
      }
    }
  }

}
