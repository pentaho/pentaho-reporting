package org.pentaho.reporting.libraries.formula.function.text;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

public class AscFunction implements Function {
  public AscFunction() {
  }

  public String getCanonicalName() {
    return "ASC";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount != 1 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }
    final Type type1 = parameters.getType( 0 );
    final Object value1 = parameters.getValue( 0 );
    final TypeRegistry typeRegistry = context.getTypeRegistry();

    final String result = typeRegistry.convertToText( type1, value1 );
    if ( result == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    final char[] chars = result.toCharArray();
    final StringBuffer b = new StringBuffer( chars.length );
    for ( int i = 0; i < chars.length; i++ ) {
      final char c = chars[ i ];
      convert( c, b );
    }
    return new TypeValuePair( TextType.TYPE, b.toString() );
  }

  private void convert( final char c, final StringBuffer b ) {
    if ( c >= 0x30a1 && c <= 0x30aa ) {
      if ( ( c % 2 ) == 0 ) {
        // katakana a-o
        b.append( (char) ( ( c - 0x30a2 ) / 2 + 0xff71 ) );
      } else {
        // katakana small a-o
        b.append( (char) ( ( c - 0x30a1 ) / 2 + 0xff67 ) );
      }
      return;
    }

    if ( c >= 0x30ab && c <= 0x30c2 ) {
      if ( ( c % 2 ) == 0 ) {
        // katakana ka-chi 
        b.append( (char) ( ( c - 0x30ab ) / 2 + 0xff76 ) );
      } else {
        // katakana ga-dhi
        b.append( (char) ( ( c - 0x30ac ) / 2 + 0xff76 ) );
        b.append( (char) 0xff9e );
      }
      return;
    }

    if ( c == 0x30c3 ) {
      // katakana small tsu
      b.append( (char) 0xff6f );
      return;
    }

    if ( c >= 0x30c4 && c <= 0x30c9 ) {
      if ( ( c % 2 ) == 0 ) {
        // katakana tsu-to
        b.append( (char) ( ( c - 0x30c4 ) / 2 + 0xff82 ) );
      } else {
        // katakana du-do
        b.append( (char) ( ( c - 0x30c5 ) / 2 + 0xff82 ) );
        b.append( (char) 0xff9e );
      }
      return;
    }

    if ( c >= 0x30ca && c <= 0x30ce ) {
      // katakana na-no
      b.append( (char) ( ( c - 0x30ca ) + 0xff85 ) );
      return;
    }

    if ( c >= 0x30cf && c <= 0x30dd ) {
      switch( c % 3 ) {
        case 0:
          // katakana ha-no
          b.append( (char) ( ( c - 0x30cf ) / 3 + 0xff8a ) );
          break;
        case 1:
          // katakana ba-bo
          b.append( (char) ( ( c - 0x30d0 ) / 3 + 0xff8a ) );
          b.append( (char) 0xff9e );
          break;
        case 2:
          // katakana pa-po
          b.append( (char) ( ( c - 0x30d1 ) / 3 + 0xff8a ) );
          b.append( (char) 0xff9f );
          break;
        default:
          throw new IllegalStateException();
      }
      return;
    }

    if ( c >= 0x30de && c <= 0x30e2 ) {
      // katakana ma-mo
      b.append( (char) ( c - 0x30de + 0xff8f ) );
      return;
    }

    if ( c >= 0x30e3 && c <= 0x30e8 ) {
      if ( c % 2 == 0 ) {
        // katakana ya-yo
        b.append( (char) ( ( c - 0x30e4 ) / 2 + 0xff94 ) );
      } else {
        // katakana small ya-yo
        b.append( (char) ( ( c - 0x30e3 ) / 2 + 0xff6c ) );
      }
      return;
    }

    if ( c >= 0x30e9 && c <= 0x30ed ) {
      // katakana ra-ro
      b.append( (char) ( c - 0x30e9 + 0xff97 ) );
      return;
    }

    if ( c == 0x30ef ) {
      // katakana wa
      b.append( (char) 0xff9c );
      return;
    }

    if ( c == 0x30f2 ) {
      // katakana wo
      b.append( (char) 0xff66 );
      return;
    }

    if ( c == 0x30f3 ) {
      // katakana nn
      b.append( (char) 0xff9d );
      return;
    }

    if ( c >= 0xff01 && c <= 0xff5e ) {
      // ASCII characters
      b.append( (char) ( c - 0xff01 + 0x0021 ) );
      return;
    }

    if ( c == 0x2015 ) {
      // HORIZONTAL BAR => HALFWIDTH KATAKANA-HIRAGANA PROLONGED SOUND MARK
      b.append( (char) 0xff70 );
      return;
    }

    if ( c == 0x2018 ) {
      // LEFT SINGLE QUOTATION MARK => GRAVE ACCENT
      b.append( (char) 0x0060 );
      return;
    }

    if ( c == 0x2019 ) {
      // RIGHT SINGLE QUOTATION MARK => APOSTROPHE
      b.append( (char) 0x0027 );
      return;
    }

    if ( c == 0x201d ) {
      // RIGHT DOUBLE QUOTATION MARK => QUOTATION MARK
      b.append( (char) 0x0022 );
      return;
    }

    if ( c == 0x3001 ) {
      // IDEOGRAPHIC COMMA
      b.append( (char) 0xff64 );
      return;
    }

    if ( c == 0x3002 ) {
      // IDEOGRAPHIC FULL STOP
      b.append( (char) 0xff61 );
      return;
    }

    if ( c == 0x300c ) {
      // LEFT CORNER BRACKET
      b.append( (char) 0xff61 );
      return;
    }

    if ( c == 0x300d ) {
      // RIGHT CORNER BRACKET
      b.append( (char) 0xff61 );
      return;
    }

    if ( c == 0x309b ) {
      // KATAKANA-HIRAGANA VOICED SOUND MARK
      b.append( (char) 0xff9e );
      return;
    }

    if ( c == 0x309c ) {
      // KATAKANA-HIRAGANA SEMI-VOICED SOUND MARK
      b.append( (char) 0xff9f );
      return;
    }

    if ( c == 0x30fb ) {
      // KATAKANA MIDDLE DOT
      b.append( (char) 0xff65 );
      return;
    }

    if ( c == 0x30fc ) {
      // KATAKANA-HIRAGANA PROLONGED SOUND MARK
      b.append( (char) 0xff70 );
      return;
    }

    if ( c == 0xffe5 ) {
      // FULLWIDTH YEN SIGN => REVERSE SOLIDUS "\" 
      b.append( (char) 0x005c );
      return;
    }

    b.append( c );
  }
}
