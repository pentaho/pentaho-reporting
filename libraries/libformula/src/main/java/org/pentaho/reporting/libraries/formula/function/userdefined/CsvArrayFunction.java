/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

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
