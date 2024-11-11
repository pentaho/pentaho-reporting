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

package org.pentaho.reporting.engine.classic.core.function;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.function.formula.EnvFunction;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultProcessingContext;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterExpressionRuntime;
import org.pentaho.reporting.libraries.formula.CustomErrorValue;
import org.pentaho.reporting.libraries.formula.DefaultFormulaContext;
import org.pentaho.reporting.libraries.formula.Formula;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.FunctionRegistry;
import org.pentaho.reporting.libraries.formula.function.logical.IfFunction;
import org.pentaho.reporting.libraries.formula.function.logical.IfFunctionDescription;
import java.lang.reflect.Field;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FormulaExpressionTest {
  private FormulaExpression formulaExpression;

  @Before
  public void setup() {
    formulaExpression = new FormulaExpression();
  }

  @Test
  public void getValueWithFormulaErrorNoFailOnErrorTest() throws Exception {
    Field formulaError = FormulaExpression.class.getDeclaredField("formulaError");
    formulaError.setAccessible(true);
    formulaError.set(formulaExpression, new InvalidReportStateException());

    Field failOnError = FormulaExpression.class.getDeclaredField("failOnError");
    failOnError.setAccessible(true);
    failOnError.set(formulaExpression, false);

    assertEquals( LibFormulaErrorValue.ERROR_UNEXPECTED_VALUE, formulaExpression.getValue() );
  }

  @Test( expected = InvalidReportStateException.class )
  public void getValueWithFormulaErrorWithFailOnErrorTest() throws Exception {
    Field formulaError = FormulaExpression.class.getDeclaredField("formulaError");
    formulaError.setAccessible(true);
    formulaError.set(formulaExpression, new InvalidReportStateException());

    Field failOnError = FormulaExpression.class.getDeclaredField("failOnError");
    failOnError.setAccessible(true);
    failOnError.set(formulaExpression, true);

    formulaExpression.getValue();
  }

  @Test
  public void getValueWithNullFormulaTest() throws Exception {
    Field formulaError = FormulaExpression.class.getDeclaredField("formulaError");
    formulaError.setAccessible(true);
    formulaError.set(formulaExpression, null);

    Field failOnError = FormulaExpression.class.getDeclaredField("failOnError");
    failOnError.setAccessible(true);
    failOnError.set(formulaExpression, true);

    assertNull( formulaExpression.getValue() );
  }

  @Test
  public void getValueWithFormulaTest() throws Exception {
    Field formulaError = FormulaExpression.class.getDeclaredField("formulaExpression");
    formulaError.setAccessible(true);
    formulaError.set(formulaExpression, "IF([SomeParam] = \"Yes\";\"Ok\";\"Not Ok\")");

    Field failOnError = FormulaExpression.class.getDeclaredField("failOnError");
    failOnError.setAccessible(true);
    failOnError.set(formulaExpression, true);

    ParameterExpressionRuntime parameterExpressionRuntime = mock( ParameterExpressionRuntime.class );
    DefaultProcessingContext defaultProcessingContext = mock( DefaultProcessingContext.class );
    DefaultFormulaContext defaultFormulaContext = mock( DefaultFormulaContext.class );
    FunctionRegistry functionRegistry = mock( FunctionRegistry.class );
    DataRow dataRow = mock( DataRow.class );
    when( parameterExpressionRuntime.getProcessingContext() ).thenReturn( defaultProcessingContext );
    when( parameterExpressionRuntime.getDataRow() ).thenReturn( dataRow );
    when( dataRow.get( "SomeParam" ) ).thenReturn( "Yes" );
    when( defaultProcessingContext.getFormulaContext() ).thenReturn( defaultFormulaContext );
    when( defaultFormulaContext.getFunctionRegistry() ).thenReturn( functionRegistry );
    when( functionRegistry.createFunction( "IF" ) ).thenReturn( new IfFunction() );
    when( functionRegistry.getMetaData( "IF" ) ).thenReturn( new IfFunctionDescription() );
    formulaExpression.setRuntime( parameterExpressionRuntime );
    assertEquals( "Ok", formulaExpression.getValue() );
  }

  @Test( expected = InvalidReportStateException.class )
  public void getValueWithWrongFormulaErrorTest() throws Exception {
    //Wrong formula (with syntax error) to force an InvalidReportStateException
    Field formulaError = FormulaExpression.class.getDeclaredField("formulaExpression");
    formulaError.setAccessible(true);
    formulaError.set(formulaExpression, "IF(Wrong[SomeParam] = \"Yes\";\"Ok\";\"Not Ok\")" );

    Field failOnError = FormulaExpression.class.getDeclaredField("failOnError");
    failOnError.setAccessible(true);
    failOnError.set(formulaExpression, true);

    formulaExpression.getValue();
  }

  @Test( expected = InvalidReportStateException.class )
  public void getValueWithFormulaEvaluateErrorTest() throws Exception {
    Field formulaError = FormulaExpression.class.getDeclaredField("formulaExpression");
    formulaError.setAccessible(true);
    formulaError.set(formulaExpression, "IF(Wrong[SomeParam] = \"Yes\";\"Ok\";\"Not Ok\")" );

    Field failOnError = FormulaExpression.class.getDeclaredField("failOnError");
    failOnError.setAccessible(true);
    failOnError.set(formulaExpression, true);

    ParameterExpressionRuntime parameterExpressionRuntime = mock( ParameterExpressionRuntime.class );
    DefaultProcessingContext defaultProcessingContext = mock( DefaultProcessingContext.class );
    DefaultFormulaContext defaultFormulaContext = mock( DefaultFormulaContext.class );
    FunctionRegistry functionRegistry = mock( FunctionRegistry.class );
    DataRow dataRow = mock( DataRow.class );
    when( parameterExpressionRuntime.getProcessingContext() ).thenReturn( defaultProcessingContext );
    when( parameterExpressionRuntime.getDataRow() ).thenReturn( dataRow );
    when( dataRow.get( "SomeParam" ) ).thenReturn( "Yes" );
    when( defaultProcessingContext.getFormulaContext() ).thenReturn( defaultFormulaContext );
    when( defaultFormulaContext.getFunctionRegistry() ).thenReturn( functionRegistry );
    //Wrong Function Env Function with an IF formula expression to force an InvalidReportStateException
    when( functionRegistry.createFunction( "IF" ) ).thenReturn( new EnvFunction() );
    when( functionRegistry.getMetaData( "IF" ) ).thenReturn( new IfFunctionDescription() );
    formulaExpression.setRuntime( parameterExpressionRuntime );
    formulaExpression.getValue();
  }

  @Test
  public void getValueWithFormulaEvaluateCustomErrorTest() throws Exception {
    Field formulaError = FormulaExpression.class.getDeclaredField("formulaExpression");
    formulaError.setAccessible(true);
    formulaError.set(formulaExpression, "IF([SomeParam] = \"Yes\";\"Ok\";\"Not Ok\")" );

    Field failOnError = FormulaExpression.class.getDeclaredField("failOnError");
    failOnError.setAccessible(true);
    failOnError.set(formulaExpression, true);

    Formula custFormula = mock( Formula.class );
    Field compiledFormula = FormulaExpression.class.getDeclaredField("compiledFormula");
    compiledFormula.setAccessible(true);
    compiledFormula.set(formulaExpression, custFormula);

    CustomErrorValue expectedCustomErrorValue = new CustomErrorValue( "Custom Error" );
    when( custFormula.evaluate() ).thenReturn( expectedCustomErrorValue );
    ParameterExpressionRuntime parameterExpressionRuntime = mock( ParameterExpressionRuntime.class );
    DefaultProcessingContext defaultProcessingContext = mock( DefaultProcessingContext.class );
    DefaultFormulaContext defaultFormulaContext = mock( DefaultFormulaContext.class );
    FunctionRegistry functionRegistry = mock( FunctionRegistry.class );
    DataRow dataRow = mock( DataRow.class );
    when( parameterExpressionRuntime.getProcessingContext() ).thenReturn( defaultProcessingContext );
    when( parameterExpressionRuntime.getDataRow() ).thenReturn( dataRow );
    when( dataRow.get( "SomeParam" ) ).thenReturn( true );
    when( defaultProcessingContext.getFormulaContext() ).thenReturn( defaultFormulaContext );
    when( defaultFormulaContext.getFunctionRegistry() ).thenReturn( functionRegistry );
    when( functionRegistry.createFunction( "IF" ) ).thenReturn( new IfFunction() );
    when( functionRegistry.getMetaData( "IF" ) ).thenReturn( new IfFunctionDescription() );
    formulaExpression.setRuntime( parameterExpressionRuntime );
    assertEquals( expectedCustomErrorValue, formulaExpression.getValue() );
  }
}
