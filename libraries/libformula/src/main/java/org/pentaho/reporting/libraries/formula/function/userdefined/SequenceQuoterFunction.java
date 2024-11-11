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


package org.pentaho.reporting.libraries.formula.function.userdefined;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Sequence;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

public class SequenceQuoterFunction implements Function {
  private static final long serialVersionUID = -8692592342324471253L;

  @Override
  public String getCanonicalName() {
    return "SEQUENCEQUOTER";
  }

  // Based on CSVTextFunction...
  @Override
  public TypeValuePair evaluate( FormulaContext context, ParameterCallback parameters ) throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount < 1 || parameterCount > 3 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    final Sequence sequence =
      context.getTypeRegistry().convertToSequence( parameters.getType( 0 ), parameters.getValue( 0 ) );
    if ( sequence == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_NA_VALUE );
    }

    String quote = "\"";
    String separator = ",";
    if ( parameterCount > 1 ) {
      final Type indexType = parameters.getType( 1 );
      final Object indexValue = parameters.getValue( 1 );
      separator = context.getTypeRegistry().convertToText( indexType, indexValue );
    }

    if ( parameterCount > 2 ) {
      final Type indexType = parameters.getType( 2 );
      final Object indexValue = parameters.getValue( 2 );
      quote = context.getTypeRegistry().convertToText( indexType, indexValue );
    }

    StringBuilder b = new StringBuilder();
    while ( sequence.hasNext() ) {
      final Object o = sequence.next();
      if ( o != null ) {
        b.append( quote ).append( String.valueOf( o ) ).append( quote );
      }
      if ( sequence.hasNext() ) {
        b.append( separator );
      }
    }
    return new TypeValuePair( TextType.TYPE, b.toString() );

  }

}
