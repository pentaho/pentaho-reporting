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

import java.text.Format;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.StringTokenizer;

/**
 * String utilities.
 *
 * @author Thomas Morgner.
 */
public final class StringUtils {
  // Constants used to convert a string to a boolean
  private static final String TRUE = "true";
  private static final String YES = "yes";
  private static final String ON = "on";

  /**
   * Private constructor prevents object creation.
   */
  private StringUtils() {
  }

  /**
   * Helper functions to query a strings start portion. The comparison is case insensitive.
   *
   * @param base  the base string.
   * @param start the starting text.
   * @return true, if the string starts with the given starting text.
   */
  public static boolean startsWithIgnoreCase( final String base, final String start ) {
    if ( base.length() < start.length() ) {
      return false;
    }
    return base.regionMatches( true, 0, start, 0, start.length() );
  }

  /**
   * Helper functions to query a strings end portion. The comparison is case insensitive.
   *
   * @param base the base string.
   * @param end  the ending text.
   * @return true, if the string ends with the given ending text.
   */
  public static boolean endsWithIgnoreCase( final String base, final String end ) {
    if ( base.length() < end.length() ) {
      return false;
    }
    return base.regionMatches( true, base.length() - end.length(), end, 0, end.length() );
  }

  /**
   * Queries the system properties for the line separator. If access to the System properties is forbidden, the UNIX
   * default is returned.
   *
   * @return the line separator.
   * @noinspection AccessOfSystemProperties
   */
  public static String getLineSeparator() {
    try {
      return System.getProperty( "line.separator", "\n" );
    } catch ( Exception e ) {
      return "\n";
    }
  }

  /**
   * Splits a given string on any whitespace character. Duplicate separators will be merged into a single separator
   * occurance. This implementation provides the same functionality as the REGEXP-based String.split(..) operation but
   * does not use Regular expressions and therefore it is faster and less memory consuming.
   *
   * @param string the text to be split.
   * @return the text elements as array.
   */
  public static String[] split( final String string ) {
    return split( string, " \t\n\r\f" );
  }

  /**
   * Splits a given string at the given separator string. Duplicate separators will be merged into a single separator
   * occurance.
   *
   * @param string    the text to be split.
   * @param separator the separator chacters used for the split.
   * @return the splitted array.
   */
  public static String[] split( final String string, final String separator ) {
    if ( separator == null ) {
      throw new NullPointerException( "Separator characters must not be null." );
    }
    if ( string == null ) {
      throw new NullPointerException( "String to be split must not be null." );
    }

    final StringTokenizer strtok = new StringTokenizer( string, separator, false );
    final String[] tokens = new String[ strtok.countTokens() ];
    int i = 0;
    while ( strtok.hasMoreTokens() ) {
      final String token = strtok.nextToken();
      tokens[ i ] = ( token );
      i += 1;
    }
    return tokens;
  }

  /**
   * Splits a given string at the given separator string. Duplicate separators will be merged into a single separator
   * occurance.
   *
   * @param string    the text to be split.
   * @param separator the separator chacters used for the split.
   * @param quate     the quoting character.
   * @return the splitted array.
   */
  public static String[] split( final String string, final String separator, final String quate ) {
    final CSVTokenizer strtok = new CSVTokenizer( string, separator, quate, false );
    final String[] tokens = new String[ strtok.countTokens() ];
    int i = 0;
    while ( strtok.hasMoreTokens() ) {
      final String token = strtok.nextToken();
      if ( token.length() > 0 ) {
        tokens[ i ] = ( token );
        i += 1;
      }
    }
    if ( i == tokens.length ) {
      return tokens;
    }

    final String[] retval = new String[ i ];
    System.arraycopy( tokens, 0, retval, 0, i );
    return retval;
  }

  /**
   * Splits a given string at the given separator string. Duplicate separators will result in empty strings thus
   * preserving the number of fields specified in the original string.
   *
   * @param string    the text to be split.
   * @param separator the separator chacters used for the split.
   * @return the splitted array.
   */
  public static String[] splitCSV( final String string, final String separator ) {
    return splitCSV( string, separator, null );
  }

  /**
   * Splits a given string at the given separator string. Duplicate separators will result in empty strings thus
   * preserving the number of fields specified in the original string.
   *
   * @param string    the text to be split.
   * @param separator the separator chacters used for the split.
   * @param quate     the quoting character.
   * @return the splitted array.
   */
  public static String[] splitCSV( final String string, final String separator, final String quate ) {
    final CSVTokenizer strtok = new CSVTokenizer( string, separator, quate, false );
    final String[] tokens = new String[ strtok.countTokens() ];
    int i = 0;
    while ( strtok.hasMoreTokens() ) {
      final String token = strtok.nextToken();
      tokens[ i ] = ( token );
      i += 1;
    }
    return tokens;
  }

  /**
   * Computes a unique name using the given known-names array as filter. This method is not intended for large
   * datasets.
   *
   * @param knownNames the list of known names.
   * @param pattern    the name pattern, which should have one integer slot to create derived names.
   * @return the unique name or null, if no unqiue name could be created.
   */
  public static String makeUniqueName( final String[] knownNames, final String pattern ) {
    final HashSet<String> knownNamesSet = new HashSet<String>( knownNames.length );
    for ( int i = 0; i < knownNames.length; i++ ) {
      final String name = knownNames[ i ];
      knownNamesSet.add( name );
    }

    final MessageFormat message = new MessageFormat( pattern );
    final Object[] objects = { "" };
    final String plain = message.format( objects );
    if ( knownNamesSet.contains( plain ) == false ) {
      return plain;
    }

    final Format[] formats = message.getFormats();
    if ( formats.length == 0 ) {
      // there is no variation in this name.
      return null;
    }

    int count = 1;
    while ( count < 2000000 ) {
      objects[ 0 ] = String.valueOf( count );
      final String testFile = message.format( objects );
      if ( knownNamesSet.contains( testFile ) == false ) {
        return testFile;
      }
      count += 1;
    }

    return null;
  }

