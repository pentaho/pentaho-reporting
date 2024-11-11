/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
