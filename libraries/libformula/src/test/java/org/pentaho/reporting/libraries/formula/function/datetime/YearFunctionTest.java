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


package org.pentaho.reporting.libraries.formula.function.datetime;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;

import java.math.BigDecimal;

/**
 * @author Cedric Pronzato
 */
public class YearFunctionTest extends FormulaTestBase {
  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "YEAR(DATE(1904;1;1))", new BigDecimal( 1904 ) },
      };
  }

  public void testDefault() throws Exception {
    runDefaultTest();
  }


}
