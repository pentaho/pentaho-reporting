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

import org.pentaho.reporting.libraries.base.util.CSVQuoter;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Sequence;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

public class CsvTextFunction implements Function {
  public CsvTextFunction() {
  }

  public String getCanonicalName() {
    return "CSVTEXT";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount < 1 || parameterCount > 4 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    final Sequence sequence =
      context.getTypeRegistry().convertToSequence( parameters.getType( 0 ), parameters.getValue( 0 ) );
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

    if ( doQuoting == false ) {
      StringBuilder b = new StringBuilder();
      while ( sequence.hasNext() ) {
        final Object o = sequence.next();
        if ( o != null ) {
          b.append( o );
        }
        if ( sequence.hasNext() ) {
          b.append( separator );
        }
      }
      return new TypeValuePair( TextType.TYPE, b.toString() );
    } else {
      final CSVQuoter quoter = new CSVQuoter( quote.charAt( 0 ), quote.charAt( 0 ), true );
      StringBuilder b = new StringBuilder();
      while ( sequence.hasNext() ) {
        final Object o = sequence.next();
        if ( o != null ) {
          b.append( quoter.doQuoting( String.valueOf( o ) ) );
        }
        if ( sequence.hasNext() ) {
          b.append( separator );
        }
      }
      return new TypeValuePair( TextType.TYPE, b.toString() );
    }
  }
}