  /**
   * Merges the contents of the first and second array returning a array that contains only unique strings. The order of
   * the returned array is undefined.
   *
   * @param first  the first array to be merged.
   * @param second the second array to be merged.
   * @return the merged araray.
   */
  public static String[] merge( final String[] first, final String[] second ) {
    if ( first.length == 0 ) {
      return second.clone();
    }
    if ( second.length == 0 ) {
      return first.clone();
    }
    final HashSet<String> total = new HashSet<String>( first.length + second.length );
    for ( int i = 0; i < first.length; i++ ) {
      total.add( first[ i ] );
    }
    for ( int i = 0; i < second.length; i++ ) {
      total.add( second[ i ] );
    }
    return total.toArray( new String[ total.size() ] );
  }

  /**
   * Returns <code>true</code> if the source string evaulates (case insensative and trimmed) to <code>true</code>,
   * <code>yes</code>, or <code>on</code>. It will return <code>false</code> otherwise (including <code>null</code>).
   *
   * @param source the string to check
   * @return <code>true</code> if the source string evaulates to <code>true</code> or similar value, <code>false</code>
   * otherwise.
   */
  public static boolean toBoolean( final String source ) {
    return toBoolean( source, false );
  }

  /**
   * Returns <code>true</code> if the source string evaulates (case insensative and trimmed) to <code>true</code>,
   * <code>yes</code>, or <code>on</code>. It will return <code>false otherwise. If the source string is
   * <code>null</code>, it will return the value of the default.
   *
   * @param source      the string to check
   * @param nullDefault to value to return if the source string is <code>null</code>
   * @return <code>true</code> if the source string evaulates to <code>true</code> or similar value, <code>false</code>
   * otherwise.
   */
  public static boolean toBoolean( String source, final boolean nullDefault ) {
    // If the source is null, use the default
    if ( source == null ) {
      return nullDefault;
    }

    // Check for valid values
    source = source.trim().toLowerCase();
    return ( TRUE.equals( source ) || YES.equals( source ) || ON.equals( source ) );
  }

  /**
   * Determines if the string is empty or <code>null</code>.
   *
   * @param source the string to check
   * @return <code>true</code> if the source string is <code>null</code> or an emptry string, <code>false</code>
   * otherwise.
   */
  public static boolean isEmpty( final String source ) {
    return isEmpty( source, true );
  }


  /**
   * Determines if the string is empty or <code>null</code>. If the </code>trim</code> is <code>true</code>, the string
   * will be trimmed before checking for an empty string.
   *
   * @param source the string to check
   * @param trim   indicates if the string should be trimmed before checking for an empty string.
   * @return <code>true</code> if the source string is <code>null</code> or an emptry string, <code>false</code>
   * otherwise.
   */
  public static boolean isEmpty( final String source, final boolean trim ) {
    if ( source == null ) {
      return true;
    }
    if ( source.length() == 0 ) {
      return true;
    }
    if ( trim == false ) {
      return false;
    }
    final char[] chars = source.toCharArray();
    for ( int i = 0; i < chars.length; i++ ) {
      final char c = chars[ i ];
      if ( c > ' ' ) {
        return false;
      }
    }
    return true;
  }

  /**
   * Determines if the two Strings are equals (taking nulls into account).
   *
   * @param s1 the first string to compare.
   * @param s2 the second string to compare.
   * @return <code>true</code> if both string are null or the contain the same value, <code>false</code>otherwise
   */
  public static boolean equals( final String s1, final String s2 ) {
    return ( ( s1 == null && s2 == null ) || ( s1 != null && s1.equals( s2 ) ) );
  }

  /**
   * Determines if the two Strings are equals ingnoring case sensitivity (taking nulls into account).
   *
   * @param s1 the first string to compare.
   * @param s2 the second string to compare.
   * @return <code>true</code> if both string are null or the contain the same case-insensitive value,
   * <code>false</code>otherwise
   */
  public static boolean equalsIgnoreCase( final String s1, final String s2 ) {
    return ( ( s1 == null && s2 == null ) || ( s1 != null && s1.equalsIgnoreCase( s2 ) ) );
  }

  /**
   * Checks whether or not a String consists only of spaces.
   *
   * @param str The string to check
   * @return true if the string has nothing but spaces.
   */
  public static final boolean onlySpaces( String str ) {
    for ( int i = 0; i < str.length(); i++ ) {
      if ( !isSpace( str.charAt( i ) ) ) {
        return false;
      }
    }
    return true;
  }

  /**
   * Determines whether or not a character is considered a space. A character is considered a space in Kettle if it is a
   * space, a tab, a newline or a cariage return.
   *
   * @param c The character to verify if it is a space.
   * @return true if the character is a space. false otherwise.
   */
  public static final boolean isSpace( char c ) {
    return c == ' ' || c == '\t' || c == '\r' || c == '\n' || Character.isWhitespace( c );
  }

  /**
   * Trims a string: removes the leading and trailing spaces of a String.
   *
   * @param str The string to trim
   * @return The trimmed string.
   */
  public static final String trim( String str ) {
    if ( str == null ) {
      return null;
    }

    int max = str.length() - 1;
    int min = 0;

    while ( min <= max && isSpace( str.charAt( min ) ) ) {
      min++;
    }
    while ( max >= 0 && isSpace( str.charAt( max ) ) ) {
      max--;
    }

    if ( max < min ) {
      return "";
    }

    return str.substring( min, max + 1 );
  }
}
