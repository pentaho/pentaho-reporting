/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
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
  private final char separator;
  /**
   * The quoting character or a single quote.
   */
  private final char quate;
  /**
   * The double quote. This is a string containing the quate two times.
   */
  private final String doubleQuate;

  private final boolean forceQuote;

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
    if (isQuotingNeeded( original )) {
      final StringBuilder retval = new StringBuilder( original.length() * 2 + 2 ); // Better size estimation
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
    // Quick check for empty or single character strings
    if (nativeString == null || nativeString.length() < 2) {
      return nativeString;
    }
    
    final int stringLength = nativeString.length(); // Cache length for better JIT optimization
    
    // Check if string is actually quoted
    if (nativeString.charAt(0) != quate || nativeString.charAt(stringLength - 1) != quate) {
      return nativeString;
    }
    
    final StringBuilder b = new StringBuilder( stringLength );
    final int length = stringLength - 1;
    int start = 1;

    int pos = start;
    while ( pos != -1 ) {
      pos = nativeString.indexOf( doubleQuate, start );
      if ( pos == -1 ) {
        b.append( nativeString.substring( start, length ) );
      } else {
        b.append( nativeString.substring( start, pos ) );
        b.append( quate ); // Add single quote instead of double
        start = pos + 2; // Skip both quote characters
      }
    }
    return b.toString();
  }

  /**
   * Tests, whether this string needs to be quoted. A string is encoded if the string contains a newline character, a
   * quote character or the defined separator.
   *
   * @param str the string that should be tested.
   * @return true, if quoting needs to be applied, false otherwise.
   */
  private boolean isQuotingNeeded( final String str ) {
    if (forceQuote) return true;
    
    // Use toCharArray() for better memory access patterns and JIT optimization
    final char[] chars = str.toCharArray();
    final int length = chars.length;
    
    // Help JIT with better branch prediction by checking most common cases first
    for ( int i = 0; i < length; i++ ) {
      final char c = chars[i];
      // Order checks by frequency: separator first, then quote, then newline
      if ( c == separator || c == quate || c == '\n' || c == '\r' ) {
        return true;
      }
    }
    return false;
  }

  /**
   * Applies the quoting to a given string, and stores the result in the StringBuilder <code>b</code>.
   *
   * @param b        the result buffer
   * @param original the string, that should be quoted.
   */
  private void applyQuote( final StringBuilder b, final String original ) {
    // Use char array for better memory access patterns
    final char[] chars = original.toCharArray();
    final int length = chars.length;
    
    // Process in blocks to reduce individual append calls
    int lastCopied = 0;
    
    for ( int i = 0; i < length; i++ ) {
      final char c = chars[i];
      if ( c == quate ) {
        // Copy the block before the quote character
        if (i > lastCopied) {
          b.append( chars, lastCopied, i - lastCopied );
        }
        // Append the escaped quote
        b.append( doubleQuate );
        lastCopied = i + 1;
      }
    }
    
    // Copy any remaining characters
    if (lastCopied < length) {
      b.append( chars, lastCopied, length - lastCopied );
    }
  }


  /**
   * Applies the quoting to a given string, and writes the result to the Writer <code>b</code>.
   *
   * @param writer   the result writer
   * @param original the string, that should be quoted.
   * @throws IOException if an IO-Error occured.
   */
  private void applyQuote( final Writer writer, final String original ) throws IOException {
    // Use char array for better memory access patterns
    final char[] chars = original.toCharArray();
    final int length = chars.length;
    
    // Process in blocks to reduce individual write calls
    int lastCopied = 0;
    
    for ( int i = 0; i < length; i++ ) {
      final char c = chars[i];
      if ( c == quate ) {
        // Write the block before the quote character
        if (i > lastCopied) {
          writer.write( chars, lastCopied, i - lastCopied );
        }
        // Write the escaped quote
        writer.write( doubleQuate );
        lastCopied = i + 1;
      }
    }
    
    // Write any remaining characters
    if (lastCopied < length) {
      writer.write( chars, lastCopied, length - lastCopied );
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

  /**
   * Returns whether force quoting is enabled.
   *
   * @return true if all strings are force quoted, false otherwise.
   */
  public boolean isForceQuote() {
    return forceQuote;
  }

  /**
   * Efficiently quotes multiple strings in a single operation.
   * This method is optimized for bulk operations and better JIT performance.
   *
   * @param values the array of strings to quote
   * @param separator the separator to use between quoted values
   * @return a single string with all values quoted and separated
   */
  public String doQuotingBulk( final String[] values, final String separator ) {
    if (values == null || values.length == 0) {
      return "";
    }
    
    // Estimate total size to avoid StringBuilder resizing
    int estimatedSize = 0;
    for (final String value : values) {
      if (value != null) {
        estimatedSize += value.length() + 4; // +4 for potential quotes and separator
      }
    }
    
    final StringBuilder result = new StringBuilder(estimatedSize);
    final int valuesLength = values.length; // Cache array length for better JIT optimization
    
    for (int i = 0; i < valuesLength; i++) {
      if (i > 0) {
        result.append(separator);
      }
      
      final String value = values[i];
      if (value != null) {
        if (isQuotingNeeded(value)) {
          result.append(quate);
          applyQuote(result, value);
          result.append(quate);
        } else {
          result.append(value);
        }
      }
    }
    
    return result.toString();
  }
}
