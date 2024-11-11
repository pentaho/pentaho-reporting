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


package org.pentaho.reporting.libraries.formula.parser;

/**
 * Creation-Date: 03.11.2006, 18:57:23
 *
 * @author Thomas Morgner
 */
public class ParserTools {
  private ParserTools() {
  }

  public static String stripReferenceQuote( String s ) {
    if ( s.length() < 2 ) {
      return s;
    }
    final String strippedBraces = s.substring( 1, s.length() - 1 );
    if ( strippedBraces.length() <= 2 ) {
      return strippedBraces;
    }
    if ( strippedBraces.startsWith( "\"" ) && strippedBraces.endsWith( "\"" ) ) {
      return stripQuote( strippedBraces );
    }
    return strippedBraces;
  }

  /**
   * Unconditionally removes the first and last character of the given string and also unquotes the quoted
   * double-quotes.
   *
   * @param s the string to be stripped.
   * @return the stripped string.
   */
  public static String stripQuote( final String s ) {
    boolean encounteredQuote = false;

    final StringBuffer b = new StringBuffer( s.length() - 2 );
    final int size = s.length() - 1;
    for ( int i = 1; i < size; i++ ) {
      final char c = s.charAt( i );
      if ( encounteredQuote ) {
        if ( c == '"' ) {
          encounteredQuote = false;
          continue;
        }
      }
      if ( c == '"' ) {
        encounteredQuote = true;
      } else {
        encounteredQuote = false;
      }
      b.append( c );
    }
    return b.toString();
  }
}
