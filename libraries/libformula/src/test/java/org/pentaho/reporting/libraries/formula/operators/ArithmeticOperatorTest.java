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

package org.pentaho.reporting.libraries.formula.operators;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;

import java.math.BigDecimal;

/**
 * Creation-Date: 10.04.2007, 15:31:58
 *
 * @author Thomas Morgner
 */
public class ArithmeticOperatorTest extends FormulaTestBase {

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "(1/3)*3", new BigDecimal( 1 ) },
        { "(5/24)*24", new BigDecimal( 5 ) },
      };
  }

  public ArithmeticOperatorTest() {
  }

  public ArithmeticOperatorTest( final String s ) {
    super( s );
  }

  public void testDefault() throws Exception {
    runDefaultTest();
  }

}
