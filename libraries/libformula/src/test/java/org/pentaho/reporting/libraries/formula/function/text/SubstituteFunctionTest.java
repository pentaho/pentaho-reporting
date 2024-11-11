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

/**
 * @author Cedric Pronzato
 */
public class SubstituteFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "SUBSTITUTE(\"121212\";\"2\";\"ab\")", "1ab1ab1ab" },
        { "SUBSTITUTE(\"121212\";\"2\";\"ab\";2)", "121ab12" },
        { "SUBSTITUTE(\"Hello\";\"x\";\"ab\")", "Hello" },
        { "SUBSTITUTE(\"Annna\";\"nn\";\"N\";2)", "AnNa" },
        { "SUBSTITUTE(\"1212121\";\"2\";\"ab\")", "1ab1ab1ab1" },
      };
  }
}
