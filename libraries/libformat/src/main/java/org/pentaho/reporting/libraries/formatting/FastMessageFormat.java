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
* Copyright (c) 2008 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formatting;

import org.pentaho.reporting.libraries.base.util.ArgumentNullException;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A wrapper around the java.text.MessageFormat class. This wrapper limits the possible interactions with the wrapped
 * format class and therefore eliminates the need to clone the choice format whenever the wrapper is cloned.
 * <p/>
 * The pattern accepted by the this class is the same as the message-format pattern, with the exception that this class
 * allows to escape the special characters using the backslash-character. Unlike the original MessageFormat class, this
 * class allows to set a null-string for parameters that are null.
 *
 * @author Thomas Morgner
 */
public class FastMessageFormat implements FastFormat {
  private String pattern;
  private Locale locale;
  private FastFormat[] subFormats;
  private String[] constantTexts;
  private int[] argumentMapping;

  private int sizeHint;
  private String nullString;
  private transient StringBuffer buffer;
  private DummyFieldPosition fieldPosition;
  private TimeZone timeZone;

  /**
   * Creates a new default message format object for the given pattern using the default locale as locale.
   *
   * @param pattern the pattern.
   */
  public FastMessageFormat( final String pattern ) {
    this( pattern, Locale.getDefault() );
  }

  /**
   * Creates a new default message format object for the given pattern and locale.
   *
   * @param pattern the pattern.
   * @param locale  the locale.
   */
  public FastMessageFormat( final String pattern, final Locale locale ) {
    this( pattern, locale, TimeZone.getDefault() );
  }

  public FastMessageFormat( final String pattern, final Locale locale, final TimeZone timeZone ) {
    ArgumentNullException.validate( "timeZone", timeZone );
    ArgumentNullException.validate( "pattern", pattern );
    ArgumentNullException.validate( "locale", locale );

    this.pattern = pattern;
    this.locale = locale;
    this.nullString = "<null>";
    this.timeZone = timeZone;

    final String[] arguments = new String[ 3 ];
    int argumentIndex = 0;
    int stackDepth = 0;
    boolean escape = false;

    final ArrayList<String> constants = new ArrayList<String>();
    final ArrayList<FastFormat> patterns = new ArrayList<FastFormat>();
    final ArrayList<Integer> indexMappings = new ArrayList<Integer>();
    final StringBuilder b = new StringBuilder( pattern.length() );
    final char[] chars = this.pattern.toCharArray();
    for ( int i = 0; i < chars.length; i++ ) {
      final char c = chars[ i ];
      if ( escape == true ) {
        b.append( c );
        escape = false;
        continue;
      }

      switch( c ) {
        case '{': {
          if ( stackDepth == 0 ) {
            argumentIndex = 0;
            arguments[ 0 ] = null;
            arguments[ 1 ] = null;
            arguments[ 2 ] = null;
            constants.add( b.toString() );
            this.sizeHint += b.length();
            b.delete( 0, b.length() );
          } else {
            b.append( '{' );
          }
          stackDepth += 1;
          break;
        }
        case '}': {
          stackDepth -= 1;
          if ( stackDepth < 0 ) {
            throw new IllegalArgumentException( "Invalid pattern; curly braces do not match at position: " + i );
          }
          if ( stackDepth == 0 ) {
            arguments[ argumentIndex ] = b.toString();
            b.delete( 0, b.length() );

            final String argIndexString = arguments[ 0 ];
            if ( argIndexString == null ) {
              throw new IllegalArgumentException( "Invalid pattern; no argument index for pattern ending at: " + i );
            }
            try {
              indexMappings.add( new Integer( argIndexString ) );
              final String argTypeRaw = arguments[ 1 ];
              final String argPattern = arguments[ 2 ];
              patterns.add( createFormatter( locale, argTypeRaw, argPattern ) );
            } catch ( NumberFormatException nfe ) {
              throw new IllegalArgumentException( "Invalid pattern; argument index is no number: " + i );
            }

            continue;
          } else {
            b.append( '}' );
          }
          break;
        }
        case ',': {
          if ( stackDepth == 1 && argumentIndex < 2 ) {
            // separator ..
            arguments[ argumentIndex ] = b.toString();
            b.delete( 0, b.length() );
            argumentIndex += 1;
          } else {
            b.append( c );
          }
          break;
        }
        case '\\': {
          escape = true;
          break;
        }
        default: {
          b.append( c );
        }
      }
    }

    this.sizeHint += b.length();
    constants.add( b.toString() );

    if ( stackDepth != 0 ) {
      throw new IllegalArgumentException( "Invalid pattern; curly braces do not match" );
    }

    this.constantTexts = constants.toArray( new String[ constants.size() ] );
    this.argumentMapping = new int[ indexMappings.size() ];
    for ( int i = 0; i < indexMappings.size(); i++ ) {
      final Integer integer = indexMappings.get( i );
      argumentMapping[ i ] = integer.intValue();
    }
    this.subFormats = patterns.toArray( new FastFormat[ patterns.size() ] );
    this.sizeHint += argumentMapping.length * 5;
  }

