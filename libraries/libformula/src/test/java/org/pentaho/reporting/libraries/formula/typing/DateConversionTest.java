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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula.typing;

import junit.framework.AssertionFailedError;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.FormulaTestBase;
import org.pentaho.reporting.libraries.formula.util.DateUtil;
import org.pentaho.reporting.libraries.formula.util.HSSFDateUtil;

import java.math.BigDecimal;
import java.util.Date;

public class DateConversionTest extends FormulaTestBase {
  private static final Object[][] EMPTY_DATA = new Object[ 0 ][];

  public DateConversionTest() {
  }

  public DateConversionTest( final String s ) {
    super( s );
  }

  protected Object[][] createDataTest() {
    return EMPTY_DATA;
  }

  public void testDateConversion1904() {
    final FormulaContext context = getContext();
    final Date januaryFirst1904 = DateUtil.createDate( 1904, 1, 1, context.getLocalizationContext() );
    final Date januaryFirst1900 = DateUtil.createDate( 1900, 1, 1, context.getLocalizationContext() );
    final Date marchFirst1904 = DateUtil.createDate( 1904, 3, 1, context.getLocalizationContext() );
    final Date marchFirst1900 = DateUtil.createDate( 1900, 3, 1, context.getLocalizationContext() );

    // these numbers must match whatever OpenOffice computes ..
    assertEqual( HSSFDateUtil.getExcelDate( januaryFirst1900, false, HSSFDateUtil.computeZeroDate( "1904", false ) ),
      new BigDecimal( -1460 ) );
    assertEqual( HSSFDateUtil.getExcelDate( marchFirst1900, false, HSSFDateUtil.computeZeroDate( "1904", false ) ),
      new BigDecimal( -1401 ) );
    assertEqual( HSSFDateUtil.getExcelDate( januaryFirst1904, false, HSSFDateUtil.computeZeroDate( "1904", false ) ),
      new BigDecimal( 0 ) );
    assertEqual( HSSFDateUtil.getExcelDate( marchFirst1904, false, HSSFDateUtil.computeZeroDate( "1904", false ) ),
      new BigDecimal( 60 ) );

    assertEqual( HSSFDateUtil.getExcelDate( januaryFirst1900, true, HSSFDateUtil.computeZeroDate( "1904", true ) ),
      new BigDecimal( -1461 ) );
    assertEqual( HSSFDateUtil.getExcelDate( marchFirst1900, true, HSSFDateUtil.computeZeroDate( "1904", true ) ),
      new BigDecimal( -1401 ) );
    assertEqual( HSSFDateUtil.getExcelDate( januaryFirst1904, true, HSSFDateUtil.computeZeroDate( "1904", true ) ),
      new BigDecimal( 0 ) );
    assertEqual( HSSFDateUtil.getExcelDate( marchFirst1904, true, HSSFDateUtil.computeZeroDate( "1904", true ) ),
      new BigDecimal( 60 ) );

  }

