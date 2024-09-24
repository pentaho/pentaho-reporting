/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.designtime.datafactory;

import java.util.Iterator;

import javax.swing.DefaultComboBoxModel;

public class DataSetComboBoxModel<T> extends DefaultComboBoxModel implements Iterable<DataSetQuery<T>> {
  private class ComboBoxModelIterator implements Iterator<DataSetQuery<T>> {
    private int index;

    private ComboBoxModelIterator() {
      this.index = 0;
    }

    public boolean hasNext() {
      return index < getSize();
    }

    public DataSetQuery<T> next() {
      final DataSetQuery<T> value = (DataSetQuery<T>) getElementAt( index );
      index += 1;
      return value;
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  public DataSetComboBoxModel() {
  }

  public DataSetQuery<T> getSelectedQuery() {
    return (DataSetQuery<T>) getSelectedItem();
  }

  public void setSelectedItem( final Object anObject ) {
    super.setSelectedItem( anObject );
  }

  public DataSetQuery<T> getQuery( final int index ) {
    return (DataSetQuery<T>) getElementAt( index );
  }

  public Iterator<DataSetQuery<T>> iterator() {
    return new ComboBoxModelIterator();
  }

  public int getIndexForQuery( final String name ) {
    for ( int i = 0; i < getSize(); i++ ) {
      final DataSetQuery elementAt = getQuery( i );
      if ( elementAt.getQueryName().equals( name ) ) {
        return i;
      }
    }
    return -1;
  }

  public void fireItemChanged( final Object item ) {
    for ( int i = 0; i < getSize(); i++ ) {
      if ( getElementAt( i ) == item ) {
        fireContentsChanged( this, i, i );
        return;
      }
    }
  }
}
