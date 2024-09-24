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
* Copyright (c) 2002-2018 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.openformula.ui.model2;

import java.util.ArrayList;

public class FormulaParser {
  private static class Tokenizer {
    private char[] data;
    private int position;
    private StringBuffer buffer;
    private char token;

    private Tokenizer( final char[] data ) {
      this.data = data;
      this.position = 0;
      this.buffer = new StringBuffer( data.length );
    }

    public char getToken() {
      return token;
    }

    public boolean hasNext() {
      return position < data.length;
    }

    /**
     * @noinspection MagicCharacter
     */
    public String next() {
      token = 0;
      final char c = data[ position ];
      if ( isToken( c ) ) {
        position += 1;
        token = c;
        return String.valueOf( c );
      }

      buffer.delete( 0, buffer.length() );
      buffer.append( c );
      position += 1;
      boolean inQuoting = ( '"' == c );
      for ( ; position < data.length; position += 1 ) {
        final char c2 = data[ position ];
        if ( inQuoting == false && isToken( c2 ) ) {
          return buffer.toString();
        }
        if ( c2 == '"' ) {
          inQuoting = !inQuoting;
        }
        buffer.append( c2 );
      }
      return buffer.toString();
    }

    /**
     * @noinspection MagicCharacter
     */
    private boolean isToken( final char c ) {
      if ( c == '(' ) {
        return true;
      }
      if ( c == ')' ) {
        return true;
      }
      if ( c == '+' ) {
        return true;
      }
      if ( c == '-' ) {
        return true;
      }
      if ( c == '*' ) {
        return true;
      }
      if ( c == '/' ) {
        return true;
      }
      if ( c == '%' ) {
        return true;
      }
      if ( c == '&' ) {
        return true;
      }
      if ( c == ';' ) {
        return true;
      }
      if ( c == '=' ) {
        return true;
      }
      return false;
    }
  }

  private FormulaParser() {
  }

  /**
   * @noinspection MagicCharacter
   */
  public static FormulaElement[] parseText( final FormulaDocument doc,
                                            final String text ) {
    // Make sure that the formula text contains an '=' as all formulas
    // must be prepended with equals.
    String formulaText = text;
    if ( text.startsWith( "=" ) == false ) {
      formulaText = "=" + text;
    }

    final Tokenizer strtok = new Tokenizer( formulaText.toCharArray() );

    final ArrayList elements = new ArrayList();

    // this is the cleanest way to do the parsing .. so no warning please
    //noinspection NestedAssignment
    while ( strtok.hasNext() ) {
      final String nextToken = strtok.next();
      final char ttype = strtok.getToken();
      if ( ttype != 0 ) {
        if ( '(' == ttype ) {
          elements.add( new FormulaOpenParenthesisElement( doc, doc.getRootElement() ) );
        } else if ( ')' == ttype ) {
          elements.add( new FormulaClosingParenthesisElement( doc, doc.getRootElement() ) );
        } else if ( ';' == ttype ) {
          elements.add( new FormulaSemicolonElement( doc, doc.getRootElement() ) );
        } else {
          elements.add( new FormulaOperatorElement( doc, doc.getRootElement(), String.valueOf( ttype ) ) );
        }
      } else {
        elements.add( new FormulaTextElement( doc, doc.getRootElement(), nextToken ) );
      }
    }

    return (FormulaElement[]) elements.toArray( new FormulaElement[ elements.size() ] );
  }

