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
 * Copyright (c) 2006 - 2019 Hitachi Vantara and Contributors.  All rights reserved.
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
public class SumFunctionTest extends FormulaTestBase {
  private static final String FORMULA_NAME = "SUM";

  private FormulaContext context = new TestFormulaContext();

  /**
   * A list of valid values to be given to the function and the (expected) resulting value
   */
  private static final Object[][] VALID_EXAMPLES = {
    // Only numbers
    { BigDecimal.TEN, new Object[] { 0, 1, 2, 3, 4 } },
    // Number as a string
    { BigDecimal.TEN, new Object[] { 0, 1, 2, 3, 4 } },
    // Logical values
    { BigDecimal.TEN, new Object[] { false, true, 2, 3, 4 } },
    // Number as a string and Logical values
    { BigDecimal.TEN, new Object[] { false, true, "2", 3, "4" } },
    // Array
    { BigDecimal.TEN, new Object[] { false, new Object[] { 1, 2, 3 }, "4" } }
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

  public void testZeroParameters() throws Exception {
    TypeValuePair res = evaluateFormula( getFormulaText( FORMULA_NAME, null ), context );
    assertNotNull( res );
    Object resValue = res.getValue();
    assertNotNull( resValue );
    assertEquals( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE, resValue );
  }
}
