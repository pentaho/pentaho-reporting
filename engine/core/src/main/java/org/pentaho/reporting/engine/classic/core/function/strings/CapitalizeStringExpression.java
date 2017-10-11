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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.function.strings;

import org.pentaho.reporting.engine.classic.core.function.AbstractExpression;

/**
 * A expression that transforms all first letters of a given string into upper-case letters.
 *
 * @author Thomas Morgner
 * @deprecated Use a formula instead.
 */
public class CapitalizeStringExpression extends AbstractExpression {
  /**
   * The field name from where to read the string that should be capitalized.
   */
  private String field;
  /**
   * A flag indicating that only the first word should be capitalized.
   */
  private boolean firstWordOnly;

  /**
   * Default constructor.
   */
  public CapitalizeStringExpression() {
  }

  /**
   * Returns, whether only the first word should be capitalized.
   *
   * @return true, if the first word should be capitalized, false if all words should be capitalized.
   */
  public boolean isFirstWordOnly() {
    return firstWordOnly;
  }

  /**
   * Defines, whether only the first word should be capitalized.
   *
   * @param firstWordOnly
   *          true, if the first word should be capitalized, false if all words should be capitalized.
   */
  public void setFirstWordOnly( final boolean firstWordOnly ) {
    this.firstWordOnly = firstWordOnly;
  }

  /**
   * Returns the name of the datarow-column from where to read the string value.
   *
   * @return the field.
   */
  public String getField() {
    return field;
  }

  /**
   * Defines the name of the datarow-column from where to read the string value.
   *
   * @param field
   *          the field.
   */
  public void setField( final String field ) {
    this.field = field;
  }

  /**
   * Capitalizes the string that has been read from the defined field.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    final Object raw = getDataRow().get( getField() );
    if ( raw == null ) {
      return null;
    }
    final String text = String.valueOf( raw );
    final char[] textArray = text.toCharArray();

    boolean startOfWord = true;

    final int textLength = textArray.length;
    for ( int i = 0; i < textLength; i++ ) {
      final char c = textArray[i];
      // we ignore the punctutation chars or any other possible extra chars
      // for now. Words start at whitespaces ...
      if ( Character.isWhitespace( c ) ) {
        startOfWord = true;
      } else {
        if ( startOfWord == true ) {
          textArray[i] = Character.toTitleCase( c );
        }
        if ( firstWordOnly ) {
          break;
        }
        startOfWord = false;
      }
    }
    return new String( textArray );
  }
}
