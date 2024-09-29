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
public class IsErrorFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "ISERROR(1/0)", Boolean.TRUE },
        { "ISERROR(NA())", Boolean.TRUE },
        { "ISERROR(\"#N/A\")", Boolean.FALSE },
        { "ISERROR(1)", Boolean.FALSE },
        //{"ISERROR(CHOOSE(0; \"Apple\";\"Orange\"; \"Grape\";\"Perry\"))", Boolean.TRUE},
      };

  }
}
