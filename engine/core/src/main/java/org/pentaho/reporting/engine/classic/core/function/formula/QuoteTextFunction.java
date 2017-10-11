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

package org.pentaho.reporting.engine.classic.core.function.formula;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;
import org.pentaho.reporting.libraries.formula.util.FormulaUtil;
import org.pentaho.reporting.libraries.xmlns.writer.CharacterEntityParser;
import org.pentaho.reporting.libraries.xmlns.writer.HtmlCharacterEntities;

public class QuoteTextFunction implements Function {
  public QuoteTextFunction() {
  }

  public String getCanonicalName() {
    return "QUOTETEXT";
  }

  public TypeValuePair evaluate( final FormulaContext context, final ParameterCallback parameters )
    throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount < 1 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }
    final Type textType = parameters.getType( 0 );
    final Object textValue = parameters.getValue( 0 );
    final String textResult = context.getTypeRegistry().convertToText( textType, textValue );

    if ( textResult == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    final String encodingResult;
    if ( parameterCount == 2 ) {
      final Type encodingType = parameters.getType( 1 );
      final Object encodingValue = parameters.getValue( 1 );
      encodingResult = context.getTypeRegistry().convertToText( encodingType, encodingValue );
      if ( encodingResult == null ) {
        throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
      }
    } else {
      encodingResult = "javascript";
    }

    if ( encodingResult.equals( "xml" ) ) {
      return new TypeValuePair( TextType.TYPE, CharacterEntityParser.createXMLEntityParser()
          .encodeEntities( textResult ) );
    } else if ( encodingResult.equals( "html" ) ) {
      return new TypeValuePair( TextType.TYPE, HtmlCharacterEntities.getEntityParser().encodeEntities( textResult ) );
    } else if ( encodingResult.equals( "formula-string" ) ) {
      return new TypeValuePair( TextType.TYPE, FormulaUtil.quoteString( textResult ) );
    } else if ( encodingResult.equals( "formula-reference" ) ) {
      return new TypeValuePair( TextType.TYPE, FormulaUtil.quoteReference( textResult ) );
    } else { // javascript
      return new TypeValuePair( TextType.TYPE, saveConvert( textResult ) );
    }
  }

  /**
   * Performs the necessary conversion of an java string into a property escaped string.
   *
   * @param text
   *          the text to be escaped
   */
  public static String saveConvert( final String text ) {
    if ( text == null ) {
      return text;
    }

    final StringBuilder buffer = new StringBuilder();
    final char[] string = text.toCharArray();

    for ( int x = 0; x < string.length; x++ ) {
      final char aChar = string[x];
      switch ( aChar ) {
        case '\\': {
          buffer.append( '\\' );
          buffer.append( '\\' );
          break;
        }
        case '\t': {
          buffer.append( '\\' );
          buffer.append( 't' );
          break;
        }
        case '\n': {
          buffer.append( '\\' );
          buffer.append( 'n' );
          break;
        }
        case '\r': {
          buffer.append( '\\' );
          buffer.append( 'r' );
          break;
        }
        case '\f': {
          buffer.append( '\\' );
          buffer.append( 'f' );
          break;
        }
        case '\b': {
          buffer.append( '\\' );
          buffer.append( 'b' );
          break;
        }
        case '"':
        case '\'':
        case '&': {
          buffer.append( '\\' );
          buffer.append( aChar );
          break;
        }
        default:
          buffer.append( aChar );
      }
    }
    return buffer.toString();
  }
}
