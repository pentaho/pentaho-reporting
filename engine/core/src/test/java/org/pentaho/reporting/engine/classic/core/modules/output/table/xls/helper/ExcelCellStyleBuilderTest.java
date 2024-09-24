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

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderEdge;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellBackground;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextRotation;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;

import java.awt.Color;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyShort;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by dima.prokopenko@gmail.com on 9/13/2016.
 */
public class ExcelCellStyleBuilderTest {

  private Workbook workbook = mock( Workbook.class );
  private XSSFCellStyle xlsxStyle = mock( XSSFCellStyle.class );
  private CellStyle xlsStyle = mock( CellStyle.class );

  private Font font = mock( Font.class );
  private HSSFCellStyleProducer.HSSFCellStyleKey styleKey = mock( HSSFCellStyleProducer.HSSFCellStyleKey.class );

  @Before
  public void beforeTest() {
  }

  @Test
  public void testRotationXlsSet() {
    when( workbook.createCellStyle() ).thenReturn( xlsxStyle );

    ExcelCellStyleBuilder builder = new ExcelCellStyleBuilder( workbook );

    StyleSheet element = mock( StyleSheet.class );
    when( element.getStyleProperty( eq( TextStyleKeys.TEXT_ROTATION ), any() ) ).thenReturn( TextRotation.D_90 );
    builder.withRotation( element );
    builder.build();
    verify( xlsxStyle, times( 1 ) ).setRotation( eq( (short) 90 ) );

    when( element.getStyleProperty( eq( TextStyleKeys.TEXT_ROTATION ), any() ) ).thenReturn( TextRotation.D_270 );
    builder.withRotation( element );
    builder.build();
    verify( xlsxStyle, times( 1 ) ).setRotation( eq( (short) 180 ) );
  }

  @Test
  public void testRotationXLSX() {
    when( workbook.createCellStyle() ).thenReturn( xlsStyle );
    ExcelCellStyleBuilder builder = new ExcelCellStyleBuilder( workbook );

    StyleSheet element = mock( StyleSheet.class );
    when( element.getStyleProperty( eq( TextStyleKeys.TEXT_ROTATION ), any() ) ).thenReturn( TextRotation.D_90 );
    builder.withRotation( element );
    builder.build();
    verify( xlsStyle, times( 1 ) ).setRotation( eq( (short) 90 ) );

    when( element.getStyleProperty( eq( TextStyleKeys.TEXT_ROTATION ), any() ) ).thenReturn( TextRotation.D_270 );
    builder.withRotation( element );
    builder.build();
    verify( xlsStyle, times( 1 ) ).setRotation( eq( (short) -90 ) );
  }

  @Test
  public void testNullRotation() {
    when( workbook.createCellStyle() ).thenReturn( xlsStyle );
    ExcelCellStyleBuilder builder = new ExcelCellStyleBuilder( workbook );

    builder.withRotation( null );
    builder.build();
    verify( xlsStyle, times( 0 ) ).setRotation( anyShort() );
  }

  @Test
  public void testNullElementSttle() {
    ExcelCellStyleBuilder builder = new ExcelCellStyleBuilder( workbook );

    when( workbook.createCellStyle() ).thenReturn( xlsStyle );
    builder.withElementStyle( null, styleKey );

    verify( xlsStyle, times( 0 ) ).setAlignment( any( HorizontalAlignment.class ) );
    verify( xlsStyle, times( 0 ) ).setVerticalAlignment( any( VerticalAlignment.class ) );
    verify( xlsStyle, times( 0 ) ).setFont( any() );
    verify( xlsStyle, times( 0 ) ).setWrapText( anyBoolean() );
    verify( xlsStyle, times( 0 ) ).setIndention( anyShort() );
    verify( xlsStyle, times( 0 ) ).setDataFormat( anyShort() );
  }

