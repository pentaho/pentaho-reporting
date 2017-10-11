/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula.function.userdefined;

import org.pentaho.reporting.libraries.base.util.CSVTokenizer;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;

public class CsvArrayFunction implements Function {
  public CsvArrayFunction() {
  }

  public String getCanonicalName() {
    return "CSVARRAY";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount < 1 || parameterCount > 4 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    final String sequence =
      context.getTypeRegistry().convertToText( parameters.getType( 0 ), parameters.getValue( 0 ) );
    if ( sequence == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_NA_VALUE );
    }

    String quote = "\"";
    String separator = ",";
    boolean doQuoting = false;
    if ( parameterCount > 1 ) {
      final Type indexType = parameters.getType( 1 );
      final Object indexValue = parameters.getValue( 1 );
      final Boolean indexNumber = context.getTypeRegistry().convertToLogical( indexType, indexValue );
      doQuoting = Boolean.TRUE.equals( indexNumber );
    }

    if ( parameterCount > 2 ) {
      final Type indexType = parameters.getType( 2 );
      final Object indexValue = parameters.getValue( 2 );
      separator = context.getTypeRegistry().convertToText( indexType, indexValue );
    }

    if ( parameterCount > 3 ) {
      final Type indexType = parameters.getType( 3 );
      final Object indexValue = parameters.getValue( 3 );
      quote = context.getTypeRegistry().convertToText( indexType, indexValue );
    }

    final ArrayList resultList;
    final Enumeration strtok;
    if ( doQuoting == false ) {
      final StringTokenizer strtokenizer = new StringTokenizer( sequence, separator );
      resultList = new ArrayList( strtokenizer.countTokens() );
      strtok = strtokenizer;
    } else {
      final CSVTokenizer strtokenizer = new CSVTokenizer( sequence, separator, quote, false );
      resultList = new ArrayList( strtokenizer.countTokens() );
      strtok = strtokenizer;
    }

    while ( strtok.hasMoreElements() ) {
      final Object o = strtok.nextElement();
      if ( o != null ) {
        resultList.add( o );
      }
    }
    return new TypeValuePair( AnyType.TYPE, resultList.toArray( new String[ resultList.size() ] ) );
  }
}
