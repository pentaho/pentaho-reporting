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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.csv;

/**
 * The <code>CSVQuoter</code> is a helper class to encode a string for the CSV file format.
 *
 * @author Thomas Morgner.
 */
public class CSVQuoter {
  /**
   * The separator used in the CSV file.
   */
  private String separator;
  private final boolean alwaysDoQuotes;

  /**
   * Creates a new CSVQuoter, which uses a comma as the default separator.
   */
  public CSVQuoter() {
    this( ",", false);
  }

  /**
   * Creates a new <code>CSVQuoter</code>, which uses the defined separator.
   *
   * @param separator
   *          the separator.
   * @throws NullPointerException
   *           if the given separator is <code>null</code>.
   */
  public CSVQuoter( final String separator, final boolean alwaysDoQuotes) {
    setSeparator( separator );
    this.alwaysDoQuotes=alwaysDoQuotes;
  }

  /**
   * Encodes the string, so that the string can safely be used in CSV files. If the string does not need quoting, the
   * original string is returned unchanged.
   *
   * @param original
   *          the unquoted string.
   * @return The quoted string
   */
  public String doQuoting( final String original ) {
    if ( isQuotingNeeded( original ) ) {
      final StringBuilder retval = new StringBuilder( original.length() + 10 );
      retval.append( '\"' );
      applyQuote( retval, original );
      retval.append( '\"' );
      return retval.toString();
    } else {
      return original;
    }
  }

  /**
   * Decodes the string, so that all escape sequences get removed. If the string was not quoted, then the string is
   * returned unchanged.
   *
   * @param nativeString
   *          the quoted string.
   * @return The unquoted string.
   */
  public String undoQuoting( final String nativeString ) {
    if ( isQuotingNeeded( nativeString ) ) {
      final StringBuilder b = new StringBuilder( nativeString.length() );
      final int length = nativeString.length() - 1;
      int start = 1;

      int pos = start;
      while ( pos != -1 ) {
        pos = nativeString.indexOf( "\"\"", start );
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
   * @param str
   *          the string that should be tested.
   * @return true, if quoting needs to be applied, false otherwise.
   */
  protected boolean isQuotingNeeded( final String str ) {
    if (this.alwaysDoQuotes) return true;
    
    // Single pass through string checking for any problematic characters
    final int length = str.length();
    for (int i = 0; i < length; i++) {
      final char c = str.charAt(i);
      if (c == '"' || c == '\n') {
        return true;
      }
    }
    
    // Check for separator (more expensive, do last)
    return str.indexOf( separator ) != -1;
  }

  /**
   * Applies the quoting to a given string, and stores the result in the StringBuffer <code>b</code>.
   *
   * @param b
   *          the result buffer
   * @param original
   *          the string, that should be quoted.
   */
  private void applyQuote( final StringBuilder b, final String original ) {
    // Optimized block copy approach instead of character-by-character
    final int length = original.length();
    int start = 0;
    
    for ( int i = 0; i < length; i++ ) {
      if ( original.charAt( i ) == '"' ) {
        // Copy the block before the quote
        if ( i > start ) {
          b.append( original, start, i );
        }
        // Add escaped quote
        b.append( "\"\"" );
        start = i + 1;
      }
    }
    
    // Copy remaining characters
    if ( start < length ) {
      b.append( original, start, length );
    }
  }

  /**
   * Gets the separator used in this quoter and the CSV file.
   *
   * @return the separator (never <code>null</code>).
   */
  public String getSeparator() {
    return separator;
  }

  /**
   * Defines the separator, which is used in the CSV file. If you use different separators for quoting and writing, the
   * resulting file will be invalid.
   *
   * @param separator
   *          the separator (<code>null</code> not permitted).
   */
  public void setSeparator( final String separator ) {
    if ( separator == null ) {
      throw new NullPointerException();
    }
    this.separator = separator;
  }
}
