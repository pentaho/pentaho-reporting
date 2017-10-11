/*
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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

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
