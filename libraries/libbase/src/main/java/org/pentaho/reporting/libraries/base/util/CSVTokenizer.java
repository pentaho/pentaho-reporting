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

package org.pentaho.reporting.libraries.base.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * The csv tokenizer class allows an application to break a Comma Separated Value format into tokens. The tokenization
 * method is much simpler than the one used by the <code>StringTokenizer</code> class. The <code>CSVTokenizer</code>
 * methods do not distinguish among identifiers, numbers, and quoted strings, nor do they recognize and skip comments.
 * <p/>
 * The set of separator (the characters that separate tokens) may be specified either at creation time or on a per-token
 * basis.
 * <p/>
 * An instance of <code>CSVTokenizer</code> behaves in one of two ways, depending on whether it was created with the
 * <code>returnSeparators</code> flag having the value <code>true</code> or <code>false</code>: <ul> <li>If the flag is
 * <code>false</code>, delimiter characters serve to separate tokens. A token is a maximal sequence of consecutive
 * characters that are not separator. <li>If the flag is <code>true</code>, delimiter characters are themselves
 * considered to be tokens. A token is thus either one delimiter character, or a maximal sequence of consecutive
 * characters that are not separator. </ul><p> A <tt>CSVTokenizer</tt> object internally maintains a current position
 * within the string to be tokenized. Some operations advance this current position past the characters processed.<p> A
 * token is returned by taking a substring of the string that was used to create the <tt>CSVTokenizer</tt> object.
 * <p/>
 * The following is one example of the use of the tokenizer. The code:
 * <blockquote><pre>
 *     CSVTokenizer csvt = new CSVTokenizer("this,is,a,test");
 *     while (csvt.hasMoreTokens()) {
 *         println(csvt.nextToken());
 *     }
 * </pre></blockquote>
 * <p/>
 * prints the following output:
 * <blockquote><pre>
 *     this
 *     is
 *     a
 *     test
 * </pre></blockquote>
 *
 * @author abupon
 */
public class CSVTokenizer implements Enumeration {
  /**
   * The complete record that should be separated into elements.
   */
  private String record;
  /**
   * The separator.
   */
  private String separator;
  /**
   * The quoting char.
   */
  private String quate;

  /**
   * the current parsing position.
   */
  private int currentIndex;

  /**
   * A flag indicating that the current parse position is before the start.
   */
  private boolean beforeStart;

  /**
   * A possible separator constant.
   */
  public static final String SEPARATOR_COMMA = ",";
  /**
   * A possible separator constant.
   */
  public static final String SEPARATOR_TAB = "\t";
  /**
   * A possible separator constant.
   */
  public static final String SEPARATOR_SPACE = " ";

  /**
   * A possible quote character constant.
   */
  public static final String DOUBLE_QUATE = "\"";
  /**
   * A possible quote character constant.
   */
  public static final String SINGLE_QUATE = "'";

  /**
   * Constructs a csv tokenizer for the specified string. <code>theSeparator</code> argument is the separator for
   * separating tokens.
   * <p/>
   * If the <code>returnSeparators</code> flag is <code>true</code>, then the separator string is also returned as
   * tokens. separator is returned as a string. If the flag is <code>false</code>, the separator string is skipped and
   * only serve as separator between tokens.
   *
   * @param aString      a string to be parsed.
   * @param theSeparator the separator (CSVTokenizer.SEPARATOR_COMMA, CSVTokenizer.TAB, CSVTokenizer.SPACE, etc.).
   * @param theQuate     the quate (CSVTokenizer.SINGLE_QUATE, CSVTokenizer.DOUBLE_QUATE, etc.).
   */
  public CSVTokenizer( final String aString,
                       final String theSeparator,
                       final String theQuate ) {
    this( aString, theSeparator, theQuate, true );
  }

  /**
   * Constructs a csv tokenizer for the specified string. <code>theSeparator</code> argument is the separator for
   * separating tokens.
   * <p/>
   * If the <code>returnSeparators</code> flag is <code>true</code>, then the separator string is also returned as
   * tokens. separator is returned as a string. If the flag is <code>false</code>, the separator string is skipped and
   * only serve as separator between tokens.
   *
   * @param aString      a string to be parsed.
   * @param theSeparator the separator (CSVTokenizer.SEPARATOR_COMMA, CSVTokenizer.TAB, CSVTokenizer.SPACE, etc.).
   * @param theQuate     the quate (CSVTokenizer.SINGLE_QUATE, CSVTokenizer.DOUBLE_QUATE, etc.).
   */
  public CSVTokenizer( final String aString,
                       final String theSeparator,
                       final String theQuate,
                       final boolean trim ) {
    if ( aString == null ) {
      throw new NullPointerException( "The given string is null" );
    }
    if ( theSeparator == null ) {
      throw new NullPointerException( "The given separator is null" );
    }
    if ( trim ) {
      this.record = aString.trim();
    } else {
      this.record = aString;
    }
    this.separator = theSeparator;
    this.quate = theQuate;
    this.currentIndex = 0;
    this.beforeStart = true;
  }

  /**
   * Constructs a csv tokenizer for the specified string. The characters in the <code>theSeparator</code> argument are
   * the separator for separating tokens. Separator string themselves will not be treated as tokens.
   *
   * @param aString      a string to be parsed.
   * @param theSeparator the separator (CSVTokenizer.SEPARATOR_COMMA, CSVTokenizer.TAB, CSVTokenizer.SPACE, etc.).
   */
  public CSVTokenizer( final String aString, final String theSeparator ) {
    this( aString, theSeparator, CSVTokenizer.DOUBLE_QUATE );
  }

