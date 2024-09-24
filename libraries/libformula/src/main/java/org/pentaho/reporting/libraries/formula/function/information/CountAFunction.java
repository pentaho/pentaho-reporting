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

package org.pentaho.reporting.libraries.formula.function.information;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.ArrayCallback;
import org.pentaho.reporting.libraries.formula.typing.Sequence;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;
import org.pentaho.reporting.libraries.formula.typing.sequence.RecursiveSequence;

import java.math.BigDecimal;

/**
 * This function counts the number of non-empty values in the list of AnySequences provided. A value is non-blank if it
 * contains any content of any type, including an error.
 *
 * @author Cedric Pronzato
 */
public class CountAFunction implements Function {

  public CountAFunction() {
  }

  public String getCanonicalName() {
    return "COUNTA";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();

    if ( parameterCount == 0 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    int count = 0;

    for ( int paramIdx = 0; paramIdx < parameterCount; paramIdx++ ) {
      try {
        final Object value = parameters.getValue( paramIdx );
        final Sequence sequence = new RecursiveSequence( value, context );
        while ( sequence.hasNext() ) {
          final Object o = sequence.next();
          count += countElement( o );
        }
      } catch ( EvaluationException e ) {
        count++;
      }
    }

    return new TypeValuePair( NumberType.GENERIC_NUMBER, new BigDecimal( count ) );
  }

  private int countElement( final Object o )
    throws EvaluationException {
    int count = 0;
    if ( o instanceof ArrayCallback ) {
      final ArrayCallback callback = (ArrayCallback) o;
      final int rowCount = callback.getRowCount();
      final int colCount = callback.getColumnCount();
      for ( int r = 0; r < rowCount; r++ ) {
        for ( int c = 0; c < colCount; c++ ) {
          final Object val = callback.getValue( r, c );
          if ( val != null ) {
            count += countElement( val );
          }
        }
      }
    } else if ( o instanceof Sequence ) {
      final Sequence s = (Sequence) o;
      while ( s.hasNext() ) {
        count += countElement( s.next() );
      }
    } else if ( o != null ) {
      count++;
    }
    return count;
  }
}