  @Test
  public void testElementStyleSet() {
    when( workbook.createCellStyle() ).thenReturn( xlsStyle );
    ExcelCellStyleBuilder builder = new ExcelCellStyleBuilder( workbook );
    when( workbook.createCellStyle() ).thenReturn( xlsStyle );

    when( styleKey.getHorizontalAlignment() ).thenReturn( HorizontalAlignment.CENTER );
    when( styleKey.getVerticalAlignment() ).thenReturn( VerticalAlignment.CENTER );
    when( styleKey.isWrapText() ).thenReturn( true );
    when( workbook.getFontAt( anyShort() ) ).thenReturn( font );
    when( styleKey.getIndention() ).thenReturn( (short) 15 );

    when( styleKey.getDataStyle() ).thenReturn( (short) -1 );

    builder.withElementStyle( mock( StyleSheet.class ), styleKey );

    verify( xlsStyle, times( 1 ) ).setAlignment( eq( HorizontalAlignment.CENTER ) );
    verify( xlsStyle, times( 1 ) ).setVerticalAlignment( eq( VerticalAlignment.CENTER ) );
    verify( xlsStyle, times( 1 ) ).setFont( any() );
    verify( xlsStyle, times( 1 ) ).setWrapText( eq( true ) );
    verify( xlsStyle, times( 1 ) ).setIndention( eq( (short) 15 ) );
    verify( xlsStyle, times( 0 ) ).setDataFormat( anyShort() );
  }

  @Test
  public void testBackgroundStyleNullSet() {
    ExcelCellStyleBuilder builder = spy( new ExcelCellStyleBuilder( workbook ) );
    builder.withBackgroundStyle( null, styleKey );

    // xls related
    verify( builder, times( 0 ) ).xls_backgroundStyle( any(), any() );
    //xlsxs related
    verify( builder, times( 0 ) ).xlsx_backgroundStyle( any(), any() );
  }

  @Test
  public void testCanHandleExcelImplementationXls() {
    when( workbook.createCellStyle() ).thenReturn( xlsStyle );
    ExcelCellStyleBuilder builder = spy( new ExcelCellStyleBuilder( workbook ) );

    CellBackground bg = getBackground();
    HSSFCellStyleProducer.HSSFCellStyleKey styleKey = getXlsKey();

    builder.withBackgroundStyle( bg, styleKey );

    verify( builder, times( 1 ) ).xls_backgroundStyle( eq( bg ), eq( styleKey ) );
    verify( builder, times( 0 ) ).xlsx_backgroundStyle( any(), any() );
  }

  @Test
  public void testCanHandleExcelImplementationXlsx() {
    when( workbook.createCellStyle() ).thenReturn( xlsxStyle );
    ExcelCellStyleBuilder builder = spy( new ExcelCellStyleBuilder( workbook ) );

    CellBackground bg = getBackground();
    HSSFCellStyleProducer.HSSFCellStyleKey key = getXlsxKey();

    builder.withBackgroundStyle( bg, key );

    verify( builder, times( 0 ) ).xls_backgroundStyle( any(), any() );
    verify( builder, times( 1 ) ).xlsx_backgroundStyle( eq( bg ), eq( key ) );
  }

  @Test
  public void testXls_BackgroundStyle() {
    when( workbook.createCellStyle() ).thenReturn( xlsStyle );
    ExcelCellStyleBuilder builder = new ExcelCellStyleBuilder( workbook );

    CellBackground bg = getBackground();
    HSSFCellStyleProducer.HSSFCellStyleKey styleKey = getXlsKey();

    builder.xls_backgroundStyle( bg, styleKey );

    verify( xlsStyle, times( 1 ) ).setBorderBottom( eq( BorderStyle.MEDIUM_DASH_DOT ) );
    verify( xlsStyle, times( 1 ) ).setBottomBorderColor( eq( (short) 116 ) );
    verify( xlsStyle, times( 1 ) ).setBorderTop( eq( BorderStyle.MEDIUM_DASHED ) );
    verify( xlsStyle, times( 1 ) ).setTopBorderColor( eq( (short) 118 ) );
    verify( xlsStyle, times( 1 ) ).setBorderLeft( eq( BorderStyle.MEDIUM_DASH_DOT_DOT ) );
    verify( xlsStyle, times( 1 ) ).setLeftBorderColor( eq( (short) 120 ) );
    verify( xlsStyle, times( 1 ) ).setBorderRight( eq( BorderStyle.MEDIUM ) );
    verify( xlsStyle, times( 1 ) ).setRightBorderColor( eq( (short) 122 ) );
    verify( xlsStyle, times( 1 ) ).setFillForegroundColor( eq( (short) 123 ) );
    verify( xlsStyle, times( 1 ) ).setFillPattern( eq( FillPatternType.SOLID_FOREGROUND ) );
  }

