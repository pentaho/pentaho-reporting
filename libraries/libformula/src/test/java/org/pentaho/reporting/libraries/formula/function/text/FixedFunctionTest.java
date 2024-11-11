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

public class FixedFunctionTest extends FormulaTestBase {

  private FormulaContext context = new TestFormulaContext();

  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "FIXED(192.9614)", "192.961" },
        { "FIXED(192.9616)", "192.962" },
        { "FIXED(192.9616;3)", "192.962" },
        { "FIXED(192.9616;2)", "192.96" },
        { "FIXED(192.9616;2;TRUE())", "192.96" },
        { "FIXED(192.9616;2;FALSE())", "192.96" },
        { "FIXED(1192.9616;2;TRUE())", "1192.96" },
        { "FIXED(1192.9616;2;FALSE())", "1,192.96" },
        { "FIXED(1192.9666;2;TRUE())", "1192.97" },
        { "FIXED(1192.9666;2;FALSE())", "1,192.97" },
        // A parameter can be a String containing the actual number...
        // The function will provide the proper conversion
        { "FIXED(\"1192.9666\";2;TRUE())", "1192.97" },
        { "FIXED(1192.9666;\"2\";FALSE())", "1,192.97" }
      };
  }

  public void testWrongParameterNumber_Zero() {
    FixedFunction fixedFunction = new FixedFunction();

    try {
      fixedFunction.evaluate( context, getMockedParameterCallback() );

      fail( "Should not have reached this far; an Exception should have been raised!" );
    } catch ( EvaluationException e ) {
      // We know the actual error that should be referred by the exception, so let's check it!
      assertEquals( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE, e.getErrorValue() );
    }
  }

  public void testWrongParameterNumber_Four() {
    FixedFunction fixedFunction = new FixedFunction();

    try {
      fixedFunction.evaluate( context,
        getMockedParameterCallback( BigDecimal.ONE, BigDecimal.TEN, Boolean.FALSE, BigDecimal.TEN ) );

      fail( "Should not have reached this far; an Exception should have been raised!" );
    } catch ( EvaluationException e ) {
      // We know the actual error that should be referred by the exception, so let's check it!
      assertEquals( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE, e.getErrorValue() );
    }
  }

  public void testWrongParameterType_One() {
    FixedFunction fixedFunction = new FixedFunction();

    try {
      fixedFunction.evaluate( context,
        getMockedParameterCallback( "error", BigDecimal.ONE, Boolean.FALSE ) );

      fail( "Should not have reached this far; an Exception should have been raised!" );
    } catch ( EvaluationException e ) {
      // We know the actual error that should be referred by the exception, so let's check it!
      assertEquals( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE, e.getErrorValue() );
    }
  }

  public void testWrongParameterType_Two() {
    FixedFunction fixedFunction = new FixedFunction();

    try {
      fixedFunction.evaluate( context,
        getMockedParameterCallback( BigDecimal.ONE, "error", Boolean.FALSE ) );

      fail( "Should not have reached this far; an Exception should have been raised!" );
    } catch ( EvaluationException e ) {
      // We know the actual error that should be referred by the exception, so let's check it!
      assertEquals( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE, e.getErrorValue() );
    }
  }

  public void testWrongParameterType_Three() {
    FixedFunction fixedFunction = new FixedFunction();

    try {
      fixedFunction.evaluate( context,
        getMockedParameterCallback( BigDecimal.TEN, BigDecimal.ONE, "error" ) );

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
      when( parameter.getType( eq( i ) ) ).thenReturn( parameterType );
      when( parameter.getValue( eq( i ) ) ).thenReturn( parameterValue );
      ++i;
    }

    return parameter;
  }
}
