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

/**
 * Creation-Date: 10.04.2007, 15:31:58
 *
 * @author Thomas Morgner
 */
public class EqualOperatorTest extends FormulaTestBase {

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "1=\"1\"", Boolean.TRUE },
        { "2=2.0", Boolean.TRUE },
        { "\"2004-01-01\"=DATE(2004; 1; 1)", Boolean.TRUE }, // comparing values of different types should yield 'false'
      };
  }

  public EqualOperatorTest() {
  }

  public EqualOperatorTest( final String s ) {
    super( s );
  }

  public void testDefault() throws Exception {
    runDefaultTest();
  }

}
