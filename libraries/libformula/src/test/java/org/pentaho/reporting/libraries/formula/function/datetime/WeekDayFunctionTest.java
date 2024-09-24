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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula.function.datetime;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;

import java.math.BigDecimal;

/**
 * @author Cedric Pronzato
 */
public class WeekDayFunctionTest extends FormulaTestBase {
  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "WEEKDAY(DATE(2006;5;21))", new BigDecimal( 1 ) },
        { "WEEKDAY(DATE(2005;1;1))", new BigDecimal( 7 ) },
        { "WEEKDAY(DATE(2005;1;1);1)", new BigDecimal( 7 ) },
        { "WEEKDAY(DATE(2005;1;1);2)", new BigDecimal( 6 ) },
        { "WEEKDAY(DATE(2005;1;1);3)", new BigDecimal( 5 ) }, };
  }

  private Number[][] createTypeDataTest() {
    return new Number[][]
      {
        { new BigDecimal( 1 ), new BigDecimal( 7 ), new BigDecimal( 6 ) },
        { new BigDecimal( 2 ), new BigDecimal( 1 ), new BigDecimal( 0 ) },
        { new BigDecimal( 3 ), new BigDecimal( 2 ), new BigDecimal( 1 ) },
        { new BigDecimal( 4 ), new BigDecimal( 3 ), new BigDecimal( 2 ) },
        { new BigDecimal( 5 ), new BigDecimal( 4 ), new BigDecimal( 3 ) },
        { new BigDecimal( 6 ), new BigDecimal( 5 ), new BigDecimal( 4 ) },
        { new BigDecimal( 7 ), new BigDecimal( 6 ), new BigDecimal( 5 ) },
      };
  }

  public void testAllTypes() {
    final WeekDayFunction function = new WeekDayFunction();
    final Number[][] dataTest = createTypeDataTest();
    for ( int i = 0; i < dataTest.length; i++ ) {
      final Number[] objects = dataTest[ i ];
      final Number type1 = objects[ 0 ];
      final Number type2 = objects[ 1 ];
      final Number type3 = objects[ 2 ];
      assertEquals( "Error with Type 1 conversion", function.convertType( type1.intValue(), 1 ), type1.intValue() );
      assertEquals( "Error with Type 2 conversion", function.convertType( type1.intValue(), 2 ), type2.intValue() );
      assertEquals( "Error with Type 3 conversion", function.convertType( type1.intValue(), 3 ), type3.intValue() );
    }
  }

  public void testDefault() throws Exception {
    runDefaultTest();
  }


}
