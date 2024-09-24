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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula.lvalues;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.FunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionRegistry;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;

/**
 * A function. Formulas consist of functions, references or static values, which are connected by operators.
 * <p/>
 * Functions always have a cannonical name, which must be unique and which identifies the function. Functions can have a
 * list of parameters. The number of parameters can vary, and not all parameters need to be filled.
 * <p/>
 * Functions can have required and optional parameters. Mixing required and optional parameters is not allowed. Optional
 * parameters cannot be ommited, unless they are the last parameter in the list.
 * <p/>
 * This class provides the necessary wrapper functionality to fill in the parameters.
 *
 * @author Thomas Morgner
 */
public class FormulaFunction extends AbstractLValue {
  private static final Log logger = LogFactory.getLog( FormulaFunction.class );

  private static class FormulaParameterCallback implements ParameterCallback {
    private TypeValuePair[] backend;
    private FormulaFunction function;

    private FormulaParameterCallback( final FormulaFunction function ) {
      this.function = function;
      this.backend = new TypeValuePair[ function.parameters.length ];
    }

    private TypeValuePair get( final int pos ) throws EvaluationException {
      final LValue parameter = function.parameters[ pos ];
      final Type paramType = function.metaData.getParameterType( pos );
      if ( parameter != null ) {
        final TypeValuePair result = parameter.evaluate();
        if ( result.getValue() == null ) {
          return result;
        }

        // lets do some type checking, right?
        final TypeRegistry typeRegistry = function.getContext().getTypeRegistry();
        final TypeValuePair converted = typeRegistry.convertTo( paramType, result );
        if ( converted == null ) {
          if ( logger.isDebugEnabled() ) {
            logger.debug( "Failed to evaluate parameter " + pos + " on function " + function );
          }
          throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_AUTO_ARGUMENT_VALUE );
        }
        return converted;
      } else {
        return new TypeValuePair( paramType, function.metaData.getDefaultValue( pos ) );
      }
    }

    public LValue getRaw( final int position ) {
      return function.parameters[ position ];
    }

    public Object getValue( final int position ) throws EvaluationException {
      final TypeValuePair retval = backend[ position ];
      if ( retval != null ) {
        return retval.getValue();
      }

      final TypeValuePair pair = get( position );
      backend[ position ] = pair;
      return pair.getValue();
    }

    public Type getType( final int position ) throws EvaluationException {
      final TypeValuePair retval = backend[ position ];
      if ( retval != null ) {
        return retval.getType();
      }

      final TypeValuePair pair = get( position );
      backend[ position ] = pair;
      return pair.getType();
    }

    public int getParameterCount() {
      return backend.length;
    }
  }

  private String functionName;
  private LValue[] parameters;
  private Function function;
  private FunctionDescription metaData;
  private static final long serialVersionUID = 8023588016882997962L;

  public FormulaFunction( final String functionName,
                          final LValue[] parameters,
                          final ParsePosition parsePosition ) {
    this.functionName = functionName;
    setParsePosition( parsePosition );
    this.parameters = (LValue[]) parameters.clone();
  }

  public FormulaFunction( final String functionName, final LValue[] parameters ) {
    this( functionName, parameters, null );
  }

  public void initialize( final FormulaContext context ) throws EvaluationException {
    super.initialize( context );
    final FunctionRegistry registry = context.getFunctionRegistry();
    if ( function == null ) {
      function = registry.createFunction( functionName );
    }
    if ( metaData == null ) {
      metaData = registry.getMetaData( functionName );
    }

    for ( int i = 0; i < parameters.length; i++ ) {
      parameters[ i ].initialize( context );
    }
  }

  /**
   * Returns the function's name. This is the normalized name and may not be suitable for the user. Query the function's
   * metadata to retrieve a display-name.
   *
   * @return the function's name.
   */
  public String getFunctionName() {
    return functionName;
  }

  /**
   * Returns the initialized function. Be aware that this method will return null if this LValue instance has not yet
   * been initialized.
   *
   * @return the function instance or null, if the FormulaFunction instance has not yet been initialized.
   */
  public Function getFunction() {
    return function;
  }

  /**
   * Returns the function's meta-data. Be aware that this method will return null if this LValue instance has not yet
   * been initialized.
   *
   * @return the function description instance or null, if the FormulaFunction instance has not yet been initialized.
   */
  public FunctionDescription getMetaData() {
    return metaData;
  }

  public Object clone() throws CloneNotSupportedException {
    final FormulaFunction fn = (FormulaFunction) super.clone();
    fn.parameters = (LValue[]) parameters.clone();
    for ( int i = 0; i < parameters.length; i++ ) {
      final LValue parameter = parameters[ i ];
      fn.parameters[ i ] = (LValue) parameter.clone();
    }
    return fn;
  }

  public TypeValuePair evaluate() throws EvaluationException {
    // First, grab the parameters and their types.
    final FormulaContext context = getContext();
    // And if everything is ok, compute the stuff ..
    if ( function == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_FUNCTION_VALUE );
    }
    try {
      return function.evaluate( context, new FormulaParameterCallback( this ) );
    } catch ( EvaluationException e ) {
      throw e;
    } catch ( Exception e ) {
      logger.error( "Unexpected exception while evaluating", e );
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_UNEXPECTED_VALUE );
    }
  }

  /**
   * Returns any dependent lvalues (parameters and operands, mostly).
   *
   * @return
   */
  public LValue[] getChildValues() {
    return (LValue[]) parameters.clone();
  }


  public String toString() {
    final StringBuffer b = new StringBuffer( 100 );
    b.append( functionName );
    b.append( '(' );
    for ( int i = 0; i < parameters.length; i++ ) {
      if ( i > 0 ) {
        b.append( ';' );
      }
      final LValue parameter = parameters[ i ];
      b.append( parameter );
    }
    b.append( ')' );
    return b.toString();
  }

  /**
   * Checks whether the LValue is constant. Constant lvalues always return the same value.
   *
   * @return true, if the function will always return the same value.
   */
  public boolean isConstant() {
    if ( metaData == null || metaData.isVolatile() ) {
      return false;
    }
    for ( int i = 0; i < parameters.length; i++ ) {
      final LValue value = parameters[ i ];
      if ( value.isConstant() == false ) {
        return false;
      }
    }
    return true;
  }

}
