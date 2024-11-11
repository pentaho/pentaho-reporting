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


package org.pentaho.reporting.libraries.formula.function.information;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.ContextLookup;
import org.pentaho.reporting.libraries.formula.lvalues.LValue;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Sequence;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;

import java.math.BigDecimal;

/**
 * This function counts the number of blank cells in the Reference provided. A cell is blank if it cell is empty.
 *
 * @author Cedric Pronzato
 */
// todo: maybe use something else than sequence (use array instead because sequence allows the use of scalar)
public class CountBlankFunction implements Function {

  public CountBlankFunction() {
  }

  public String getCanonicalName() {
    return "COUNTBLANK";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();

    if ( parameterCount != 1 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    int count = 0;

    final Object value = parameters.getValue( 0 );
    final Type type = parameters.getType( 0 );
    final LValue raw = parameters.getRaw( 0 );

    if ( raw instanceof ContextLookup ) {
      if ( value != null ) {
        try {
          final Sequence sequence = context.getTypeRegistry().convertToSequence( type, value );

          while ( sequence.hasNext() ) {
            final Object o = sequence.next();
            if ( o == null ) {
              count++;
            }
          }
        } catch ( EvaluationException e ) {

        }
      }
    }

    return new TypeValuePair( NumberType.GENERIC_NUMBER, new BigDecimal( count ) );
  }
}
