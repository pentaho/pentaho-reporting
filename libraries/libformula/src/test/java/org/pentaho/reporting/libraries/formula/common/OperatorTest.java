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


package org.pentaho.reporting.libraries.formula.common;

import junit.framework.Assert;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaTestBase;

import java.math.BigDecimal;

public class OperatorTest extends FormulaTestBase {
  protected Object[][] createDataTest() {
    return new Object[][]
      {
        { "1 + 1", new BigDecimal( 2 ) },
        { "1 - 1", new BigDecimal( 0 ) },
        { "1 = 1", Boolean.TRUE },
        { "1 <> 1", Boolean.FALSE },
        { "1 < 1", Boolean.FALSE },
        { "1 > 1", Boolean.FALSE },
        { "1 >= 1", Boolean.TRUE },
        { "1 <= 1", Boolean.TRUE },
        { "1 * 1", new BigDecimal( 1 ) },
        { "1 ^ 1", new BigDecimal( 1.0 ) },
        { "1 / 1", new BigDecimal( 1 ) },
        { "1%", new BigDecimal( "0.01" ) },
        { "0.1%", new BigDecimal( "0.001" ) } };
  }

  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public void testRangeOperator() throws EvaluationException {
    final Object resolveReference = getContext().resolveReference( ".B4:.B5" );
    Assert.assertNotNull( "Reference should not be null", resolveReference );
  }
}