  /**
   * Creates a sub-formatter for the given raw-type and raw-pattern. The formatter will be initialized with the locale
   * given.
   *
   * @param locale     the locale for the new sub-formatter.
   * @param argTypeRaw the type, one of "time", "date", "datetime", "number" or "choice".
   * @param argPattern the type-specific raw pattern.
   * @return the creates format or null, if the raw format did not match anything valid.
   */
  private FastFormat createFormatter( final Locale locale, final String argTypeRaw, final String argPattern ) {
    if ( argTypeRaw == null ) {
      return null;
    }
    final String trimmedType = argTypeRaw.trim();
    if ( "time".equals( trimmedType ) ) {
      if ( "short".equals( argPattern ) ) {
        return new FastDateFormat( 0, DateFormat.SHORT, locale, timeZone );
      } else if ( "medium".equals( argPattern ) ) {
        return new FastDateFormat( 0, DateFormat.MEDIUM, locale, timeZone );
      } else if ( "long".equals( argPattern ) ) {
        return new FastDateFormat( 0, DateFormat.LONG, locale, timeZone );
      } else if ( "full".equals( argPattern ) ) {
        return new FastDateFormat( 0, DateFormat.FULL, locale, timeZone );
      } else {
        if ( argPattern == null ) {
          return new FastDateFormat( 0, DateFormat.MEDIUM, locale, timeZone );
        } else {
          return new FastDateFormat( argPattern, locale, timeZone );
        }
      }
    } else if ( "date".equals( trimmedType ) ) {
      if ( "short".equals( argPattern ) ) {
        return new FastDateFormat( DateFormat.SHORT, 0, locale, timeZone );
      } else if ( "medium".equals( argPattern ) ) {
        return new FastDateFormat( DateFormat.MEDIUM, 0, locale, timeZone );
      } else if ( "long".equals( argPattern ) ) {
        return new FastDateFormat( DateFormat.LONG, 0, locale, timeZone );
      } else if ( "full".equals( argPattern ) ) {
        return new FastDateFormat( DateFormat.FULL, 0, locale, timeZone );
      } else {
        if ( argPattern == null ) {
          return new FastDateFormat( DateFormat.MEDIUM, 0, locale, timeZone );
        } else {
          return new FastDateFormat( argPattern, locale, timeZone );
        }
      }
    } else if ( "datetime".equals( trimmedType ) ) {
      if ( "short".equals( argPattern ) ) {
        return new FastDateFormat( DateFormat.SHORT, DateFormat.SHORT, locale, timeZone );
      } else if ( "medium".equals( argPattern ) ) {
        return new FastDateFormat( DateFormat.MEDIUM, DateFormat.MEDIUM, locale, timeZone );
      } else if ( "long".equals( argPattern ) ) {
        return new FastDateFormat( DateFormat.LONG, DateFormat.LONG, locale, timeZone );
      } else if ( "full".equals( argPattern ) ) {
        return new FastDateFormat( DateFormat.FULL, DateFormat.FULL, locale, timeZone );
      } else {
        if ( argPattern == null ) {
          return new FastDateFormat( 0, DateFormat.MEDIUM, locale, timeZone );
        } else {
          return new FastDateFormat( argPattern, locale, timeZone );
        }
      }


    } else if ( "number".equals( trimmedType ) ) {
      if ( "currency".equals( argPattern ) ) {
        return new FastDecimalFormat( FastDecimalFormat.TYPE_CURRENCY, locale );
      } else if ( "percent".equals( argPattern ) ) {
        return new FastDecimalFormat( FastDecimalFormat.TYPE_PERCENT, locale );
      } else if ( "integer".equals( argPattern ) ) {
        return new FastDecimalFormat( FastDecimalFormat.TYPE_INTEGER, locale );
      } else {
        if ( argPattern == null ) {
          return new FastDecimalFormat( FastDecimalFormat.TYPE_DEFAULT, locale );
        } else {
          return new FastDecimalFormat( argPattern, locale );
        }
      }
    } else if ( "choice".equals( trimmedType ) ) {
      return new FastChoiceFormat( argPattern, locale );
    } else {
      return null;
    }
  }