  private HSSFCellStyleProducer.HSSFCellStyleKey getXlsKey() {
    when( styleKey.getBorderStrokeBottom() ).thenReturn( BorderStyle.MEDIUM_DASH_DOT );
    when( styleKey.getColorBottom() ).thenReturn( (short) 116 );
    when( styleKey.getBorderStrokeTop() ).thenReturn( BorderStyle.MEDIUM_DASHED );
    when( styleKey.getColorTop() ).thenReturn( (short) 118 );
    when( styleKey.getBorderStrokeLeft() ).thenReturn( BorderStyle.MEDIUM_DASH_DOT_DOT );
    when( styleKey.getColorLeft() ).thenReturn( (short) 120 );
    when( styleKey.getBorderStrokeRight() ).thenReturn( BorderStyle.MEDIUM );
    when( styleKey.getColorRight() ).thenReturn( (short) 122 );
    when( styleKey.getColor() ).thenReturn( (short) 123 );
    return styleKey;
  }

  private CellBackground getBackground() {
    CellBackground bg = mock( CellBackground.class );
    BorderEdge bEdge = new BorderEdge( org.pentaho.reporting.engine.classic.core.style.BorderStyle.WAVE, Color.BLACK, (long) 13 );
    when( bg.getBottom() ).thenReturn( bEdge );
    when( bg.getTop() ).thenReturn( bEdge );
    when( bg.getLeft() ).thenReturn( bEdge );
    when( bg.getRight() ).thenReturn( bEdge );
    when( bg.getBackgroundColor() ).thenReturn( Color.BLACK );
    return bg;
  }

  @Test
  public void testXlsx_BackgroundStyle() {
    when( workbook.createCellStyle() ).thenReturn( xlsxStyle );
    ExcelCellStyleBuilder builder = new ExcelCellStyleBuilder( workbook );

    CellBackground bg = getBackground();
    HSSFCellStyleProducer.HSSFCellStyleKey styleKey = getXlsxKey();

    builder.withBackgroundStyle( bg, styleKey );

    verify( xlsxStyle, times( 1 ) ).setBorderBottom( eq( BorderStyle.DASH_DOT ) );
    verify( xlsxStyle, times( 1 ) )
      .setBorderColor( eq( XSSFCellBorder.BorderSide.BOTTOM ), notNull( XSSFColor.class ) );
    verify( xlsxStyle, times( 1 ) ).setBorderTop( eq( BorderStyle.DOTTED ) );
    verify( xlsxStyle, times( 1 ) ).setBorderColor( eq( XSSFCellBorder.BorderSide.TOP ), notNull( XSSFColor.class ) );
    verify( xlsxStyle, times( 1 ) ).setBorderLeft( eq( BorderStyle.DASH_DOT_DOT ) );
    verify( xlsxStyle, times( 1 ) ).setBorderColor( eq( XSSFCellBorder.BorderSide.LEFT ), notNull( XSSFColor.class ) );
    verify( xlsxStyle, times( 1 ) ).setBorderRight( eq( BorderStyle.DASHED ) );
    verify( xlsxStyle, times( 1 ) ).setBorderColor( eq( XSSFCellBorder.BorderSide.RIGHT ), notNull( XSSFColor.class ) );
    verify( xlsxStyle, times( 1 ) ).setFillForegroundColor( notNull( XSSFColor.class ) );
    verify( xlsxStyle, times( 1 ) ).setFillPattern( eq( FillPatternType.SOLID_FOREGROUND ) );
  }

  private HSSFCellStyleProducer.HSSFCellStyleKey getXlsxKey() {
    when( styleKey.getBorderStrokeBottom() ).thenReturn( BorderStyle.DASH_DOT );
    when( styleKey.getExtendedColorBottom() ).thenReturn( Color.BLACK );
    when( styleKey.getBorderStrokeTop() ).thenReturn( BorderStyle.DOTTED );
    when( styleKey.getExtendedColorTop() ).thenReturn( Color.BLACK );
    when( styleKey.getBorderStrokeLeft() ).thenReturn( BorderStyle.DASH_DOT_DOT );
    when( styleKey.getExtendedColorLeft() ).thenReturn( Color.BLACK );
    when( styleKey.getBorderStrokeLeft() ).thenReturn( BorderStyle.DASH_DOT_DOT );
    when( styleKey.getBorderStrokeRight() ).thenReturn( BorderStyle.DASHED );
    when( styleKey.getExtendedColorRight() ).thenReturn( Color.BLACK );
    when( styleKey.getExtendedColor() ).thenReturn( Color.BLACK );

    return styleKey;
  }
}
