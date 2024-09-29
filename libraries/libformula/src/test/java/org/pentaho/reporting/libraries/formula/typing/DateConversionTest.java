/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


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