  /**
   * Constructs a string tokenizer for the specified string. The tokenizer uses the default separator set, which is
   * <code>CSVTokenizer.SEPARATOR_COMMA</code>. Separator string themselves will not be treated as tokens.
   *
   * @param aString a string to be parsed.
   */
  public CSVTokenizer( final String aString ) {
    this( aString, CSVTokenizer.SEPARATOR_COMMA, CSVTokenizer.DOUBLE_QUATE, true );
  }

  /**
   * Constructs a string tokenizer for the specified string. The tokenizer uses the default separator set, which is
   * <code>CSVTokenizer.SEPARATOR_COMMA</code>. Separator string themselves will not be treated as tokens.
   *
   * @param aString a string to be parsed.
   */
  public CSVTokenizer( final String aString, final boolean trim ) {
    this( aString, CSVTokenizer.SEPARATOR_COMMA, CSVTokenizer.DOUBLE_QUATE, trim );
  }

  /**
   * Tests if there are more tokens available from this tokenizer's string. If this method returns <tt>true</tt>, then a
   * subsequent call to <tt>nextToken</tt> with no argument will successfully return a token.
   *
   * @return <code>true</code> if and only if there is at least one token in the string after the current position;
   * <code>false</code> otherwise.
   */
  public boolean hasMoreTokens() {
    return ( ( this.currentIndex < this.record.length() )
        && ( this.separator.length() > 0 ) );
  }

  /**
   * Returns the next token from this string tokenizer.
   *
   * @return the next token from this string tokenizer.
   * @throws NoSuchElementException   if there are no more tokens in this tokenizer's string.
   * @throws IllegalArgumentException if given parameter string format was wrong
   */
  public String nextToken()
    throws NoSuchElementException, IllegalArgumentException {

    if ( !this.hasMoreTokens() ) {
      throw new NoSuchElementException();
    }

    if ( beforeStart == false ) {
      currentIndex += this.separator.length();
    } else {
      beforeStart = false;
    }

    if ( this.quate != null && !this.quate.isEmpty()
        && this.record.startsWith( this.quate, this.currentIndex ) ) {
      final int quateLength = this.quate.length();
      int startOffset = this.currentIndex + quateLength;
      final StringBuilder b = new StringBuilder();
      while ( true ) {
        final int end = record.indexOf( this.quate, startOffset );
        if ( end < 0 ) {
          throw new IllegalArgumentException( "Illegal format" );
        }

        if ( record.startsWith( this.quate, end + 1 ) == false ) {
          b.append( record.substring( startOffset, end ) );
          currentIndex = end + 1;
          return b.toString();
        } else {
          b.append( record.substring( startOffset, end + 1 ) );
        }

        startOffset = end + quateLength * 2;
      }
    }

    final int end = this.record.indexOf( this.separator, this.currentIndex );
    if ( end >= 0 ) {
      final int start = this.currentIndex;
      final String token = this.record.substring( start, end );
      this.currentIndex = end;
      return token;
    } else {
      final int start = this.currentIndex;
      final String token = this.record.substring( start );
      this.currentIndex = this.record.length();
      return token;
    }
  }

  /**
   * Returns the next token in this string tokenizer's string. First, the set of characters considered to be separator
   * by this <tt>CSVTokenizer</tt> object is changed to be the characters in the string <tt>separator</tt>. Then the
   * next token in the string after the current position is returned. The current position is advanced beyond the
   * recognized token.  The new delimiter set remains the default after this call.
   *
   * @param theSeparator the new separator.
   * @return the next token, after switching to the new delimiter set.
   * @throws NoSuchElementException if there are no more tokens in this tokenizer's string.
   */
  public String nextToken( final String theSeparator ) {
    separator = theSeparator;
    return nextToken();
  }

  /**
   * Returns the same value as the <code>hasMoreTokens</code> method. It exists so that this class can implement the
   * <code>Enumeration</code> interface.
   *
   * @return <code>true</code> if there are more tokens; <code>false</code> otherwise.
   * @see Enumeration
   * @see CSVTokenizer#hasMoreTokens()
   */
  public boolean hasMoreElements() {
    return hasMoreTokens();
  }

  /**
   * Returns the same value as the <code>nextToken</code> method, except that its declared return value is
   * <code>Object</code> rather than <code>String</code>. It exists so that this class can implement the
   * <code>Enumeration</code> interface.
   *
   * @return the next token in the string.
   * @throws NoSuchElementException if there are no more tokens in this tokenizer's string.
   * @see Enumeration
   * @see CSVTokenizer#nextToken()
   */
  public Object nextElement() {
    return nextToken();
  }

  /**
   * Calculates the number of times that this tokenizer's <code>nextToken</code> method can be called before it
   * generates an exception. The current position is not advanced.
   *
   * @return the number of tokens remaining in the string using the current delimiter set.
   * @see CSVTokenizer#nextToken()
   */
  public int countTokens() {
    int count = 0;

    final int preserve = this.currentIndex;
    final boolean preserveStart = this.beforeStart;
    while ( this.hasMoreTokens() ) {
      this.nextToken();
      count++;
    }
    this.currentIndex = preserve;
    this.beforeStart = preserveStart;

    return count;
  }

  /**
   * Returns the quate.
   *
   * @return char
   */
  public String getQuate() {
    return this.quate;
  }

  /**
   * Sets the quate.
   *
   * @param quate The quate to set
   */
  public void setQuate( final String quate ) {
    this.quate = quate;
  }

  public String[] toArray() {
    final ArrayList<String> retval = new ArrayList<String>( 20 );
    while ( hasMoreElements() ) {
      retval.add( nextToken() );
    }
    return (String[]) retval.toArray();
  }
}
