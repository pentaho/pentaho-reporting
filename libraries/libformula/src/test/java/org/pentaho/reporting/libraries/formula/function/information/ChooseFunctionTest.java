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
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;


/**
 * @author Cedric Pronzato
 */
public class ChooseFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "CHOOSE(1;\"1\";\"Orange\";\"Grape\";\"Perry\")", "1" },
        { "CHOOSE(3;\"Apple\";\"Orange\";\"Grape\";\"Perry\")", "Grape" },
        { "CHOOSE(0;\"Apple\";\"Orange\";\"Grape\";\"Perry\")", LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE },
        { "CHOOSE(5;\"Apple\";\"Orange\";\"Grape\";\"Perry\")", LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE },
        //        {"CHOOSE(2;SUM([.B4:.B5]);SUM([.B5]))", Boolean.FALSE},
        //        {"SUM(CHOOSE(2;[.B4:.B5];[.B5]))", Boolean.FALSE},
      };
  }

}
