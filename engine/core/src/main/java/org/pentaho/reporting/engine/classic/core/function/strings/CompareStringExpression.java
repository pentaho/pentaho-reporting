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

import org.pentaho.reporting.engine.classic.core.function.AbstractCompareExpression;

/**
 * Compares a given static string with a string read from a field.
 *
 * @author Thomas Morgner
 * @deprecated This can be done a lot easier using a simple formula.
 */
@SuppressWarnings( "deprecation" )
public class CompareStringExpression extends AbstractCompareExpression {
  /**
   * The static text.
   */
  private String text;

  /**
   * Default Constructor.
   */
  public CompareStringExpression() {
  }

  /**
   * Returns the static text to which the field's value should be compared to.
   *
   * @return the text.
   */
  public String getText() {
    return text;
  }

  /**
   * Defines the static text to which the field's value should be compared to.
   *
   * @param text
   *          the text.
   */
  public void setText( final String text ) {
    this.text = text;
  }

  /**
   * Returns the text.
   *
   * @return the text.
   */
  protected Comparable getComparable() {
    return text;
  }
}
