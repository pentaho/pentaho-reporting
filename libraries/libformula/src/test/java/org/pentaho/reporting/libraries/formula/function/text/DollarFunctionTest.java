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
 * Copyright (c) 2006 - 2024 Hitachi Vantara and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.libraries.formula.function.text;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.FormulaTestBase;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.common.TestFormulaContext;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.LogicalType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

import java.math.BigDecimal;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DollarFunctionTest extends FormulaTestBase {

  private FormulaContext context = new TestFormulaContext();

  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "DOLLAR(102.35;2)", "$102.35" },
        { "DOLLAR(102.354;2)", "$102.35" },
        { "DOLLAR(102.357;2)", "$102.36" },
        { "DOLLAR(102.354;3)", "$102.354" },
        { "DOLLAR(102.354;4)", "$102.3540" },
        { "DOLLAR(102.354)", "$102.35" },
        { "DOLLAR(102.357)", "$102.36" },
        { "DOLLAR(102)", "$102.00" },
        { "DOLLAR(0)", "$0.00" },
        // A parameter can be a String containing the actual number...
        // The function will provide the proper conversion
        { "DOLLAR(\"102.354\";3)", "$102.354" },
        { "DOLLAR(102.354;\"4\")", "$102.3540" },
      };
  }

  public void testWrongParameterNumber_Zero() {
    DollarFunction dollarFunction = new DollarFunction();

    try {
      dollarFunction.evaluate( context, getMockedParameterCallback() );

      fail( "Should not have reached this far; an Exception should have been raised!" );
    } catch ( EvaluationException e ) {
      // We know the actual error that should be referred by the exception, so let's check it!
      assertEquals( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE, e.getErrorValue() );
    }
  }

  public void testWrongParameterNumber_Three() {
    DollarFunction dollarFunction = new DollarFunction();

    try {
      dollarFunction.evaluate( context, getMockedParameterCallback( BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.TEN ) );

      fail( "Should not have reached this far; an Exception should have been raised!" );
    } catch ( EvaluationException e ) {
      // We know the actual error that should be referred by the exception, so let's check it!
      assertEquals( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE, e.getErrorValue() );
    }
  }

  public void testWrongParameterType_One() {
    DollarFunction dollarFunction = new DollarFunction();

    try {
      dollarFunction.evaluate( context,
        getMockedParameterCallback( "error", BigDecimal.ONE ) );

      fail( "Should not have reached this far; an Exception should have been raised!" );
    } catch ( EvaluationException e ) {
      // We know the actual error that should be referred by the exception, so let's check it!
      assertEquals( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE, e.getErrorValue() );
    }
  }

  public void testWrongParameterType_Two() {
    DollarFunction dollarFunction = new DollarFunction();

    try {
      dollarFunction.evaluate( context,
        getMockedParameterCallback( BigDecimal.TEN, "error" ) );

      fail( "Should not have reached this far; an Exception should have been raised!" );
    } catch ( EvaluationException e ) {
      // We know the actual error that should be referred by the exception, so let's check it!
      assertEquals( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE, e.getErrorValue() );
    }
  }

  private ParameterCallback getMockedParameterCallback( Object... parameterValues ) throws EvaluationException {
    ParameterCallback parameter = mock( ParameterCallback.class );
    when( parameter.getParameterCount() ).thenReturn( parameterValues.length );
    int i = 0;
    for ( Object parameterValue : parameterValues ) {
      Type parameterType = AnyType.TYPE;
      if ( parameterValue instanceof BigDecimal ) {
        parameterType = NumberType.GENERIC_NUMBER;
      } else if ( parameterValue instanceof Boolean ) {
        parameterType = LogicalType.TYPE;
      } else if ( parameterValue instanceof String ) {
        parameterType = TextType.TYPE;
      }
      when( parameter.getType( eq( i ) ) ).thenReturn( NumberType.GENERIC_NUMBER );
      when( parameter.getValue( eq( i ) ) ).thenReturn( parameterValue );
      ++i;
    }

    return parameter;
  }
}
