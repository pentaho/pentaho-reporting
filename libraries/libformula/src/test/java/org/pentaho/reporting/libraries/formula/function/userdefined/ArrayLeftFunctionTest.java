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


package org.pentaho.reporting.libraries.formula.function.userdefined;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;

import java.math.BigDecimal;

public class ArrayLeftFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "NORMALIZEARRAY(ARRAYLEFT([.B3:.B7]; 2))", new Object[]
          { "7", new BigDecimal( 2 ), } },
        { "NORMALIZEARRAY(ARRAYLEFT([.B3:.B7]; 0))", new Object[] {} },
        { "NORMALIZEARRAY(ARRAYLEFT([.B3:.B7]; 10))", new Object[]
          { "7", new BigDecimal( 2 ), new BigDecimal( 3 ), Boolean.TRUE, "Hello" } },
      };
  }

}
