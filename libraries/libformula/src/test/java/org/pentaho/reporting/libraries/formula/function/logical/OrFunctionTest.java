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


package org.pentaho.reporting.libraries.formula.function.logical;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;

/**
 * @author Cedric Pronzato
 */
public class OrFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "OR(FALSE();FALSE())", Boolean.FALSE },
        { "OR(FALSE();TRUE())", Boolean.TRUE },
        { "OR(TRUE();FALSE())", Boolean.TRUE },
        { "OR(TRUE();TRUE())", Boolean.TRUE },
        //TODO { "OR(FALSE();NA())",  },
        { "OR(FALSE();FALSE();TRUE())", Boolean.TRUE },
        { "OR(TRUE())", Boolean.TRUE },
      };
  }
}
