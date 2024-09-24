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

package org.pentaho.reporting.libraries.formula.function.rounding;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;

import java.math.BigDecimal;

/**
 * @author Cedric Pronzato
 */
public class IntFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "INT(2)", new BigDecimal( 2 ) },
        { "INT(-3)", new BigDecimal( -3 ) },
        { "INT(1.2)", new BigDecimal( 1 ) },
        { "INT(1.7)", new BigDecimal( 1 ) },
        { "INT(-1.2)", new BigDecimal( -2 ) },
        { "INT((1/3)*3)", new BigDecimal( 1 ) },
      };
  }

}
