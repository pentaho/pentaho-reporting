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
public class IndexFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "INDEX({1;2;3 | 4;5;6 | 7;8;9}; 2; 2)", new BigDecimal( "5" ) },
        { "INDEX({1; 2; 3};;3)", new BigDecimal( "3" ) },
      };
  }

}
