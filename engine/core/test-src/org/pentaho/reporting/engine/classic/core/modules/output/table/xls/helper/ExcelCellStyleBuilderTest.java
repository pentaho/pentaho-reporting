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
 *  Copyright (c) 2006 - 2015 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextRotation;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by dima.prokopenko@gmail.com on 9/13/2016.
 */
public class ExcelCellStyleBuilderTest {

  Workbook workbook = mock( Workbook.class );
  XSSFCellStyle xlsxStyle = mock( XSSFCellStyle.class );
  CellStyle xlsStyle = mock( CellStyle.class );

  Font font = mock( Font.class );

  HSSFCellStyleProducer.HSSFCellStyleKey styleKey = mock( HSSFCellStyleProducer.HSSFCellStyleKey.class );

  @Before
  public void beforeTest() {
  }

  @Test
  public void testRotationXlsSet() {
    when( workbook.createCellStyle() ).thenReturn( xlsxStyle );

    ExcelCellStyleBuilder builder = new ExcelCellStyleBuilder( workbook );

    builder.withRotation( TextRotation.D_90 );
    builder.build();
    verify( xlsxStyle, times( 1 ) ).setRotation( eq( (short) 90 ) );

    builder.withRotation( TextRotation.D_270 );
    builder.build();
    verify( xlsxStyle, times( 1 ) ).setRotation( eq( (short) 180 ) );
  }

  @Test
  public void testRotationXLSX() {
    when( workbook.createCellStyle() ).thenReturn( xlsStyle );
    ExcelCellStyleBuilder builder = new ExcelCellStyleBuilder( workbook );

    builder.withRotation( TextRotation.D_90 );
    builder.build();
    verify( xlsStyle, times( 1 ) ).setRotation( eq( (short) 90 ) );

    builder.withRotation( TextRotation.D_270 );
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

    verify( xlsStyle, times( 0 ) ).setAlignment( anyShort() );
    verify( xlsStyle, times( 0 ) ).setVerticalAlignment( anyShort() );
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

    when( styleKey.getHorizontalAlignment() ).thenReturn( (short) 13 );
    when( styleKey.getVerticalAlignment() ).thenReturn( (short) 14 );
    when( styleKey.isWrapText() ).thenReturn( true );
    when( workbook.getFontAt( anyShort() ) ).thenReturn( font );
    when( styleKey.getIndention() ).thenReturn( (short) 15 );

    when( styleKey.getDataStyle() ).thenReturn( (short) -1 );

    builder.withElementStyle( mock( StyleSheet.class ), styleKey );

    verify( xlsStyle, times( 1 ) ).setAlignment( eq( (short) 13 ) );
    verify( xlsStyle, times( 1 ) ).setVerticalAlignment( eq( (short) 14 ) );
    verify( xlsStyle, times( 1 ) ).setFont( any() );
    verify( xlsStyle, times( 1 ) ).setWrapText( eq( true ) );
    verify( xlsStyle, times( 1 ) ).setIndention( eq( (short) 15 ) );
    verify( xlsStyle, times( 0 ) ).setDataFormat( anyShort() );
  }
  
}
