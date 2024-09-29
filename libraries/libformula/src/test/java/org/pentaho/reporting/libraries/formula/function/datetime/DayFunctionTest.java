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


package org.pentaho.reporting.libraries.formula.function.datetime;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;

import java.math.BigDecimal;

/**
 * @author Cedric Pronzato
 */
public class DayFunctionTest extends FormulaTestBase {
  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "DAY(DATE(2006;5;21))", new BigDecimal( 21 ) },
        // TODO signal this usecase as wrong { "DAY(\"2006-12-15\")", new Integer(12) }
        { "DAY(\"2006-12-15\")", new BigDecimal( 15 ) },
      };
  }

  public void testDefault() throws Exception {
    runDefaultTest();
  }

}
