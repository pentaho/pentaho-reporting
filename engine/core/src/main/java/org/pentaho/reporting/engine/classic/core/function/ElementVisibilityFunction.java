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

package org.pentaho.reporting.engine.classic.core.function;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Triggers the visiblity of an element based on the boolean value read from the defined field.
 *
 * @author Thomas Morgner
 * @deprecated add a style-expression for the visible style-key instead.
 */
public class ElementVisibilityFunction extends AbstractElementFormatFunction {
  /**
   * The field name of the data-row column from where to read the boolean value.
   */
  private String field;

  /**
   * Default Constructor.
   */
  public ElementVisibilityFunction() {
  }

  /**
   * Returns the field name of the data-row column from where to read the boolean value.
   *
   * @return the field name.
   */
  public String getField() {
    return field;
  }

  /**
   * Defines field name of the data-row column from where to read the boolean value.
   *
   * @param field
   *          the name of the field.
   */
  public void setField( final String field ) {
    this.field = field;
  }

  /**
   * Returns whether the element will be visible or not.
   *
   * @return Boolean.TRUE or Boolean.FALSE.
   */
  public Object getValue() {
    if ( isVisible() ) {
      return Boolean.TRUE;
    } else {
      return Boolean.FALSE;
    }
  }

  protected boolean evaluateElement( final ReportElement e ) {
    if ( ObjectUtilities.equal( e.getName(), getElement() ) ) {
      e.getStyle().setStyleProperty( ElementStyleKeys.VISIBLE, isVisible() );
    }
    return true;
  }

  /**
   * Computes the visiblity of the element.
   *
   * @return true, if the field contains the Boolean.TRUE object, false otherwise.
   */
  protected boolean isVisible() {
    return Boolean.TRUE.equals( getDataRow().get( getField() ) );
  }
}