  public static FormulaElement[] normalizeDocument( final FormulaDocument document,
                                                    final FormulaElement[] elements ) {
    if ( elements.length == 0 ) {
      return elements;
    }

    final ArrayList mergedList = new ArrayList( elements.length );
    // merge text elements
    int startElementIdx = -1;
    for ( int i = 0; i < elements.length; i++ ) {
      final FormulaElement element = elements[ i ];
      if ( element instanceof FormulaTextElement ) {
        if ( startElementIdx < 0 ) {
          startElementIdx = i;
        }
      } else {
        if ( startElementIdx >= 0 ) {
          if ( i - 1 == startElementIdx ) {
            // a single element
            mergedList.add( elements[ i - 1 ] );
          } else {
            // many elements
            final StringBuffer buffer = new StringBuffer( 100 );
            for ( int t = startElementIdx; t < i; t++ ) {
              buffer.append( elements[ t ].getText() );
            }
            mergedList.add( new FormulaTextElement( document, document.getRootElement(), buffer.toString() ) );
          }
          startElementIdx = -1;
        }
        mergedList.add( element );

      }
    }
    if ( startElementIdx != -1 ) {
      if ( elements.length - 1 == startElementIdx ) {
        // a single element
        mergedList.add( elements[ elements.length - 1 ] );
      } else {
        // many elements
        final StringBuffer buffer = new StringBuffer( 100 );
        for ( int t = startElementIdx; t < elements.length; t++ ) {
          buffer.append( elements[ t ].getText() );
        }
        mergedList.add( new FormulaTextElement( document, document.getRootElement(), buffer.toString() ) );
      }
    }

    // mark function names
    final FormulaElement[] mergedValues =
      (FormulaElement[]) mergedList.toArray( new FormulaElement[ mergedList.size() ] );
    mergedList.clear();
    for ( int i = 0; i < ( mergedValues.length - 1 ); i++ ) {
      final FormulaElement mergedValue = mergedValues[ i ];
      final FormulaElement nextValue = mergedValues[ i + 1 ];
      if ( nextValue instanceof FormulaOpenParenthesisElement == false ) {
        mergedList.add( mergedValue );
        continue;
      }

      // potentially a function
      final String buffer = mergedValue.getText();
      final int startIdx = findFunctionNameStart( buffer );
      if ( startIdx == -1 ) {
        // a term or so, but at least definitely not a function at all..
        mergedList.add( mergedValue );
        continue;
      }

      if ( startIdx != 0 ) {
        final String text = buffer.substring( 0, startIdx );
        if ( text.trim().length() == 0 ) {
          // only leading and trailing whitespace wont cause us to create a new node.
          mergedList.add( new FormulaFunctionElement( document, document.getRootElement(), buffer ) );
          continue;
        }

        mergedList.add( new FormulaTextElement( document, document.getRootElement(), text ) );
      }

      if ( buffer.trim().equals( "(" ) ) {
        mergedList.add( new FormulaOpenParenthesisElement( document, document.getRootElement() ) );
      } else {
        mergedList.add( new FormulaFunctionElement( document, document.getRootElement(), buffer.substring( startIdx ) ) );
      }

    }

    mergedList.add( mergedValues[ mergedValues.length - 1 ] );
    // mark numeric values
    for ( int i = 0; i < mergedList.size(); i++ ) {
      final FormulaElement element = (FormulaElement) mergedList.get( i );
      if ( element instanceof FormulaTextElement ) {
        try {
          Double.parseDouble( element.getText().trim() );
          mergedList.set( i, new FormulaNumberElement( document, document.getRootElement(), element.getText() ) );
        } catch ( NumberFormatException nfe ) {
          // ignore ..
        }
      }
    }

    return (FormulaElement[]) mergedList.toArray( new FormulaElement[ mergedList.size() ] );
  }

  public static void print( final FormulaElement[] formulaElements ) {
    for ( int i = 0; i < formulaElements.length; i++ ) {
      final FormulaElement element = formulaElements[ i ];
      System.out.println( i + " Name=" + element.getName() + "; Text='" + element.getText() + '\'' );
    }
  }


  private static int findFunctionNameStart( final String buffer ) {
    int length = buffer.length() - 1;
    while ( length >= 0 ) {
      if ( Character.isWhitespace( buffer.charAt( length ) ) ) {
        length -= 1;
      } else {
        break;
      }
    }
    // the whole string is a whitespace, so the function is not a function but a term
    if ( length == -1 ) {
      return -1;
    }

    for ( int i = length - 1; i >= 0; i -= 1 ) {
      final char c = buffer.charAt( i );
      //noinspection MagicCharacter
      if ( Character.isLetter( c ) || Character.isDigit( c ) || '.' == c ) {
        continue;
      }
      return i + 1;
    }
    return 0;
  }

}
