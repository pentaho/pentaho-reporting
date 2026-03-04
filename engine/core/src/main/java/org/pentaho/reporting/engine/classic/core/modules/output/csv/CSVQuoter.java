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


package org.pentaho.reporting.engine.classic.core.modules.output.csv;

/**
 * The <code>CSVQuoter</code> is a helper class to encode a string for the CSV file format.
 *
 * @author Thomas Morgner.
 */
public class CSVQuoter {
  private static final int QUOTE_SEARCH_START_INDEX = 1;

  /**
   * The separator used in the CSV file.
   */
  private String separator;
  private boolean alwaysDoQuotes;

  /**
   * Creates a new CSVQuoter, which uses a comma as the default separator.
   */
  public CSVQuoter() {
    this( "," );
  }

  /**
   * Creates a new <code>CSVQuoter</code>, which uses the defined separator.
   *
   * @param separator
   *          the separator.
   * @throws NullPointerException
   *           if the given separator is <code>null</code>.
   */
  public CSVQuoter( final String separator ) {
    this( separator, false );
  }

  public CSVQuoter( final String separator, final boolean alwaysDoQuotes ) {
    setSeparator( separator );
    setAlwaysDoQuotes( alwaysDoQuotes );
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
    if ( alwaysDoQuotes || requiresQuoting( original ) ) {
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
    final int strLength = nativeString.length();
    if ( isEnclosedByQuotes( nativeString ) ) {
      final StringBuilder b = new StringBuilder( strLength );
      final int length = strLength - 1;
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

  private boolean isEnclosedByQuotes( final String nativeString ) {
    final int strLength = nativeString.length();
    return strLength >= 2 && nativeString.charAt( 0 ) == '"' && nativeString.charAt( strLength - 1 ) == '"';
  }

  /**
   * Tests, whether this string needs to be quoted. A string is encoded if the string contains a newline character, a
   * quote character or the defined separator.
   *
   * @param str
   *          the string that should be tested.
   * @return true, if quoting needs to be applied, false otherwise.
   */
  private boolean requiresQuoting( final String str ) {
    final boolean containsSeparator = str.indexOf( separator ) != -1;
    final boolean containsNewline = str.indexOf( '\n' ) != -1;
    final boolean containsQuote = str.indexOf( '\"', QUOTE_SEARCH_START_INDEX ) != -1;

    return containsSeparator || containsNewline || containsQuote;
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
    final int length = original.length();
    int blockStart = 0;

    for ( int i = 0; i < length; i++ ) {
      if ( original.charAt( i ) == '"' ) {
        // Copy the block before the quote character
        if ( i > blockStart ) {
          b.append( original, blockStart, i );
        }

        // Append the escaped quote
        b.append( "\"\"" );
        blockStart = i + 1;
      }
    }

    // Copy any remaining characters
    if ( blockStart < length ) {
      b.append( original, blockStart, length );
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

  public void setAlwaysDoQuotes( final boolean alwaysDoQuotes ) {
    this.alwaysDoQuotes = alwaysDoQuotes;
  }

  public boolean isAlwaysDoQuotes() {
    return alwaysDoQuotes;
  }
}
