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
public class TimeFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }


  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "0.9999884259259259259259259259259259259*27", new BigDecimal( "26.9996875000000000000" ) },
        { "TIME(0;0;0)+0", new BigDecimal( 0 ) },
        { "TIME(23;59;59)*60*60*24", new BigDecimal( 86399 ) },
            /*{ "", Boolean.TRUE },
                    { "", Boolean.TRUE },
                    { "", Boolean.TRUE },
                    { "", Boolean.TRUE },
                    { "", Boolean.TRUE },
                    { "", Boolean.TRUE },
                    { "", Boolean.TRUE },
                    { "", Boolean.TRUE },
                    { "", Boolean.TRUE },
            */
      };
  }
}
