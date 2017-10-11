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

import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.function.AbstractExpression;

/**
 * A expression that transforms all letters of a given string into lower-case letters.
 *
 * @author Thomas Morgner
 * @deprecated This can be solved with a formula
 */
public class ToLowerCaseStringExpression extends AbstractExpression {
  /**
   * The field name from where to read the string that should be converted to lower case.
   */
  private String field;

  /**
   * Default Constructor.
   */
  public ToLowerCaseStringExpression() {
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
   * Transforms the string that has been read from the defined field.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    final Object raw = getDataRow().get( getField() );
    if ( raw == null ) {
      return null;
    }
    final String text = String.valueOf( raw );
    final ResourceBundleFactory rf = getResourceBundleFactory();
    if ( rf == null ) {
      return text.toLowerCase();
    } else {
      return text.toLowerCase( rf.getLocale() );
    }
  }
}
