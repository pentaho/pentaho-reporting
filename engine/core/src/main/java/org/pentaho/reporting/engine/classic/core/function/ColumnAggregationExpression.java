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

import org.pentaho.reporting.engine.classic.core.DataRow;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The base-class for all expressions that aggregate values from multiple columns.
 *
 * @author Thomas Morgner
 */
public abstract class ColumnAggregationExpression extends AbstractExpression {
  /**
   * An ordered list containing the fieldnames used in the expression.
   */
  private ArrayList<String> fields;
  /**
   * A temporary array to reduce the number of object creations.
   */
  private transient Object[] fieldValues;

  /**
   * Default Constructor.
   */
  protected ColumnAggregationExpression() {
    fields = new ArrayList<String>();
  }

  /**
   * Collects the values of all fields defined in the fieldList.
   *
   * @return an Object-array containing all defined values from the datarow
   */
  protected Object[] getFieldValues() {
    final int size = fields.size();
    if ( fieldValues == null || fieldValues.length != size ) {
      fieldValues = new Object[size];
    }

    final DataRow dataRow = getDataRow();
    for ( int i = 0; i < size; i++ ) {
      final String field = fields.get( i );
      if ( field != null ) {
        fieldValues[i] = dataRow.get( field );
      }
    }
    return fieldValues;
  }

  /**
   * Defines the field in the field-list at the given index.
   *
   * @param index
   *          the position in the list, where the field should be defined.
   * @param field
   *          the name of the field.
   */
  public void setField( final int index, final String field ) {
    if ( fields.size() == index ) {
      fields.add( field );
    } else {
      fields.set( index, field );
    }
    this.fieldValues = null;
  }

  /**
   * Returns the defined field at the given index-position.
   *
   * @param index
   *          the position of the field name that should be queried.
   * @return the field name at the given position.
   */
  public String getField( final int index ) {
    return fields.get( index );
  }

  /**
   * Returns the number of fields defined in this expression.
   *
   * @return the number of fields.
   */
  public int getFieldCount() {
    return fields.size();
  }

  /**
   * Returns all defined fields as array of strings.
   *
   * @return all the fields.
   */
  public String[] getField() {
    return fields.toArray( new String[fields.size()] );
  }

  /**
   * Defines all fields as array. This completely replaces any previously defined fields.
   *
   * @param fields
   *          the new list of fields.
   */
  public void setField( final String[] fields ) {
    this.fields.clear();
    this.fields.addAll( Arrays.asList( fields ) );
    this.fieldValues = null;
  }

  /**
   * Return a completly separated copy of this function. The copy does no longer share any changeable objects with the
   * original function.
   *
   * @return a copy of this function.
   */
  public Expression getInstance() {
    final ColumnAggregationExpression cae = (ColumnAggregationExpression) super.getInstance();
    cae.fields = (ArrayList) fields.clone();
    return cae;
  }
}
