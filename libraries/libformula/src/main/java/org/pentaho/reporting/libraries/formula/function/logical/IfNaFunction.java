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

package org.pentaho.reporting.libraries.formula.function.logical;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.formula.ErrorValue;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.ErrorType;

/**
 * Creation-Date: 04.11.2006, 18:28:15
 *
 * @author Thomas Morgner
 */
public class IfNaFunction implements Function {
  private static final Log logger = LogFactory.getLog( IfNaFunction.class );
  private static final long serialVersionUID = -7517668261071087411L;

  public IfNaFunction() {
  }

  public String getCanonicalName() {
    return "IFNA";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters )
    throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount < 2 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }
    Object value = null;
    Type type = null;
    boolean nafound = false;
    try {
      type = parameters.getType( 0 );
      value = parameters.getValue( 0 );
      if ( ErrorType.TYPE.equals( type ) && value instanceof ErrorValue ) {
        logger.warn( "Passing errors around is deprecated. Throw exceptions instead." );
        final ErrorValue na = (ErrorValue) value;
        if ( na.getErrorCode() == LibFormulaErrorValue.ERROR_NA ) {
          nafound = true;
        }
      } else {
        if ( value == null ) {
          nafound = true;
        }
      }
    } catch ( EvaluationException e ) {
      if ( e.getErrorValue().getErrorCode() == LibFormulaErrorValue.ERROR_NA ) {
        nafound = true;
      } else {
        // here the error propagates, as IFNA([x],"v") is defined to behave like IF(ISNA([x]); "v"; [x])
        // and the second evaluation of [X] may yield the error that was swallowed by ISNA.
        throw e;
      }
    }

    if ( nafound == false ) {
      return new TypeValuePair( type, value );
    }
    return new TypeValuePair( parameters.getType( 1 ), parameters.getValue( 1 ) );
  }
}
