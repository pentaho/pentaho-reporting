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
public class OddFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "ODD(5)", new BigDecimal( 5 ) },
        { "ODD(-5)", new BigDecimal( -5 ) },
        { "ODD(2)", new BigDecimal( 3 ) },
        { "ODD(0.3)", new BigDecimal( 1 ) },
        { "ODD(-2)", new BigDecimal( -3 ) },
        { "ODD(-0.3)", new BigDecimal( -1 ) },
        { "ODD(0)", new BigDecimal( 1 ) },

        { "ODD(0.0)", new BigDecimal( 1 ) },
        { "ODD(0.05)", new BigDecimal( 1 ) },
        { "ODD(0.95)", new BigDecimal( 1 ) },
        { "ODD(1.0)", new BigDecimal( 1 ) },
        { "ODD(1.05)", new BigDecimal( 3 ) },
        { "ODD(1.9)", new BigDecimal( 3 ) },
        { "ODD(2.0)", new BigDecimal( 3 ) },
        { "ODD(2.05)", new BigDecimal( 3 ) },
        { "ODD(2.95)", new BigDecimal( 3 ) },
        { "ODD(3.0)", new BigDecimal( 3 ) },
        { "ODD(3.05)", new BigDecimal( 5 ) },
        { "ODD(-0.05)", new BigDecimal( -1 ) },
        { "ODD(-0.95)", new BigDecimal( -1 ) },
        { "ODD(-1.0)", new BigDecimal( -1 ) },
        { "ODD(-1.05)", new BigDecimal( -3 ) },
        { "ODD(-1.9)", new BigDecimal( -3 ) },
        { "ODD(-2.0)", new BigDecimal( -3 ) },
        { "ODD(-2.05)", new BigDecimal( -3 ) },
        { "ODD(-2.95)", new BigDecimal( -3 ) },
        { "ODD(-3.0)", new BigDecimal( -3 ) },
        { "ODD(-3.05)", new BigDecimal( -5 ) },
      };
  }
}
