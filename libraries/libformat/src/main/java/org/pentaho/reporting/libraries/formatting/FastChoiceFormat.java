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

import java.text.ChoiceFormat;
import java.util.Locale;

/**
 * A wrapper around the java.text.ChoiceFormat class. This wrapper limits the possible interactions with the wrapped
 * format class and therefore eliminates the need to clone the choice format whenever the wrapper is cloned.
 *
 * @author Thomas Morgner
 */
public class FastChoiceFormat implements FastFormat {
  private Locale locale;
  private String pattern;
  private ChoiceFormat choiceFormat;

  /**
   * Creates a new ChoiceFormat with the given pattern and the default locale.
   *
   * @param pattern the pattern.
   */
  public FastChoiceFormat( final String pattern ) {
    this( pattern, Locale.getDefault() );
  }

  /**
   * Creates a new ChoiceFormat with the given pattern and locale. As the local is not used anywhere, this has no impact
   * on the format itself.
   *
   * @param pattern the pattern.
   * @param locale  the locale.
   */
  public FastChoiceFormat( final String pattern, final Locale locale ) {
    if ( pattern == null ) {
      throw new NullPointerException();
    }
    if ( locale == null ) {
      throw new NullPointerException();
    }
    this.pattern = pattern;
    this.locale = locale;
    this.choiceFormat = new ChoiceFormat( pattern );
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
   * Formats the given object using the choice-pattern.
   *
   * @param parameters the parameters, usually a Number- or Date object.
   * @return the formatted text.
   */
  public String format( final Object parameters ) {
    return choiceFormat.format( parameters );
  }

  /**
   * Clones the object.
   *
   * @return the clone.
   * @throws CloneNotSupportedException if cloning failed for some reason.
   */
  public FastChoiceFormat clone() {
    try {
      final FastChoiceFormat format = (FastChoiceFormat) super.clone();
      format.choiceFormat = (ChoiceFormat) choiceFormat.clone();
      return format;
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException();
    }
  }
}
