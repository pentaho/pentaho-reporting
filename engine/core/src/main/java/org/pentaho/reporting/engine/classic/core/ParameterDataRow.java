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

package org.pentaho.reporting.engine.classic.core;

/**
 * A static data row that reads its values from a report properties collection. Changes to the report property
 * collection do not affect the parameter-data-row.
 *
 * @author Thomas Morgner
 */
public class ParameterDataRow extends StaticDataRow {
  private static final String[] EMPTY_NAMES = new String[0];

  private String[] outerNames;

  public ParameterDataRow() {
    this.outerNames = EMPTY_NAMES;
  }

  public ParameterDataRow( final String[] names, final Object[] values ) {
    if ( names == null ) {
      throw new NullPointerException();
    }
    if ( values == null ) {
      throw new NullPointerException();
    }
    if ( names.length != values.length ) {
      throw new NullPointerException();
    }

    final int length = names.length;
    System.arraycopy( values, 0, values, 0, length );
    setData( names, values );
    outerNames = names;
  }

  /**
   * Create a parameter data row from a master report's data row and a set of parameter mappings. The incoming
   * parameters can be aliased through the parameter mapping definitions.
   *
   * @param parameters
   *          the parameter mappings
   * @param dataRow
   *          the data row.
   */
  public ParameterDataRow( final ParameterMapping[] parameters, final DataRow dataRow ) {
    final int length = parameters.length;
    final String[] outerNames = new String[length];
    final String[] innerNames = new String[length];
    final Object[] values = new Object[length];
    for ( int i = 0; i < length; i++ ) {
      final ParameterMapping parameter = parameters[i];
      final String name = parameter.getName();
      innerNames[i] = parameter.getAlias();
      values[i] = dataRow.get( name );
      outerNames[i] = name;
    }
    this.outerNames = outerNames;
    setData( innerNames, values );
  }

  /**
   * Create a parameter data row from a master report's data row and a set of parameter mappings.
   *
   * @param dataRow
   *          the data row.
   */
  public ParameterDataRow( final DataRow dataRow ) {
    final String[] names = dataRow.getColumnNames();
    final int columnCount = names.length;
    final String[] innerNames = new String[columnCount];
    int nameCount = 0;
    for ( int i = 0; i < columnCount; i++ ) {
      final String innerName = names[i];
      if ( innerName == null ) {
        continue;
      }
      if ( contains( innerName, innerNames, nameCount - 1 ) == false ) {
        innerNames[nameCount] = innerName;
        nameCount += 1;
      }
    }

    final Object[] values = new Object[nameCount];
    for ( int i = 0; i < nameCount; i++ ) {
      values[i] = dataRow.get( innerNames[i] );
    }
    if ( values.length != innerNames.length ) {
      // some values have been filtered.
      this.outerNames = new String[values.length];
      System.arraycopy( innerNames, 0, outerNames, 0, values.length );
      setData( this.outerNames, values );
    } else {
      this.outerNames = innerNames;
      setData( innerNames, values );
    }
  }

  /**
   * A helper function that searches the given name if the provided array.
   *
   * @param name
   *          the name that is searched.
   * @param array
   *          the array containing all known names.
   * @param length
   *          the maximum number of elements in the given array that are valid.
   * @return true, if the name has been found, false otherwise.
   */
  private boolean contains( final String name, final String[] array, final int length ) {
    for ( int i = 0; i < length; i++ ) {
      if ( name.equals( array[i] ) ) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the names of the parameters as used in the master-report.
   *
   * @return the original names.
   */
  public String[] getParentNames() {
    return (String[]) outerNames.clone();
  }

}