  public void testDateConversion1900() {
    final FormulaContext context = getContext();
    final Date januaryFirst1904 = DateUtil.createDate( 1904, 1, 1, context.getLocalizationContext() );
    final Date januaryFirst1900 = DateUtil.createDate( 1900, 1, 1, context.getLocalizationContext() );
    final Date marchFirst1904 = DateUtil.createDate( 1904, 3, 1, context.getLocalizationContext() );
    final Date marchFirst1900 = DateUtil.createDate( 1900, 3, 1, context.getLocalizationContext() );

    // these numbers must match whatever OpenOffice computes ..
    assertEqual( HSSFDateUtil.getExcelDate( januaryFirst1900, false, HSSFDateUtil.computeZeroDate( "1900", false ) ),
      new BigDecimal( 0 ) );
    assertEqual( HSSFDateUtil.getExcelDate( marchFirst1900, false, HSSFDateUtil.computeZeroDate( "1900", false ) ),
      new BigDecimal( 59 ) );
    assertEqual( HSSFDateUtil.getExcelDate( januaryFirst1904, false, HSSFDateUtil.computeZeroDate( "1900", false ) ),
      new BigDecimal( 1460 ) );
    assertEqual( HSSFDateUtil.getExcelDate( marchFirst1904, false, HSSFDateUtil.computeZeroDate( "1900", false ) ),
      new BigDecimal( 1520 ) );

    assertEqual( HSSFDateUtil.getExcelDate( januaryFirst1900, true, HSSFDateUtil.computeZeroDate( "1900", true ) ),
      new BigDecimal( 0 ) );
    assertEqual( HSSFDateUtil.getExcelDate( marchFirst1900, true, HSSFDateUtil.computeZeroDate( "1900", true ) ),
      new BigDecimal( 60 ) );
    assertEqual( HSSFDateUtil.getExcelDate( januaryFirst1904, true, HSSFDateUtil.computeZeroDate( "1900", true ) ),
      new BigDecimal( 1461 ) );
    assertEqual( HSSFDateUtil.getExcelDate( marchFirst1904, true, HSSFDateUtil.computeZeroDate( "1900", true ) ),
      new BigDecimal( 1521 ) );

  }


  public void testDateConversion1899() {
    final FormulaContext context = getContext();
    final Date januaryFirst1904 = DateUtil.createDate( 1904, 1, 1, context.getLocalizationContext() );
    final Date januaryFirst1900 = DateUtil.createDate( 1900, 1, 1, context.getLocalizationContext() );
    final Date marchFirst1904 = DateUtil.createDate( 1904, 3, 1, context.getLocalizationContext() );
    final Date marchFirst1900 = DateUtil.createDate( 1900, 3, 1, context.getLocalizationContext() );

    // these numbers must match whatever OpenOffice computes ..
    assertEqual( HSSFDateUtil.getExcelDate( januaryFirst1900, false, HSSFDateUtil.computeZeroDate( "1899", false ) ),
      new BigDecimal( 2 ) );
    assertEqual( HSSFDateUtil.getExcelDate( marchFirst1900, false, HSSFDateUtil.computeZeroDate( "1899", false ) ),
      new BigDecimal( 61 ) );
    assertEqual( HSSFDateUtil.getExcelDate( januaryFirst1904, false, HSSFDateUtil.computeZeroDate( "1899", false ) ),
      new BigDecimal( 1462 ) );
    assertEqual( HSSFDateUtil.getExcelDate( marchFirst1904, false, HSSFDateUtil.computeZeroDate( "1899", false ) ),
      new BigDecimal( 1522 ) );

    assertEqual( HSSFDateUtil.getExcelDate( januaryFirst1900, true, HSSFDateUtil.computeZeroDate( "1899", true ) ),
      new BigDecimal( 2 ) );
    assertEqual( HSSFDateUtil.getExcelDate( marchFirst1900, true, HSSFDateUtil.computeZeroDate( "1899", true ) ),
      new BigDecimal( 62 ) );
    assertEqual( HSSFDateUtil.getExcelDate( januaryFirst1904, true, HSSFDateUtil.computeZeroDate( "1899", true ) ),
      new BigDecimal( 1463 ) );
    assertEqual( HSSFDateUtil.getExcelDate( marchFirst1904, true, HSSFDateUtil.computeZeroDate( "1899", true ) ),
      new BigDecimal( 1523 ) );

  }

  public void assertEqual( final BigDecimal expectedResult, final BigDecimal receivedResult ) {
    if ( expectedResult == null && receivedResult == null ) {
      return;
    }
    if ( expectedResult == null || receivedResult == null ) {
      throw new AssertionFailedError(
        "Assertation failed: Expected " + expectedResult + ", but got " + receivedResult );
    }

    if ( expectedResult.compareTo( receivedResult ) != 0 ) {
      throw new AssertionFailedError(
        "Assertation failed: Expected " + expectedResult + ", but got " + receivedResult );
    }
  }
}
