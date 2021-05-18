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
 * Copyright (c) 2001 - 2021 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */
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

import static org.junit.Assert.assertNull;
import static org.powermock.reflect.Whitebox.setInternalState;
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
  public void getValueWithFormulaErrorNoFailOnErrorTest() {
    setInternalState( formulaExpression, "formulaError", new InvalidReportStateException() );
    setInternalState( formulaExpression, "failOnError", false );
    assertEquals( LibFormulaErrorValue.ERROR_UNEXPECTED_VALUE, formulaExpression.getValue() );
  }

  @Test( expected = InvalidReportStateException.class )
  public void getValueWithFormulaErrorWithFailOnErrorTest() {
    setInternalState( formulaExpression, "formulaError", new InvalidReportStateException() );
    setInternalState( formulaExpression, "failOnError", true );
    formulaExpression.getValue();
  }

  @Test
  public void getValueWithNullFormulaTest() {
    setInternalState( formulaExpression, "formulaError", (String) null );
    setInternalState( formulaExpression, "failOnError", true );
    assertNull( formulaExpression.getValue() );
  }

  @Test
  public void getValueWithFormulaTest() {
    setInternalState( formulaExpression, "formulaExpression", "IF([SomeParam] = \"Yes\";\"Ok\";\"Not Ok\")" );
    setInternalState( formulaExpression, "failOnError", true );
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
  public void getValueWithWrongFormulaErrorTest() {
    //Wrong formula (with syntax error) to force an InvalidReportStateException
    setInternalState( formulaExpression, "formulaExpression", "IF(Wrong[SomeParam] = \"Yes\";\"Ok\";\"Not Ok\")" );
    setInternalState( formulaExpression, "failOnError", true );
    formulaExpression.getValue();
  }

  @Test( expected = InvalidReportStateException.class )
  public void getValueWithFormulaEvaluateErrorTest() {
    setInternalState( formulaExpression, "formulaExpression", "IF([SomeParam] = \"Yes\";\"Ok\";\"Not Ok\")" );
    setInternalState( formulaExpression, "failOnError", true );
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
  public void getValueWithFormulaEvaluateCustomErrorTest() {
    setInternalState( formulaExpression, "formulaExpression", "IF([SomeParam] = \"Yes\";\"Ok\";\"Not Ok\")" );
    setInternalState( formulaExpression, "failOnError", true );
    Formula custFormula = mock( Formula.class );
    setInternalState( formulaExpression, "compiledFormula",  custFormula );
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
