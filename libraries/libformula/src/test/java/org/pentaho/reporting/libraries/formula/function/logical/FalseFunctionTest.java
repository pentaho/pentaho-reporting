/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.libraries.formula.function.logical;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;

import java.math.BigDecimal;

/**
 * @author Cedric Pronzato
 */
public class FalseFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }


  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "FALSE()", Boolean.FALSE },
        //{"IF(ISNUMBER(FALSE());FALSE()=0;FALSE())", Boolean.FALSE},
        { "2+FALSE()", new BigDecimal( 2 ) },

      };
  }
}
