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


package org.pentaho.reporting.libraries.formula.function.information;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;

import java.math.BigDecimal;

/**
 * @author Cedric Pronzato
 */
public class CountAFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "COUNTA(\"1\";2;TRUE())", new BigDecimal( 3 ) },
        { "COUNTA([.B3:.B5])", new BigDecimal( 3 ) },
        { "COUNTA([.B3:.B5];[.B3:.B5])", new BigDecimal( 6 ) },
        { "COUNTA([.B3:.B9])", new BigDecimal( 6 ) },
        { "COUNTA(\"1\";2;1/0)", new BigDecimal( 3 ) },
        { "COUNTA(\"1\";2;SUM([.B3:.B9]))", new BigDecimal( 3 ) },
        { "COUNTA(\"1\";2;[.B3:.B9])", new BigDecimal( 8 ) },
        { "COUNTA({\"1\";2;[.B3:.B9]})", new BigDecimal( 8 ) },
        { "COUNTA({[.B3:.B9]})", new BigDecimal( 6 ) },
      };
  }

}
