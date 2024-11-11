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

import java.math.BigDecimal;


/**
 * @author Cedric Pronzato
 */
public class CountFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "COUNT(1;2;3)", new BigDecimal( 3 ) },
        //            {"COUNT([.B4:.B5])", new BigDecimal(2)},
        //            {"COUNT([.B4:.B5];[.B4:.B5])", new BigDecimal(4)},
        //            {"COUNT([.B4:.B9])", new BigDecimal(2)},
        //            {"COUNT([.B4:.B8];1/0)", new BigDecimal(2)},
        //            {"COUNT([.B3:.B5])", new BigDecimal(2)},
      };
  }
}
