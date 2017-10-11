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

package org.pentaho.reporting.libraries.base.util;

import java.io.IOException;
import java.io.Writer;

/**
 * The <code>CSVQuoter</code> is a helper class to encode a string for the CSV file format.
 *
 * @author Thomas Morgner.
 */
public final class CSVQuoter {
  /**
   * The separator used in the CSV file.
   */
  private char separator;
  /**
   * The quoting character or a single quote.
   */
  private char quate;
  /**
   * The double quote. This is a string containing the quate two times.
   */
  private String doubleQuate;

  private boolean forceQuote;

  /**
   * Creates a new CSVQuoter, which uses a comma as the default separator.
   */
  public CSVQuoter() {
    this( ',', '"' );
  }

  /**
   * Creates a new <code>CSVQuoter</code>, which uses the defined separator.
   *
   * @param separator the separator.
   * @throws NullPointerException if the given separator is <code>null</code>.
   */
  public CSVQuoter( final char separator ) {
    this( separator, '"' );
  }

  /**
   * Creates a new CSVQuoter with the given separator and quoting character.
   *
   * @param separator the separator
   * @param quate     the quoting character
   */
  public CSVQuoter( final char separator, final char quate ) {
    this( separator, quate, false );
  }

  public CSVQuoter( final char separator, final char quate, final boolean forceQuoting ) {
    this.forceQuote = forceQuoting;
    this.separator = separator;
    this.quate = quate;
    this.doubleQuate = String.valueOf( quate ) + quate;
  }

  /**
   * Encodes the string, so that the string can safely be used in CSV files. If the string does not need quoting, the
   * original string is returned unchanged.
   *
   * @param original the unquoted string.
   * @return The quoted string
   */
  public String doQuoting( final String original ) {
    if ( forceQuote || isQuotingNeeded( original ) ) {
      final StringBuffer retval = new StringBuffer( original.length() + 5 ); // a safe guess most of the time.
      retval.append( quate );
      applyQuote( retval, original );
      retval.append( quate );
      return retval.toString();
    } else {
      return original;
    }
  }

  /**
   * A streaming version of the quoting algorithm for more performance. Encodes the string, so that the string can
   * safely be used in CSV files. If the string does not need quoting, the original string is returned unchanged.
   *
   * @param original the unquoted string.
   * @param writer   the writer.
   * @throws IOException if an IO error occured.
   */
  public void doQuoting( final String original, final Writer writer ) throws IOException {
    if ( isQuotingNeeded( original ) ) {
      writer.write( quate );
      applyQuote( writer, original );
      writer.write( quate );
    } else {
      writer.write( original );
    }
  }

  /**
   * Decodes the string, so that all escape sequences get removed. If the string was not quoted, then the string is
   * returned unchanged.
   *
   * @param nativeString the quoted string.
   * @return The unquoted string.
   */
  public String undoQuoting( final String nativeString ) {
    if ( isQuotingNeeded( nativeString ) ) {
      final StringBuilder b = new StringBuilder( nativeString.length() );
      final int length = nativeString.length() - 1;
      int start = 1;

      int pos = start;
      while ( pos != -1 ) {
        pos = nativeString.indexOf( doubleQuate, start );
        if ( pos == -1 ) {
          b.append( nativeString.substring( start, length ) );
        } else {
          b.append( nativeString.substring( start, pos ) );
          start = pos + 1;
        }
      }
      return b.toString();
    } else {
      return nativeString;
    }
  }

  /**
   * Tests, whether this string needs to be quoted. A string is encoded if the string contains a newline character, a
   * quote character or the defined separator.
   *
   * @param str the string that should be tested.
   * @return true, if quoting needs to be applied, false otherwise.
   */
  private boolean isQuotingNeeded( final String str ) {
    final int length = str.length();
    for ( int i = 0; i < length; i++ ) {
      final char c = str.charAt( i );
      if ( c == separator ) {
        return true;
      }
      if ( c == '\n' ) {
        return true;
      }
      if ( c == quate ) {
        return true;
      }
    }
    return false;
  }

  /**
   * Applies the quoting to a given string, and stores the result in the StringBuffer <code>b</code>.
   *
   * @param b        the result buffer
   * @param original the string, that should be quoted.
   */
  private void applyQuote( final StringBuffer b, final String original ) {
    // This solution needs improvements. Copy blocks instead of single
    // characters.
    final int length = original.length();

    for ( int i = 0; i < length; i++ ) {
      final char c = original.charAt( i );
      if ( c == quate ) {
        b.append( doubleQuate );
      } else {
        b.append( c );
      }
    }
  }


  /**
   * Applies the quoting to a given string, and stores the result in the StringBuffer <code>b</code>.
   *
   * @param b        the result buffer
   * @param original the string, that should be quoted.
   * @throws IOException if an IO-Error occured.
   */
  private void applyQuote( final Writer b, final String original ) throws IOException {
    // This solution needs improvements. Copy blocks instead of single
    // characters.
    final int length = original.length();

    for ( int i = 0; i < length; i++ ) {
      final char c = original.charAt( i );
      if ( c == quate ) {
        b.write( doubleQuate );
      } else {
        b.write( c );
      }
    }
  }

  /**
   * Gets the separator used in this quoter and the CSV file.
   *
   * @return the separator (never <code>null</code>).
   */
  public char getSeparator() {
    return separator;
  }

  /**
   * Returns the quoting character.
   *
   * @return the quote character.
   */
  public char getQuate() {
    return quate;
  }
}
