package org.pentaho.reporting.libraries.formula.function.text;

import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class CleanFunction implements Function {
  public CleanFunction() {
  }

  public String getCanonicalName() {
    return "CLEAN";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount < 1 || parameterCount > 2 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }
    final Type type1 = parameters.getType( 0 );
    final Object value1 = parameters.getValue( 0 );
    final TypeRegistry typeRegistry = context.getTypeRegistry();

    String result = typeRegistry.convertToText( type1, value1 );
    if ( result == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }


    if ( parameterCount == 2 ) {
      final Type typeEncoding = parameters.getType( 1 );
      final Object valueEncoding = parameters.getValue( 1 );
      result = pruneUnprintableChars( typeRegistry, result, typeEncoding, valueEncoding );
    }


    final char[] chars = result.toCharArray();
    final StringBuffer b = new StringBuffer( chars.length );
    for ( int i = 0; i < chars.length; i++ ) {
      final char c = chars[ i ];
      convert( c, b );
    }
    return new TypeValuePair( TextType.TYPE, b.toString() );

  }

  private String pruneUnprintableChars( final TypeRegistry typeRegistry,
                                        final String result,
                                        final Type typeEncoding,
                                        final Object valueEncoding )
    throws EvaluationException {
    final String encoding = typeRegistry.convertToText( typeEncoding, valueEncoding );
    if ( StringUtils.isEmpty( encoding ) ) {
      return result;
    }

    if ( !Charset.isSupported( encoding ) ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    final Charset charset = Charset.forName( encoding );
    if ( charset.canEncode() ) {
      final CharsetEncoder charsetEncoder = charset.newEncoder();
      final char[] chars = result.toCharArray();
      final StringBuffer b = new StringBuffer( chars.length );
      for ( int i = 0; i < chars.length; i++ ) {
        final char c = chars[ i ];
        if ( charsetEncoder.canEncode( c ) ) {
          b.append( c );
        }
      }
      return b.toString();
    }
    return result;
  }

  private void convert( final char c, final StringBuffer b ) {
    if ( Character.isISOControl( c ) || Character.getType( c ) == Character.UNASSIGNED ) {
      return;
    }
    b.append( c );
  }
}
