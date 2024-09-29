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


package org.pentaho.reporting.engine.classic.core.sorting;

import org.pentaho.reporting.engine.classic.core.MetaTableModel;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;

import javax.swing.event.TableModelListener;

public abstract class IndexedMetaTableModel implements MetaTableModel {
  private MetaTableModel backend;

  public IndexedMetaTableModel( final MetaTableModel backend ) {
    this.backend = backend;
  }

  protected abstract int mapRow( final int row );

  public boolean isCellDataAttributesSupported() {
    return backend.isCellDataAttributesSupported();
  }

  public DataAttributes getColumnAttributes( final int column ) {
    return backend.getColumnAttributes( column );
  }

  public DataAttributes getTableAttributes() {
    return backend.getTableAttributes();
  }

  public int getRowCount() {
    return backend.getRowCount();
  }

  public int getColumnCount() {
    return backend.getColumnCount();
  }

  public Class<?> getColumnClass( final int columnIndex ) {
    return backend.getColumnClass( columnIndex );
  }

  public String getColumnName( final int columnIndex ) {
    return backend.getColumnName( columnIndex );
  }

  public void addTableModelListener( final TableModelListener l ) {
    backend.addTableModelListener( l );
  }

  public void removeTableModelListener( final TableModelListener l ) {
    backend.removeTableModelListener( l );
  }

  public DataAttributes getCellDataAttributes( final int row, final int column ) {
    return backend.getCellDataAttributes( mapRow( row ), column );
  }

  public boolean isCellEditable( final int rowIndex, final int columnIndex ) {
    return backend.isCellEditable( mapRow( rowIndex ), columnIndex );
  }

  public Object getValueAt( final int rowIndex, final int columnIndex ) {
    return backend.getValueAt( mapRow( rowIndex ), columnIndex );
  }

  public void setValueAt( final Object aValue, final int rowIndex, final int columnIndex ) {
    backend.setValueAt( aValue, mapRow( rowIndex ), columnIndex );
  }
}
