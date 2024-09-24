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
public class SecondFunctionTest extends FormulaTestBase {
  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "SECOND(1/(24*60*60))", new BigDecimal( 1 ) },
        //TODO this testcase seems to be wrong as the definition is: round to the nearest integer which should be 0
        // {"SECOND(1/(24*60*60*2))", new BigDecimal(1)},
        { "SECOND(1/(24*60*60*4))", new BigDecimal( 0 ) },
      };
  }

  public void testDefault() throws Exception {
    runDefaultTest();
  }


}
