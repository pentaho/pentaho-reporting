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


package org.pentaho.reporting.libraries.formula.function.text;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;

import java.math.BigDecimal;

/**
 * @author Cedric Pronzato
 */
public class FindFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "FIND(\"b\";\"abcabc\")", new BigDecimal( 2 ) },
        { "FIND(\"b\";\"abcabcabc\"; 3)", new BigDecimal( 5 ) },
        { "FIND(\"b\";\"ABC\";1)", LibFormulaErrorValue.ERROR_NOT_FOUND_VALUE },
        { "FIND(\"b\";\"bbbb\")", new BigDecimal( 1 ) },
        { "FIND(\"b\";\"bbbb\";2)", new BigDecimal( 2 ) },
        { "FIND(\"b\";\"bbbb\";2.9)", new BigDecimal( 2 ) },
        { "FIND(\"b\";\"bbbb\";0)", LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE },
        { "FIND(\"b\";\"bbbb\";0.9)", LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE },
      };
  }
}
