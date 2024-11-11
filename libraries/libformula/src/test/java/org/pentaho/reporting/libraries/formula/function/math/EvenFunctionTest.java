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


package org.pentaho.reporting.libraries.formula.function.math;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;

import java.math.BigDecimal;

/**
 * @author Cedric Pronzato
 */
public class EvenFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "EVEN(6)", new BigDecimal( 6 ) },
        { "EVEN(-4)", new BigDecimal( -4 ) },
        { "EVEN(1)", new BigDecimal( 2 ) },
        { "EVEN(0.3)", new BigDecimal( 2 ) },
        { "EVEN(-1)", new BigDecimal( -2 ) },
        { "EVEN(-0.3)", new BigDecimal( -2 ) },
        { "EVEN(0)", new BigDecimal( 0 ) },

        // test with border cases
        { "EVEN(0.05)", new BigDecimal( 2 ) },
        { "EVEN(2.05)", new BigDecimal( 4 ) },
        { "EVEN(3.05)", new BigDecimal( 4 ) },
        { "EVEN(4.05)", new BigDecimal( 6 ) },
        { "EVEN(5.0)", new BigDecimal( 6 ) },
        { "EVEN(6.0)", new BigDecimal( 6 ) },
        { "EVEN(7.95)", new BigDecimal( 8 ) },
        { "EVEN(8.95)", new BigDecimal( 10 ) },
        { "EVEN(-0.05)", new BigDecimal( -2 ) },
        { "EVEN(-2.05)", new BigDecimal( -4 ) },
        { "EVEN(-3.05)", new BigDecimal( -4 ) },
        { "EVEN(-4.05)", new BigDecimal( -6 ) },
        { "EVEN(-5.0)", new BigDecimal( -6 ) },
        { "EVEN(-6.0)", new BigDecimal( -6 ) },
        { "EVEN(-7.95)", new BigDecimal( -8 ) },
        { "EVEN(-8.95)", new BigDecimal( -10 ) },


      };
  }

}
