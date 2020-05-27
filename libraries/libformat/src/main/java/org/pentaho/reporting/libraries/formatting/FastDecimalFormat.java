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
* Copyright (c) 2008 - 2020 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formatting;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * A wrapper around the java.text.DecimalFormat class. This wrapper limits the possible interactions with the wrapped
 * format class and therefore eliminates the need to clone the choice format whenever the wrapper is cloned.
 *
 * @author Thomas Morgner
 */
public class FastDecimalFormat implements FastFormat {

  /**
   * A format-type constant indicating the system's default number format.
   */
  public static final int TYPE_DEFAULT = 0;
  /**
   * A format-type constant indicating the system's default integer format.
   */
  public static final int TYPE_INTEGER = 1;
  /**
   * A format-type constant indicating the system's default currency format.
   */
  public static final int TYPE_CURRENCY = 2;
  /**
   * A format-type constant indicating the system's default percentage format.
   */
  public static final int TYPE_PERCENT = 3;

  private Locale locale;
  private DecimalFormat decimalFormat;
  private String pattern;
  private transient StringBuffer buffer;
  private transient DummyFieldPosition fieldPosition;

  /**
   * Creates a new decimal-format for the given pattern.
   *
   * @param pattern the pattern string.
   */
  public FastDecimalFormat( final String pattern ) {
    this( pattern, Locale.getDefault() );
  }

  /**
   * Creates a new decimal-format for the given pattern and locale.
   *
   * @param pattern the pattern string.
   * @param locale  the locale.
   */
  public FastDecimalFormat( final String pattern, final Locale locale ) {
    if ( pattern == null ) {
      throw new NullPointerException();
    }
    if ( locale == null ) {
      throw new NullPointerException();
    }
    this.pattern = pattern;
    this.locale = locale;
    this.decimalFormat = new DecimalFormat( pattern, new DecimalFormatSymbols( locale ) );
    this.decimalFormat.setParseBigDecimal( true );
    this.decimalFormat.setRoundingMode( RoundingMode.HALF_UP );

  }

  /**
   * Creates a new number-format for the given type using the standard JDK methods.
   *
   * @param type   the type.
   * @param locale the locale for which the format shoudl be created.
   * @return the number format or null, if there was an error while creating the format.
   */
  private NumberFormat createFormat( final int type, final Locale locale ) {
    switch ( type ) {
      case TYPE_INTEGER: {
        return NumberFormat.getIntegerInstance( locale );
      }
      case TYPE_PERCENT: {
        return NumberFormat.getPercentInstance( locale );
      }
      case TYPE_CURRENCY: {
        return NumberFormat.getCurrencyInstance( locale );
      }
      default: {
        return NumberFormat.getInstance( locale );
      }
    }
  }

  /**
   * Creates a new date-format for the given default date and time style.
   *
   * @param type   the number-style, one of TYPE_INTEGER, TYPE_PERCENT, TYPE_CURRENCY or TYPE_DEFAULT.
   * @param locale the locale.
   * @throws IllegalArgumentException if both date and time-style are set to -1.
   */
  public FastDecimalFormat( final int type, final Locale locale ) {
    this( type, locale, false );
  }

  /**
   * Creates a new date-format for the given default date and time style.
   *
   * @param type   the number-style, one of TYPE_INTEGER, TYPE_PERCENT, TYPE_CURRENCY or TYPE_DEFAULT.
   * @param locale the locale.
   * @param useFormattingFromResources always use formatting specified in resources
   * @throws IllegalArgumentException if both date and time-style are set to -1.
   */
  public FastDecimalFormat( final int type, final Locale locale, final boolean useFormattingFromResources ) {
    if ( locale == null ) {
      throw new NullPointerException();
    }

    final NumberFormat rawFormat = createFormat( type, locale );
    if ( rawFormat instanceof DecimalFormat && !useFormattingFromResources ) {
      this.decimalFormat = (DecimalFormat) rawFormat;
      this.pattern = decimalFormat.toPattern();
      this.locale = locale;
    } else {
      final ResourceBundle patterns = ResourceBundle.getBundle( "org.pentaho.reporting.libraries.formatting.format-patterns" );

      switch ( type ) {
        case TYPE_INTEGER: {
          pattern = patterns.getString( "format.integer" );
          break;
        }
        case TYPE_PERCENT: {
          pattern = patterns.getString( "format.percentage" );
          break;
        }
        case TYPE_CURRENCY: {
          pattern = patterns.getString( "format.currency" );
          break;
        }
        default: {
          pattern = patterns.getString( "format.number" );
        }
      }

      this.locale = locale;
      this.decimalFormat = new DecimalFormat( pattern, new DecimalFormatSymbols( locale ) );
    }

    this.decimalFormat.setParseBigDecimal( true );
    this.decimalFormat.setRoundingMode( RoundingMode.HALF_UP );

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
   * Returns the currently active pattern.
   *
   * @return the locale.
   */
  public String getPattern() {
    return pattern;
  }

  /**
   * Formats the given object in a formatter-specific way.
   *
   * @param parameters the parameters for the formatting.
   * @return the formatted string.
   */
  public String format( final Object parameters ) {
    if ( parameters == null ) {
      throw new NullPointerException();
    }
    if ( parameters instanceof Number == false ) {
      throw new IllegalArgumentException( "Cannot format given Object as a Number" );
    }
    if ( buffer == null ) {
      buffer = new StringBuffer();
    } else {
      buffer.delete( 0, buffer.length() );
    }
    if ( fieldPosition == null ) {
      fieldPosition = new DummyFieldPosition();
    } else {
      fieldPosition.clear();
    }
    final StringBuffer stringBuffer = decimalFormat.format( parameters, buffer, new DummyFieldPosition() );
    return stringBuffer.toString();
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final FastDecimalFormat that = (FastDecimalFormat) o;

    if ( !locale.equals( that.locale ) ) {
      return false;
    }
    if ( !pattern.equals( that.pattern ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result = locale.hashCode();
    result = 31 * result + pattern.hashCode();
    return result;
  }

  /**
   * Clones the formatter.
   *
   * @return the clone.
   * @throws CloneNotSupportedException if cloning failed.
   */
  public Object clone() {
    try {
      final FastDecimalFormat format = (FastDecimalFormat) super.clone();
      format.decimalFormat = (DecimalFormat) decimalFormat.clone();
      return format;
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException();
    }
  }
}
