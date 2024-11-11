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

/**
 * @author Cedric Pronzato
 */
public class MidFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "MID(\"123456789\";5;3)", "567" },
        { "MID(\"123456789\";20;3)", "" },
        { "MID(\"123456789\";-1;0)", LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE },
        { "MID(\"123456789\";1;0)", "" },
        { "MID(\"123456789\";2.9;1)", "2" },
        { "MID(\"123456789\";2;2.9)", "23" },

        // custom tests
        { "MID(\"123456789\";5;10)", "56789" },
        { "MID(\"123456789\";1;9)", "123456789" },
        { "MID(\"text\";2;2)", "ex" },
        { "MID(123456789;\"3\";4)", "3456" },
      };
  }

}
