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
public class AverageFunctionTest extends FormulaTestBase {
  private static final String FORMULA_NAME = "AVERAGE";

  private FormulaContext context = new TestFormulaContext();

  /**
   * A list of valid values to be given to the function and the (expected) resulting value
   */
  private static final Object[][] VALID_EXAMPLES = {
    // Only numbers
    { BigDecimal.valueOf( 3 ), new Object[] { 0, 1, 5, 6 } },
    // Number as a string
    { BigDecimal.valueOf( 3 ), new Object[] { 0, 1, "5", 6 } },
    // Logical values
    { BigDecimal.valueOf( 3 ), new Object[] { false, true, 5, 6 } },
    // Number as a string and Logical values
    { BigDecimal.valueOf( 3 ), new Object[] { false, true, "5", 6 } }
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
    assertEquals( BigDecimal.valueOf( 33 ), resValue );
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
    assertEquals( BigDecimal.valueOf( ( 0 + 1 + 0 + 0 ) / 4.0 ), resValue );
  }

  public void testZeroParameters() throws Exception {
    TypeValuePair res = evaluateFormula( getFormulaText( FORMULA_NAME, null ), context );
    assertNotNull( res );
    Object resValue = res.getValue();
    assertNotNull( resValue );
    assertEquals( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE, resValue );
  }
}
