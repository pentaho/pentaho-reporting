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
 * Creates a substring of the given text. If the expression has an ellipsis defined, and the given start position and
 * length are greater than the string's length, the ellipsis-text will be appended to indicate that this substring
 * result is only a partial match.
 * <p/>
 * A similiar functionality can be achived using the built-in formula support.
 *
 * @author Thomas Morgner
 * @deprecated Use a formula
 */
public class SubStringExpression extends AbstractExpression {
  /**
   * The field from where to read the string.
   */
  private String field;
  /**
   * The ellipse-text that marks partial results.
   */
  private String ellipsis;
  /**
   * The start-index position.
   */
  private int start;
  /**
   * The length of the substring.
   */
  private int length;

  /**
   * Default Constructor.
   */
  public SubStringExpression() {
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
   * Returns the ellipsis-text that indicates partial values.
   *
   * @return the ellipsis.
   */
  public String getEllipsis() {
    return ellipsis;
  }

  /**
   * Defines the ellipsis-text that indicates partial values.
   *
   * @param ellipsis
   *          the ellipsis.
   */
  public void setEllipsis( final String ellipsis ) {
    this.ellipsis = ellipsis;
  }

  /**
   * Returns the sub-string's start position.
   *
   * @return the start position.
   */
  public int getStart() {
    return start;
  }

  /**
   * Defines the sub-string's start position. The start position cannot be negative.
   *
   * @param start
   *          the start position.
   */
  public void setStart( final int start ) {
    if ( length < 0 ) {
      throw new IndexOutOfBoundsException( "String start position cannot be negative." );
    }
    this.start = start;
  }

  /**
   * Returns the sub-string's length.
   *
   * @return the length.
   */
  public int getLength() {
    return length;
  }

  /**
   * Defines the sub-string's length. The length cannot be negative.
   *
   * @param length
   *          the length.
   */
  public void setLength( final int length ) {
    if ( length < 0 ) {
      throw new IndexOutOfBoundsException( "String length cannot be negative." );
    }
    this.length = length;
  }

  /**
   * Computes the sub-string.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    final Object raw = getDataRow().get( getField() );
    if ( raw == null ) {
      return null;
    }
    final String text = String.valueOf( raw );

    // the text is not large enough to fit the start-bounds. Return nothing,
    // but indicate that there would have been some more content ...
    if ( start >= text.length() ) {
      return appendEllipsis( null );
    }

    // the text fully fits into the given range. No clipping needed ...
    if ( ( start + length ) > text.length() ) {
      return appendEllipsis( text.substring( start ) );
    }

    return text.substring( start, start + length );
  }

  /**
   * Appends the ellipsis, if one is defined.
   *
   * @param value
   *          the computed value (without the ellipsis).
   * @return the computed value with the ellipsis.
   */
  private String appendEllipsis( final String value ) {
    if ( ellipsis == null ) {
      return value;
    }
    if ( value == null ) {
      return ellipsis;
    }
    return value + ellipsis;
  }
}
