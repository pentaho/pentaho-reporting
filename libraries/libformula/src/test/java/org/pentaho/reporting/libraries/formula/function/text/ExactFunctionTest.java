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

/**
 * @author Cedric Pronzato
 */
public class ExactFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "EXACT(\"A\";\"A\")", Boolean.TRUE },
        { "EXACT(\"A\";\"a\")", Boolean.FALSE },
        { "EXACT(1;1)", Boolean.TRUE },
        { "EXACT((1/3)*3;1)", Boolean.TRUE },
        { "EXACT(TRUE();TRUE())", Boolean.TRUE },
        { "EXACT(\"1\";2)", Boolean.FALSE },
        { "EXACT(\"h\";1)", Boolean.FALSE },
        { "EXACT(\"1\";1)", Boolean.TRUE },
        { "EXACT(\" 1\";1)", Boolean.FALSE },
        { "EXACT(\"12a 456 788\";\"12a 456 789\")", Boolean.FALSE },
      };
  }
}
