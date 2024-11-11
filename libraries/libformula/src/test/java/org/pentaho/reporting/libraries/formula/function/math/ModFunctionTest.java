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
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;

import java.math.BigDecimal;

/**
 * @author Cedric Pronzato
 */
public class ModFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "MOD(10;3)", new BigDecimal( 1 ) },
        { "MOD(2;8)", new BigDecimal( 2 ) },
        { "MOD(5.5;2.5)", new BigDecimal( 0.5 ) },
        { "MOD(-2;3)", new BigDecimal( 1 ) },
        { "MOD(2;-3)", new BigDecimal( -1 ) },
        { "MOD(-2;-3)", new BigDecimal( -2 ) },
        { "MOD(10;0)", LibFormulaErrorValue.ERROR_ARITHMETIC_VALUE },

        // custom tests
        { "MOD(40;50)", new BigDecimal( 40 ) },
        { "MOD(-40;50)", new BigDecimal( 10 ) },
        { "MOD(40;-50)", new BigDecimal( -10 ) },
        { "MOD(-40;-50)", new BigDecimal( -40 ) },
      };
  }


}
