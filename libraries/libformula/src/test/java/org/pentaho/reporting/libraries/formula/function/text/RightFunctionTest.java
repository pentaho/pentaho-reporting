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
public class RightFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "RIGHT(\"Hello\";2)", "lo" },
        { "RIGHT(\"Hello\")", "o" },
        { "RIGHT(\"Hello\";20)", "Hello" },
        { "RIGHT(\"Hello\";0)", "" },
        { "RIGHT(\"Hello\";2^15-1)", "Hello" },
        { "RIGHT(\"\";4)", "" },
      };
  }
}
