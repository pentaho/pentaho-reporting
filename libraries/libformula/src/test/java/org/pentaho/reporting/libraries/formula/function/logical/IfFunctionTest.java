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

package org.pentaho.reporting.libraries.formula.function.logical;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;

import java.math.BigDecimal;

/**
 * @author Cedric Pronzato
 */
public class IfFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "IF(FALSE();7;8)", new BigDecimal( 8 ) },
        { "IF(TRUE();7;8)", new BigDecimal( 7 ) },
        { "IF(TRUE();\"HI\";8)", "HI" },
        { "IF(1;7;8)", new BigDecimal( 7 ) },
        { "IF(5;7;8)", new BigDecimal( 7 ) },
        { "IF(0;7;8)", new BigDecimal( 8 ) },
        { "IF(TRUE();[.B4];8)", new BigDecimal( 2 ) },
        { "IF(TRUE();[.B4]+5;8)", new BigDecimal( 7 ) },
        { "IF(\"x\";7;8)", LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE },
        { "IF(\"1\";7;8)", LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE },
        { "IF(\"\";7;8)", LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE },
        { "IF(FALSE();7)", Boolean.FALSE },
        { "IF(FALSE();7;)", Boolean.FALSE },
        { "IF(FALSE();;7)", new BigDecimal( 7 ) },
        //TODO { "IF(FALSE();7;)", new BigDecimal(0) }, we will not allow this syntax
        { "IF(TRUE();4;1/0)", new BigDecimal( 4 ) },
        { "IF(FALSE();1/0;5)", new BigDecimal( 5 ) },
      };
  }
}
