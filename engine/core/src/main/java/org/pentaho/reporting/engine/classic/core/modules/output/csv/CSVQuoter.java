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
  /**
   * The separator used in the CSV file.
   */
  private String separator;

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
    setSeparator( separator );
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
      final StringBuffer retval = new StringBuffer( original.length() + 10 );
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
      final StringBuffer b = new StringBuffer( nativeString.length() );
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
  private boolean isQuotingNeeded( final String str ) {
    if ( str.indexOf( separator ) != -1 ) {
      return true;
    }
    if ( str.indexOf( '\n' ) != -1 ) {
      return true;
    }
    if ( str.indexOf( '\"', 1 ) != -1 ) {
      return true;
    }
    return false;
  }

  /**
   * Applies the quoting to a given string, and stores the result in the StringBuffer <code>b</code>.
   *
   * @param b
   *          the result buffer
   * @param original
   *          the string, that should be quoted.
   */
  private void applyQuote( final StringBuffer b, final String original ) {
    // This solution needs improvements. Copy blocks instead of single
    // characters.
    final int length = original.length();

    for ( int i = 0; i < length; i++ ) {
      final char c = original.charAt( i );
      if ( c == '"' ) {
        b.append( "\"\"" );
      } else {
        b.append( c );
      }
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
