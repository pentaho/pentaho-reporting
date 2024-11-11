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


package org.pentaho.reporting.libraries.formula.function.logical;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;

/**
 * @author Cedric Pronzato
 */
public class XorFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "XOR(FALSE();FALSE())", Boolean.FALSE },
        { "XOR(FALSE();TRUE())", Boolean.TRUE },
        { "XOR(TRUE();FALSE())", Boolean.TRUE },
        { "XOR(TRUE();TRUE())", Boolean.FALSE },
        //TODO { "XOR(FALSE();NA())", Boolean. },
        { "XOR(FALSE();FALSE();TRUE())", Boolean.TRUE },
        { "XOR(FALSE();TRUE();TRUE())", Boolean.FALSE },
        { "XOR(TRUE())", Boolean.TRUE },
      };
  }
}
