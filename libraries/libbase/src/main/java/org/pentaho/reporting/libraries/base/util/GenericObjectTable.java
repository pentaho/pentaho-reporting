/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.libraries.base.util;

/**
 * A generic table storing objects in an fast array backend. This generic class provides public getter and setters for
 * the contents of the table.
 *
 * @author Thomas Morgner
 */
public class GenericObjectTable<T> extends ObjectTable<T> {
  /**
   * A constant for serialization support.
   */
  private static final long serialVersionUID = 4870219010677984960L;

  /**
   * Creates a new object table.
   */
  public GenericObjectTable() {
  }

  /**
   * Creates a new table.
   *
   * @param increment the row and column size increment.
   */
  public GenericObjectTable( final int increment ) {
    super( Math.max( 1, increment ) );
  }

  /**
   * Creates a new table.
   *
   * @param rowIncrement the row size increment.
   * @param colIncrement the column size increment.
   */
  public GenericObjectTable( final int rowIncrement, final int colIncrement ) {
    super( Math.max( 1, rowIncrement ), Math.max( 1, colIncrement ) );
  }

  /**
   * Returns the object from a particular cell in the table. Returns null, if there is no object at the given position.
   * <p/>
   * Note: throws IndexOutOfBoundsException if row or column is negative.
   *
   * @param row    the row index (zero-based).
   * @param column the column index (zero-based).
   * @return The object.
   */
  public T getObject( final int row, final int column ) {
    return super.getObject( row, column );
  }

  /**
   * Sets the object for a cell in the table.  The table is expanded if necessary.
   *
   * @param row    the row index (zero-based).
   * @param column the column index (zero-based).
   * @param object the object.
   */
  public void setObject( final int row, final int column, final T object ) {
    super.setObject( row, column, object );
  }

  /**
   * Copys the contents of the old column to the new column.
   *
   * @param oldColumn the index of the old (source) column
   * @param newColumn the index of the new column
   */
  public void copyColumn( final int oldColumn, final int newColumn ) {
    super.copyColumn( oldColumn, newColumn );
  }

  /**
   * Copys the contents of the old row to the new row. This uses raw access to the data and is remarkably faster than
   * manual copying.
   *
   * @param oldRow the index of the old row
   * @param newRow the index of the new row
   */
  public void copyRow( final int oldRow, final int newRow ) {
    super.copyRow( oldRow, newRow );
  }
}