  /**
   * Returns the number of subformats in the message-formatter.
   *
   * @return the number of subformats.
   */
  public int getSubFormatCount() {
    return subFormats.length;
  }

  /**
   * Returns the subformat at the given index.
   *
   * @param index the index.
   * @return a clone of the fast-format or null, if there is no formatter at that position.
   */
  protected FastFormat getSubFormat( final int index ) {
    final FastFormat fastFormat = subFormats[ index ];
    if ( fastFormat != null ) {
      try {
        return (FastFormat) fastFormat.clone();
      } catch ( CloneNotSupportedException e ) {
        throw new IllegalStateException();
      }
    }
    return null;
  }

  /**
   * Returns the current locale of the formatter.
   *
   * @return the current locale, never null.
   */
  public Locale getLocale() {
    return locale;
  }

  /**
   * Returns the current time-zone for date formats.
   *
   * @return the current time zone, never null.
   */
  public TimeZone getTimeZone() {
    return timeZone;
  }

  /**
   * Returns the currently active pattern.
   *
   * @return the locale.
   */
  public String getPattern() {
    return pattern;
  }

  /**
   * Returns the subformats of this message-format.
   *
   * @return the subformats as deeply cloned array.
   */
  protected FastFormat[] getSubFormats() {
    try {
      final FastFormat[] retval = new FastFormat[ subFormats.length ];
      for ( int i = 0; i < subFormats.length; i++ ) {
        final FastFormat fastFormat = subFormats[ i ];
        if ( fastFormat != null ) {
          retval[ i ] = (FastFormat) fastFormat.clone();
        }
      }
      return retval;
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException( "Should not happen" );
    }
  }

  /**
   * Redefines the subformats of this message-format.
   *
   * @param subFormats the subformats.
   */
  protected void setSubFormats( final FastFormat[] subFormats ) {
    if ( subFormats == null ) {
      throw new NullPointerException();
    }
    if ( subFormats.length != this.subFormats.length ) {
      throw new IllegalArgumentException();
    }
    try {
      for ( int i = 0; i < subFormats.length; i++ ) {
        final FastFormat fastFormat = subFormats[ i ];
        if ( fastFormat != null ) {
          this.subFormats[ i ] = (FastFormat) fastFormat.clone();
        }
      }
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException( "Should not happen" );
    }
  }

  /**
   * Returns the null-string that is used whenever a parameter object is null.
   *
   * @return the nullstring.
   */
  public String getNullString() {
    return nullString;
  }

  /**
   * Defines the null-string that is used whenever a parameter object is null.
   *
   * @param nullString the nullstring, never null in itself.
   */
  public void setNullString( final String nullString ) {
    if ( nullString == null ) {
      throw new NullPointerException();
    }
    this.nullString = nullString;
  }

  /**
   * Formats the given object in a formatter-specific way.
   *
   * @param parameters the parameters for the formatting.
   * @return the formatted string.
   */
  public String format( final Object parameters ) {
    if ( parameters instanceof Object[] == false ) {
      throw new IllegalArgumentException();
    }
    final Object[] parameterArray = (Object[]) parameters;
    if ( subFormats.length == 0 ) {
      return constantTexts[ 0 ];
    }

    if ( buffer == null ) {
      buffer = new StringBuffer( sizeHint );
    } else {
      buffer.delete( 0, buffer.length() );
    }

    for ( int i = 0; i < subFormats.length; i++ ) {
      final FastFormat format = subFormats[ i ];
      buffer.append( constantTexts[ i ] );
      final Object value = parameterArray[ argumentMapping[ i ] ];
      if ( value == null ) {
        buffer.append( nullString );
      } else if ( format == null ) {
        buffer.append( String.valueOf( value ) );
      } else if ( format instanceof FastChoiceFormat ) {
        final String formatStr = format.format( value );
        final FastMessageFormat fastMessageFormat = new FastMessageFormat( formatStr, locale );
        buffer.append( fastMessageFormat.format( parameters ) );
      } else {
        buffer.append( format.format( value ) );
      }
    }
    buffer.append( constantTexts[ subFormats.length ] );
    if ( buffer.length() > sizeHint ) {
      this.sizeHint = buffer.length();
    }
    return buffer.toString();
  }

  /**
   * Clones the formatter.
   *
   * @return the clone.
   * @throws CloneNotSupportedException if cloning failed.
   */
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
