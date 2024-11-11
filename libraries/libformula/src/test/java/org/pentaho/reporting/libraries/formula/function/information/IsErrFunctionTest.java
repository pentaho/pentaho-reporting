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


package org.pentaho.reporting.libraries.formula.function.information;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;


/**
 * @author Cedric Pronzato
 */
public class IsErrFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "ISERR(1/0)", Boolean.TRUE },
        { "ISERR(NA())", Boolean.FALSE },
        { "ISERR(\"#N/A\")", Boolean.FALSE },
        { "ISERR(1)", Boolean.FALSE },
      };
  }

}
