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

package org.pentaho.reporting.libraries.formula.function.userdefined;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;

import java.math.BigDecimal;

public class ArrayMidFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "NORMALIZEARRAY(ARRAYMID([.B3:.B7]; 3; 2))", new Object[]
          { new BigDecimal( 3 ), Boolean.TRUE } },
        { "NORMALIZEARRAY(ARRAYMID([.B3:.B7]; 1; 0))", new Object[] {} },
        { "NORMALIZEARRAY(ARRAYMID([.B3:.B7]; 1; 10))", new Object[]
          { "7", new BigDecimal( 2 ), new BigDecimal( 3 ), Boolean.TRUE, "Hello" } },
      };
  }

}
