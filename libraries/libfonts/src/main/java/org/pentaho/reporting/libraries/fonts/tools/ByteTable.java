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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.fonts.tools;

import java.io.Serializable;
import java.util.Arrays;

/**
 * A lookup table for objects. This implementation is not synchronized, it is up to the caller to synchronize it
 * properly.
 *
 * @author Thomas Morgner
 */
public class ByteTable implements Serializable {
  /**
   * For serialization.
   */
  private static final long serialVersionUID = -276004279213053063L;

  /**
   * The number of rows.
   */
  private int rows;

  /**
   * The number of columns.
   */
  private int columns;

  /**
   * An array of objects.  The array may contain <code>null</code> values.
   */
  private byte[][] data;

  /**
   * Creates a new table.
   *
   * @param rows the inital number of rows.
   * @param cols the initial number of columns.
   */
  public ByteTable( final int rows, final int cols ) {
    if ( rows < 1 ) {
      throw new IllegalArgumentException( "Increment must be positive." );
    }

    if ( cols < 1 ) {
      throw new IllegalArgumentException( "Increment must be positive." );
    }

    this.rows = rows;
    this.columns = cols;

    this.data = new byte[ rows ][];
  }

  /**
   * Ensures that there is storage capacity for the specified item.
   *
   * @param row    the row index.
   * @param column the column index.
   */
  public void ensureCapacity( final int row, final int column ) {

    if ( row < 0 || row >= this.rows ) {
      throw new IndexOutOfBoundsException( "Row is invalid. " + row );
    }
    if ( column < 0 || column >= this.columns ) {
      throw new IndexOutOfBoundsException( "Column is invalid. " + column );
    }

    final byte[] current = this.data[ row ];
    if ( current == null ) {
      this.data[ row ] = new byte[ Math.max( column + 1, this.columns ) ];
    }
  }

  /**
   * Returns the number of rows in the table.
   *
   * @return The row count.
   */
  public int getRowCount() {
    return this.rows;
  }

  /**
   * Returns the number of columns in the table.
   *
   * @return The column count.
   */
  public int getColumnCount() {
    return this.columns;
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
  public byte getByte( final int row, final int column, final byte defaultValue ) {
    if ( row < this.data.length ) {
      final byte[] current = this.data[ row ];
      if ( current == null ) {
        return defaultValue;
      }
      if ( column < current.length ) {
        return current[ column ];
      }
    }
    return defaultValue;

  }

  /**
   * Sets the object for a cell in the table.  The table is expanded if necessary.
   *
   * @param row    the row index (zero-based).
   * @param column the column index (zero-based).
   * @param object the object.
   */
  public void setByte( final int row, final int column, final byte object ) {

    ensureCapacity( row, column );
    this.data[ row ][ column ] = object;
  }

  /**
   * Tests this paint table for equality with another object (typically also an <code>ObjectTable</code>).
   *
   * @param o the other object.
   * @return A boolean.
   */
  public boolean equals( final Object o ) {

    if ( o == null ) {
      return false;
    }

    if ( this == o ) {
      return true;
    }

    if ( ( o instanceof ByteTable ) == false ) {
      return false;
    }

    final ByteTable ot = (ByteTable) o;
    if ( getRowCount() != ot.getRowCount() ) {
      return false;
    }

    if ( getColumnCount() != ot.getColumnCount() ) {
      return false;
    }

    for ( int r = 0; r < getRowCount(); r++ ) {
      for ( int c = 0; c < getColumnCount(); c++ ) {
        if ( getByte( r, c, (byte) -1 ) == ot.getByte( r, c, (byte) -1 ) == false ) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Returns a hash code value for the object.
   *
   * @return the hashcode
   */
  public int hashCode() {
    int result = this.rows;
    result = 29 * result + this.columns;
    return result;
  }

  /**
   * Clears the table.
   */
  public void clear( final byte value ) {
    this.rows = 0;
    this.columns = 0;
    final int dataLength = this.data.length;
    for ( int i = 0; i < dataLength; i++ ) {
      if ( this.data[ i ] != null ) {
        Arrays.fill( this.data[ i ], value );
      }
    }
  }
  //
  //  protected void setData(final byte[][] data, final int colCount)
  //  {
  //    if (data == null) {
  //      throw new NullPointerException();
  //    }
  //    if (colCount < 0) {
  //      throw new IndexOutOfBoundsException();
  //    }
  //
  //    this.data = data;
  //    this.rows = data.length;
  //    this.columns = colCount;
  //  }

  protected byte[][] getData() {
    return data;
  }
}

