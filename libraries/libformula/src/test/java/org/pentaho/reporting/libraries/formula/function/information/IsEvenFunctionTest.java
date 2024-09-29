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
public class IsEvenFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "ISEVEN(2)", Boolean.TRUE },
        { "ISEVEN(6)", Boolean.TRUE },
        { "ISEVEN(2.1)", Boolean.TRUE },
        { "ISEVEN(2.5)", Boolean.TRUE },
        { "ISEVEN(2.9)", Boolean.TRUE },
        { "ISEVEN(3)", Boolean.FALSE },
        { "ISEVEN(3.9)", Boolean.FALSE },
        { "ISEVEN(-2)", Boolean.TRUE },
        { "ISEVEN(-2.1)", Boolean.TRUE },
        { "ISEVEN(-2.5)", Boolean.TRUE },
        { "ISEVEN(-2.9)", Boolean.TRUE },
      };
  }

}
