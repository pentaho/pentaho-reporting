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

/**
 * @author Cedric Pronzato
 */
public class IsNonTextFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "ISNONTEXT(1)", Boolean.TRUE },
        { "ISNONTEXT(TRUE())", Boolean.TRUE },
        { "ISNONTEXT(\"1\")", Boolean.FALSE },
        { "ISNONTEXT([.B7])", Boolean.FALSE },
        { "ISNONTEXT([.B9])", Boolean.TRUE },
        { "ISNONTEXT([.B8])", Boolean.TRUE },
      };
  }

}
