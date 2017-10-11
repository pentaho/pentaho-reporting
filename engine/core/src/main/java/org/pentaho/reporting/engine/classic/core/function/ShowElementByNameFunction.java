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

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This function hiddes the elements with the name specified in the 'element' parameter, if the given field has one of
 * the values specified in the values array.
 *
 * @author Thomas Morgner
 * @deprecated This can be done easier using style-expressions
 */
@SuppressWarnings( "deprecation" )
public class ShowElementByNameFunction extends AbstractElementFormatFunction {
  /**
   * The field from where to read the compare value.
   */
  private String field;
  /**
   * The list of values.
   */
  private ArrayList values;

  /**
   * Default Constructor.
   */
  public ShowElementByNameFunction() {
    values = new ArrayList();
  }

  /**
   * Returns the name of the field from where the compare value is read.
   *
   * @return the name of the field.
   */
  public String getField() {
    return field;
  }

  /**
   * Defines the name of the field from where the compare value is read.
   *
   * @param field
   *          the name of the field.
   */
  public void setField( final String field ) {
    this.field = field;
  }

  /**
   * Defines one of the values that hide the element. This defines the value at the given index in the list.
   *
   * @param value
   *          the compare value.
   * @param index
   *          the position in the list of all values.
   */
  public void setValues( final int index, final Object value ) {
    if ( values.size() == index ) {
      values.add( value );
    } else {
      values.set( index, value );
    }
  }

  /**
   * Returns one of the values that hide the element. This returns the defined value at the given index in the list.
   *
   * @param index
   *          the position in the list of all values.
   * @return the value at the given position.
   */
  public Object getValues( final int index ) {
    return values.get( index );
  }

  /**
   * Returns all known compare values as array.
   *
   * @return the values as array.
   */
  public Object[] getValues() {
    return values.toArray();
  }

  /**
   * Defines all values using the object from the value-array.
   *
   * @param values
   *          the new list of compare values.
   */
  public void setValues( final Object[] values ) {
    this.values.clear();
    this.values.addAll( Arrays.asList( values ) );
  }

  /**
   * Returns the number of values in the compare list.
   *
   * @return the number of values.
   */
  public int getValuesCount() {
    return this.values.size();
  }

  protected boolean evaluateElement( final ReportElement e ) {
    if ( ObjectUtilities.equal( e.getName(), getElement() ) ) {
      final boolean visible = isVisible();
      e.getStyle().setStyleProperty( ElementStyleKeys.VISIBLE, visible );
      return true;
    }
    return false;
  }

  /**
   * Computes the visiblity.
   *
   * @return true, if the field value matches, false otherwise.
   */
  private boolean isVisible() {
    final Object fieldValue = getDataRow().get( getField() );
    for ( int i = 0; i < values.size(); i++ ) {
      final Object o = values.get( i );
      if ( ObjectUtilities.equal( fieldValue, o ) ) {
        return false;
      }
    }
    return true;
  }

  /**
   * Return a completly separated copy of this function. The copy does no longer share any changeable objects with the
   * original function.
   *
   * @return a copy of this function.
   */
  public ShowElementByNameFunction getInstance() {
    final ShowElementByNameFunction ex = (ShowElementByNameFunction) super.getInstance();
    ex.values = (ArrayList) values.clone();
    return ex;
  }

}
