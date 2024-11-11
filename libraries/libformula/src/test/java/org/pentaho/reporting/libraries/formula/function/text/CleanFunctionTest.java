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
public class CleanFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "CLEAN(\"TEXT\")", "TEXT" },
        { "CLEAN(CHAR(7)& \"Tex\" & \"t\" & CHAR(8) & CHAR(9))", "Text" },
        { "CLEAN(\"Hi there\")", "Hi there" },
      };
  }
}
