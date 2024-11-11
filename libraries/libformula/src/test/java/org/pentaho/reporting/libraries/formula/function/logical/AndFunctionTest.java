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
public class AndFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "AND(FALSE();FALSE())", Boolean.FALSE },
        { "AND(FALSE();TRUE())", Boolean.FALSE },
        { "AND(TRUE();FALSE())", Boolean.FALSE },
        { "AND(TRUE();TRUE())", Boolean.TRUE },
        //define NA first {new Formula("AND(TRUE();NA())"), },
        { "AND(1;TRUE())", Boolean.TRUE },
        { "AND(0;TRUE())", Boolean.FALSE },
        { "AND(TRUE();TRUE();TRUE())", Boolean.TRUE },
        { "AND(TRUE())", Boolean.TRUE },
      };
  }

}
