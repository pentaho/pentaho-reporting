/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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
