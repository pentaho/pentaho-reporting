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


package org.pentaho.reporting.libraries.formula.operators;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;

import java.math.BigDecimal;

/**
 * Creation-Date: 11.26.2007, 15:31:58
 *
 * @author David Kincade
 */
public class PercentageOperatorTest extends FormulaTestBase {
  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "0.25%", new BigDecimal( "0.0025" ) },
        { "25%", new BigDecimal( "0.25" ) },
        { "125%", new BigDecimal( "1.25" ) },
      };
  }

  public void testDefault() throws Exception {
    runDefaultTest();
  }

}
