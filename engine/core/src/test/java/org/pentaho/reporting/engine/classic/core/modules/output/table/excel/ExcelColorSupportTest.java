/*!
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
 * Copyright (c) 2002-2021 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.excel;

import junit.framework.TestCase;
import org.apache.poi.hssf.util.HSSFColor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper.StaticExcelColorSupport;

import java.awt.*;

public class ExcelColorSupportTest extends TestCase {
  // These colors are not mapped correctly #C6C3C6,#949694,#848284
  /*
   * 8 = new Color(0, 0, 0), 9 = new Color(255, 255, 255), 10 = new Color(255, 0, 0), 11 = new Color(0, 255, 0), 12 =
   * new Color(0, 0, 255), 13 = new Color(255, 255, 0), 14 = new Color(255, 0, 255), 15 = new Color(0, 255, 255), 16 =
   * new Color(128, 0, 0), 17 = new Color(0, 128, 0), 18 = new Color(0, 0, 128), 19 = new Color(128, 128, 0), 20 = new
   * Color(128, 0, 128), 21 = new Color(0, 128, 128), 22 = new Color(192, 192, 192), 23 = new Color(128, 128, 128), 24 =
   * new Color(153, 153, 255), 25 = new Color(153, 51, 102), 26 = new Color(255, 255, 204), 41 = new Color(204, 255,
   * 255), 28 = new Color(102, 0, 102), 29 = new Color(255, 128, 128), 30 = new Color(0, 102, 204), 31 = new Color(204,
   * 204, 255), 18 = new Color(0, 0, 128), 14 = new Color(255, 0, 255), 13 = new Color(255, 255, 0), 15 = new Color(0,
   * 255, 255), 20 = new Color(128, 0, 128), 16 = new Color(128, 0, 0), 21 = new Color(0, 128, 128), 12 = new Color(0,
   * 0, 255), 40 = new Color(0, 204, 255), 41 = new Color(204, 255, 255), 42 = new Color(204, 255, 204), 43 = new
   * Color(255, 255, 153), 44 = new Color(153, 204, 255), 45 = new Color(255, 153, 204), 46 = new Color(204, 153, 255),
   * 48 = new Color(51, 102, 255), 49 = new Color(51, 204, 204), 50 = new Color(153, 204, 0), 51 = new Color(255, 204,
   * 0), 52 = new Color(255, 153, 0), 53 = new Color(255, 102, 0), 54 = new Color(102, 102, 153), 55 = new Color(150,
   * 150, 150), 56 = new Color(0, 51, 102), 57 = new Color(51, 153, 102), 58 = new Color(0, 51, 0), 59 = new Color(51,
   * 51, 0), 60 = new Color(153, 51, 0), 61 = new Color(153, 51, 102), 62 = new Color(51, 51, 153), 63 = new Color(51,
   * 51, 51),
   */

  /**
   *
   */
  public ExcelColorSupportTest() {
  }

  public ExcelColorSupportTest( final String s ) {
    super( s );
  }

  public void testMapping() {
    System.out.println( "0xC6=" + 0xC6 );
    final Color c = new Color( 0xC6C3C6 );
    final StaticExcelColorSupport colorSupport = new StaticExcelColorSupport();
    final short nearestColor = colorSupport.getNearestColor( c );
    assertEquals( "Color: " + c + " -> " + colorSupport.getColor( nearestColor ).getHexString(), 22, nearestColor );
  }

  public void testMappingWhite() {
    final Color c = Color.WHITE;
    final StaticExcelColorSupport colorSupport = new StaticExcelColorSupport();
    final short nearestColor = colorSupport.getNearestColor( c );
    assertEquals( HSSFColor.HSSFColorPredefined.WHITE.getIndex(), nearestColor );
  }

  public void testMappingBlack() {
    final Color c = Color.BLACK;
    final StaticExcelColorSupport colorSupport = new StaticExcelColorSupport();
    final short nearestColor = colorSupport.getNearestColor( c );
    assertEquals( HSSFColor.HSSFColorPredefined.BLACK.getIndex(), nearestColor );
  }

}
