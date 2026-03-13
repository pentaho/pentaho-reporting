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

import java.io.IOException;
import java.io.Writer;

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
   * The enclosure character.
   */
  private char enclosure;
  /**
   * The double enclosure. This is a string containing the enclosure character two times.
   */
  private String doubleEnclosure;

  private boolean forceEnclosure;

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
    this( separator, '"' );
  }

  /**
   * Creates a new CSVQuoter with the given separator and enclosure character.
   *
   * @param separator the separator
   * @param enclosure the enclosure character
   */
  public CSVQuoter( final String separator, final char enclosure ) {
    this( separator, enclosure, false );
  }

  /**
   * Creates a new CSVQuoter with the given separator and forceEnclosure flag, using '"' as default enclosure.
   *
   * @param separator      the separator
   * @param forceEnclosure whether to always enclose values
   */
  public CSVQuoter( final String separator, final boolean forceEnclosure ) {
    this( separator, '"', forceEnclosure );
  }

  public CSVQuoter( final String separator, final char enclosure, final boolean forceEnclosure ) {
    setSeparator( separator );
    this.enclosure = enclosure;
    this.doubleEnclosure = String.valueOf( enclosure ) + enclosure;
    this.forceEnclosure = forceEnclosure;
  }

  /**
   * Encodes the string, so that the string can safely be used in CSV files. If the string does not need quoting, the
   * original string is returned unchanged.
   *
   * @param original the unquoted string.
   * @return The quoted string
   */
  public String doQuoting( final String original ) {
    if ( forceEnclosure || requiresEnclosure( original ) ) {
      final StringBuilder retval = new StringBuilder( original.length() + 5 ); // a safe guess most of the time.
      retval.append( enclosure );
      applyEnclosure( retval, original );
      retval.append( enclosure );
      return retval.toString();
    } else {
      return original;
    }
  }

  /**
   * A streaming version of the enclosure algorithm for more performance. Encodes the string, so that the string can
   * safely be used in CSV files. If the string does not need enclosing, the original string is written unchanged.
   *
   * @param original the unquoted string.
   * @param writer   the writer.
   * @throws IOException if an IO error occurred.
   */
  public void doQuoting( final String original, final Writer writer ) throws IOException {
    if ( forceEnclosure || requiresEnclosure( original ) ) {
      writer.write( enclosure );
      applyEnclosure( writer, original );
      writer.write( enclosure );
    } else {
      writer.write( original );
    }
  }

  /**
   * Decodes the string, so that all escape sequences get removed. If the string was not enclosed, then the string is
   * returned unchanged.
   *
   * @param nativeString
   *          the quoted string.
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
        pos = nativeString.indexOf( doubleEnclosure, start );
        if ( pos == -1 ) {
          b.append( nativeString.substring( start, length ) );
        } else {
          b.append( nativeString.substring( start, pos ) );
          b.append( enclosure );
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
    return strLength >= 2 && nativeString.charAt( 0 ) == enclosure && nativeString.charAt( strLength - 1 ) == enclosure;
  }

  /**
   * Tests, whether this string needs to be enclosed. A string is enclosed if it contains a newline character, an
   * enclosure character or the defined separator.
   *
   * @param str
   *          the string that should be tested.
   * @return true, if enclosure needs to be applied, false otherwise.
   */
  private boolean requiresEnclosure( final String str ) {
    final boolean containsSeparator = str.indexOf( separator ) != -1;
    final boolean containsNewline = str.indexOf( '\n' ) != -1;
    final boolean containsEnclosure = str.indexOf( enclosure ) != -1;

    return containsSeparator || containsNewline || containsEnclosure;
  }

  /**
   * Applies the enclosure to a given string, and stores the result in the StringBuilder <code>b</code>.
   *
   * @param b
   *          the result buffer
   * @param original
   *          the string, that should be enclosed.
   */
  private void applyEnclosure( final StringBuilder b, final String original ) {
    final int length = original.length();
    int blockStart = 0;

    for ( int i = 0; i < length; i++ ) {
      if ( original.charAt( i ) == enclosure ) {
        // Copy the block before the enclosure character
        if ( i > blockStart ) {
          b.append( original, blockStart, i );
        }

        // Append the escaped enclosure
        b.append( doubleEnclosure );
        blockStart = i + 1;
      }
    }

    // Copy any remaining characters
    if ( blockStart < length ) {
      b.append( original, blockStart, length );
    }
  }

  /**
   * Applies the enclosure to a given string, and stores the result in the Writer <code>b</code>.
   *
   * @param b
   *          the result writer
   * @param original
   *          the string, that should be enclosed.
   * @throws IOException if an IO-Error occured.
   */
  private void applyEnclosure( final Writer b, final String original ) throws IOException {
    final int length = original.length();
    int blockStart = 0;

    for ( int i = 0; i < length; i++ ) {
      if ( original.charAt( i ) == enclosure ) {
        // Copy the block before the enclosure character
        if ( i > blockStart ) {
          b.write( original, blockStart, i - blockStart );
        }

        // Append the escaped enclosure
        b.write( doubleEnclosure );
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
  public String getSeparator() {
    return separator;
  }

  /**
   * Defines the separator, which is used in the CSV file. If you use different separators for enclosing and writing,
   * the resulting file will be invalid.
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

  /**
   * Returns the enclosure character.
   *
   * @return the enclosure character.
   */
  public char getEnclosure() {
    return enclosure;
  }

  /**
   * Sets the enclosure character.
   *
   * @param enclosure the enclosure character.
   */
  public void setEnclosure( final char enclosure ) {
    this.enclosure = enclosure;
    this.doubleEnclosure = String.valueOf( enclosure ) + enclosure;
  }

  /**
   * Returns whether force enclosure is enabled.
   *
   * @return true if all strings are force enclosed, false otherwise.
   */
  public boolean isForceEnclosure() {
    return forceEnclosure;
  }

  /**
   * Sets whether all values should be force enclosed.
   *
   * @param forceEnclosure true to always enclose, false otherwise.
   */
  public void setForceEnclosure( final boolean forceEnclosure ) {
    this.forceEnclosure = forceEnclosure;
  }
}
