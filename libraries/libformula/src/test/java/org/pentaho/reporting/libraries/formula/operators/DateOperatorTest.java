/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.formula.operators;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;

import java.math.BigDecimal;
import java.util.TimeZone;

/**
 * Creation-Date: 10.04.2007, 15:31:58
 *
 * @author Thomas Morgner
 */
public class DateOperatorTest extends FormulaTestBase {

  private TimeZone origTz;

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "DATETIMEVALUE(\"2009-10-10T17:15:00.00+0000\") + 0",
          new BigDecimal( "40094.7604166666666666666666666666666667" ) },
        { "DATETIMEVALUE(\"2009-10-10T17:30:00.00+0000\") - 0",
          new BigDecimal( "40094.7708333333333333333333333333333333" ) },
        { "DATETIMEVALUE(\"2009-10-10T17:30:00.00+0000\") - DATETIMEVALUE(\"2009-10-10T17:30:00.00+0000\")",
          new BigDecimal( 0 ) },
        { "DATETIMEVALUE(\"2009-10-10T17:30:00.00+0000\") - DATETIMEVALUE(\"2009-10-10T17:15:00.00+0000\")",
          new BigDecimal( "0.0104166666666666666666666666666667" ) },
      };
  }

  public DateOperatorTest() {
  }

  public DateOperatorTest( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    origTz = TimeZone.getDefault();
    TimeZone.setDefault( TimeZone.getTimeZone( "GMT+01:00" ) );
    super.setUp();
  }


  protected void tearDown() throws Exception {
    TimeZone.setDefault( origTz );
  }

  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public void testDefaultTZ() throws Exception {
    runDefaultTest();
  }


}
