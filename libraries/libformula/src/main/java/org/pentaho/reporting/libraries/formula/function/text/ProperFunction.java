package org.pentaho.reporting.libraries.formula.function.text;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

public class ProperFunction implements Function {
  public ProperFunction() {
  }

  public String getCanonicalName() {
    return "PROPER";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount != 1 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }
    final Type type1 = parameters.getType( 0 );
    final Object value1 = parameters.getValue( 0 );
    final String result = context.getTypeRegistry().convertToText( type1, value1 );

    if ( result == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    return new TypeValuePair( TextType.TYPE, capitalize( result ) );
  }

  private String capitalize( final String text ) {
    final char[] textArray = text.toCharArray();

    boolean startOfWord = true;

    final int textLength = textArray.length;
    for ( int i = 0; i < textLength; i++ ) {
      final char c = textArray[ i ];
      // we ignore the punctutation chars or any other possible extra chars
      // for now. Words start at whitespaces ...
      if ( Character.isLetter( c ) == false ) {
        startOfWord = true;
      } else {
        if ( startOfWord == true ) {
          if ( Character.isLetter( c ) ) {
            textArray[ i ] = Character.toTitleCase( c );
          }
        }
        startOfWord = false;
      }
    }
    return new String( textArray );
  }
}
