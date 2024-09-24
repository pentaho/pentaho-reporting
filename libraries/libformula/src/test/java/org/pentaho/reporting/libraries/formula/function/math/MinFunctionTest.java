/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2008 - 2019 Hitachi Vantara and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.libraries.formula.function.math;

import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.FormulaTestBase;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.common.TestFormulaContext;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;

import java.math.BigDecimal;

/**
 * @author Cedric Pronzato
 */
public class MinFunctionTest extends FormulaTestBase {
  private static final String FORMULA_NAME = "MIN";

  private FormulaContext context = new TestFormulaContext();

  /**
   * A list of valid values to be given to the function and the (expected) resulting value
   */
  private static final Object[][] VALID_EXAMPLES = {
    // Only numbers
    { BigDecimal.valueOf( -3 ), new Object[] { 0, 10, 2, -3, 1 } },
    { BigDecimal.valueOf( -10 ), new Object[] { -5, -10, -1 } },
    { BigDecimal.valueOf( -8 ), new Object[] { 2, 4, 1, -8 } },
    // Number as a string
    { BigDecimal.valueOf( 0.25 ), new Object[] { "0.25", "0.500" } },
    { BigDecimal.ZERO, new Object[] { "0", "0.0" } },
    { BigDecimal.ZERO, new Object[] { 0, 1, "2", 3, "4" } },
    // Logical values
    { BigDecimal.valueOf( -3 ), new Object[] { false, 10, 2, -3, true } },
    { BigDecimal.valueOf( -3 ), new Object[] { false, 0, -2, -3, true } },
    { BigDecimal.ZERO, new Object[] { 0.5, false, 10 } },
    { BigDecimal.ONE, new Object[] { 5.5, true, 10 } },
    // Number as a string and Logical values
    { BigDecimal.valueOf( -3 ), new Object[] { false, "10", 2, -3, 1 } },
    { BigDecimal.ZERO, new Object[] { false, "10", 2, true } },
    { BigDecimal.valueOf( -10 ), new Object[] { false, "-10", 2, true } },
    // Array
    { BigDecimal.ZERO, new Object[] { true, new Object[] { 0, 4, 3 }, "2" } }
  };

  public Object[][] createDataTest() {
    // Ignore this feature
    return null;
  }

  public void testValidExamples() throws Exception {
    for ( Object[] testValues : VALID_EXAMPLES ) {
      BigDecimal expectedResult = (BigDecimal) testValues[ 0 ];
      Object[] parameters = (Object[]) testValues[ 1 ];

      TypeValuePair res =
        evaluateFormula( getFormulaText( FORMULA_NAME, parameters ), context );
      assertNotNull( res );
      Object resValue = res.getValue();
      assertNotNull( resValue );
      assertTrue( resValue instanceof BigDecimal );
      assertEquals( expectedResult, resValue );
    }
  }

  public void testStringParameters_Numbers() throws Exception {
    TypeValuePair res = evaluateFormula( getFormulaText( FORMULA_NAME, new Object[] { "100", "-1", "0" } ), context );
    assertNotNull( res );
    Object resValue = res.getValue();
    assertNotNull( resValue );
    assertTrue( resValue instanceof BigDecimal );
    assertEquals( BigDecimal.valueOf( -1 ), resValue );
  }

  public void testStringParameters_NoNumbers() throws Exception {
    TypeValuePair res =
      evaluateFormula( getFormulaText( FORMULA_NAME, new Object[] { "abc", "def", "g", "" } ), context );
    assertNotNull( res );
    Object resValue = res.getValue();
    assertNotNull( resValue );
    assertEquals( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE, resValue );
  }

  public void testStringParameters_Mix() throws Exception {
    TypeValuePair res =
      evaluateFormula(
        getFormulaText( FORMULA_NAME, new Object[] { "xpto", "100", "2.5", -1, "alfa", 0, "+200", "-1.5" } ), context );
    assertNotNull( res );
    Object resValue = res.getValue();
    assertNotNull( resValue );
    assertEquals( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE, resValue );
  }

  public void testLogicalParameters() throws Exception {
    TypeValuePair res =
      evaluateFormula(
        getFormulaText( FORMULA_NAME, new Object[] { Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE } ),
        context );
    assertNotNull( res );
    Object resValue = res.getValue();
    assertNotNull( resValue );
    assertTrue( resValue instanceof BigDecimal );
    assertEquals( BigDecimal.ZERO, resValue );
  }

  public void testZeroParameters() throws Exception {
    TypeValuePair res = evaluateFormula( getFormulaText( FORMULA_NAME, null ), context );
    assertNotNull( res );
    Object resValue = res.getValue();
    assertNotNull( resValue );
    assertTrue( resValue instanceof BigDecimal );
    assertEquals( BigDecimal.ZERO, resValue );
  }

  public void testParameterNA() throws Exception {
    TypeValuePair res = evaluateFormula( "MIN(NA())", context );
    assertNotNull( res );
    Object resValue = res.getValue();
    assertNotNull( resValue );
    assertEquals( LibFormulaErrorValue.ERROR_NA_VALUE, resValue );
  }
}
