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
  private char quote;
  /**
   * The double quote. This is a string containing the quote two times.
   */
  private String doubleQuote;

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
   * @param quote     the quoting character
   */
  public CSVQuoter( final char separator, final char quote ) {
    this( separator, quote, false );
  }

  public CSVQuoter( final char separator, final char quote, final boolean forceQuoting ) {
    this.forceQuote = forceQuoting;
    this.separator = separator;
    this.quote = quote;
    this.doubleQuote = String.valueOf( quote ) + quote;
  }

  /**
   * Encodes the string, so that the string can safely be used in CSV files. If the string does not need quoting, the
   * original string is returned unchanged.
   *
   * @param original the unquoted string.
   * @return The quoted string
   */
  public String doQuoting( final String original ) {
    if ( forceQuote || requiresQuoting( original ) ) {
      final StringBuilder retval = new StringBuilder( original.length() + 5 ); // a safe guess most of the time.
      retval.append( quote );
      applyQuote( retval, original );
      retval.append( quote );
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
   * @throws IOException if an IO error occurred.
   */
  public void doQuoting( final String original, final Writer writer ) throws IOException {
    if ( forceQuote || requiresQuoting( original ) ) {
      writer.write( quote );
      applyQuote( writer, original );
      writer.write( quote );
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
    final int strLength = nativeString.length();
    if ( isEnclosed( nativeString ) ) {
      final StringBuilder b = new StringBuilder( strLength );
      final int length = strLength - 1;
      int start = 1;

      int pos = start;
      while ( pos != -1 ) {
        pos = nativeString.indexOf( doubleQuote, start );
        if ( pos == -1 ) {
          b.append( nativeString.substring( start, length ) );
        } else {
          b.append( nativeString.substring( start, pos ) );
          b.append( quote );
          start = pos + 2;
        }
      }
      return b.toString();
    } else {
      return nativeString;
    }
  }

  private boolean isEnclosed( final String nativeString ) {
    final int strLength = nativeString.length();
    return strLength >= 2 && nativeString.charAt( 0 ) == quote && nativeString.charAt( strLength - 1 ) == quote;
  }

  /**
   * Tests, whether this string needs to be quoted. A string is encoded if the string contains a newline character, a
   * quote character or the defined separator.
   *
   * @param str the string that should be tested.
   * @return true, if quoting needs to be applied, false otherwise.
   */
  private boolean requiresQuoting( final String str ) {
    final boolean containsSeparator = str.indexOf( separator ) != -1;
    final boolean containsNewline = str.indexOf( '\n' ) != -1;
    final boolean containsQuote = str.indexOf( quote ) != -1;

    return containsSeparator || containsNewline || containsQuote;
  }

  /**
   * Applies the quoting to a given string, and stores the result in the StringBuffer <code>b</code>.
   *
   * @param b        the result buffer
   * @param original the string, that should be quoted.
   */
  private void applyQuote( final StringBuilder b, final String original ) {
    final int length = original.length();
    int blockStart = 0;

    for ( int i = 0; i < length; i++ ) {
      if ( original.charAt( i ) == quote ) {
        // Copy the block before the quote character
        if ( i > blockStart ) {
          b.append( original, blockStart, i );
        }

        // Append the escaped quote
        b.append( doubleQuote );
        blockStart = i + 1;
      }
    }

    // Copy any remaining characters
    if ( blockStart < length ) {
      b.append( original, blockStart, length );
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
    final int length = original.length();
    int blockStart = 0;

    for ( int i = 0; i < length; i++ ) {
      if ( original.charAt( i ) == quote ) {
        // Copy the block before the quote character
        if ( i > blockStart ) {
          b.write( original, blockStart, i - blockStart );
        }

        // Append the escaped quote
        b.write( doubleQuote );
        blockStart = i + 1;
      }
    }

    // Copy any remaining characters
    if ( blockStart < length ) {
      b.write( original, blockStart, length - blockStart );
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
  public char getQuote() {
    return quote;
  }

  /**
   * Returns the quoting character.
   *
   * @return the quote character.
   * @deprecated use {@link #getQuote()}.
   */
  @Deprecated
  public char getQuate() {
    return getQuote();
  }

  /**
   * Returns whether force quoting is enabled.
   *
   * @return true if all strings are force quoted, false otherwise.
   */
  public boolean isForceQuote() {
    return forceQuote;
  }
}
