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

package org.pentaho.reporting.libraries.formula.function.datetime;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;

import java.math.BigDecimal;

/**
 * @author Cedric Pronzato
 */
public class MinuteFunctionTest extends FormulaTestBase {
  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "MINUTE(1/(24*60))", new BigDecimal( 1 ) }, // Changed as a result of PRD-5734 - should be the same as =MINUTE("00:01:00")
        { "MINUTE(TODAY()+1/(24*60))", new BigDecimal( 1 ) }, // Changed as a result of PRD-5734 - should be the same as =MINUTE("00:01:00")
        { "MINUTE(1/24)", new BigDecimal( 0 ) },
        { "MINUTE(TIME(11;37;05))", new BigDecimal( 37 ) },
        { "MINUTE(TIME(11;37;52))", new BigDecimal( 37 ) }, // No rounding ... PRD-5499
        { "MINUTE(TIME(00;00;59))", new BigDecimal( 0 ) },
        { "MINUTE(TIME(00;01;00))", new BigDecimal( 1 ) },
        { "MINUTE(TIME(00;01;59))", new BigDecimal( 1 ) },
        { "MINUTE(\"00:00:59\")", new BigDecimal( 0 ) },
        { "MINUTE(\"00:01:00\")", new BigDecimal( 1 ) },
        { "MINUTE(\"00:01:59\")", new BigDecimal( 1 ) },
        { "MINUTE(\"00:29:59\")", new BigDecimal( 29 ) },
        { "MINUTE(\"00:30:00\")", new BigDecimal( 30 ) },
        { "MINUTE(\"00:30:59\")", new BigDecimal( 30 ) },
        { "MINUTE(TIMEVALUE(\"00:00:59\"))", new BigDecimal( 0 ) },
        { "MINUTE(TIMEVALUE(\"00:01:00\"))", new BigDecimal( 1 ) },
        { "MINUTE(TIMEVALUE(\"00:01:59\"))", new BigDecimal( 1 ) },
        { "MINUTE(TIMEVALUE(\"00:29:59\"))", new BigDecimal( 29 ) },
        { "MINUTE(TIMEVALUE(\"00:30:00\"))", new BigDecimal( 30 ) },
        { "MINUTE(TIMEVALUE(\"00:30:59\"))", new BigDecimal( 30 ) },
        { "MINUTE(15/24/60/60+timevalue(\"00:30:00\"))", new BigDecimal( 30 ) }
      };
  }

  public void testDefault() throws Exception {
    runDefaultTest();
  }


}
