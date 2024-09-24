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

package org.pentaho.reporting.libraries.formula.function.text;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;

import java.math.BigDecimal;

/**
 * @author Cedric Pronzato
 */
public class LeftFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "LEFT(\"Hello\";2)", "He" },
        { "LEFT(\"Hello\")", "H" },
        { "LEFT(\"Hello\";20)", "Hello" },
        { "LEFT(\"Hello\";0)", "" },
        { "LEFT(\"\";4)", "" },
        { "LEN(LEFT(\"xxx\";-0.1))", LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE },
        { "LEN(LEFT(\"xxx\";2^15-1))", new BigDecimal( 3 ) },
        { "LEFT(\"Hello\";2.9)", "He" },
      };
  }

}
