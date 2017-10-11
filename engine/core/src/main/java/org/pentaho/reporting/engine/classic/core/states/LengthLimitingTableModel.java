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

package org.pentaho.reporting.engine.classic.core.states;

import org.pentaho.reporting.engine.classic.core.MetaTableModel;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.EmptyDataAttributes;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class LengthLimitingTableModel implements MetaTableModel {
  private TableModel backend;
  private int queryLimit;
  private MetaTableModel metaBackend;

  public LengthLimitingTableModel( final TableModel backend, final int queryLimit ) {
    if ( backend == null ) {
      throw new NullPointerException();
    }
    if ( queryLimit <= 0 ) {
      throw new IllegalArgumentException();
    }
    this.backend = backend;
    this.queryLimit = Math.min( queryLimit, backend.getRowCount() );
    if ( backend instanceof MetaTableModel ) {
      metaBackend = (MetaTableModel) backend;
    }
  }

  public int getRowCount() {
    return queryLimit;
  }

  public int getColumnCount() {
    return backend.getColumnCount();
  }

  public String getColumnName( final int columnIndex ) {
    return backend.getColumnName( columnIndex );
  }

  public Class getColumnClass( final int columnIndex ) {
    return backend.getColumnClass( columnIndex );
  }

  public boolean isCellEditable( final int rowIndex, final int columnIndex ) {
    return backend.isCellEditable( rowIndex, columnIndex );
  }

  public Object getValueAt( final int rowIndex, final int columnIndex ) {
    return backend.getValueAt( rowIndex, columnIndex );
  }

  public void setValueAt( final Object aValue, final int rowIndex, final int columnIndex ) {
    backend.setValueAt( aValue, rowIndex, columnIndex );
  }

  public void addTableModelListener( final TableModelListener l ) {
    backend.addTableModelListener( l );
  }

  public void removeTableModelListener( final TableModelListener l ) {
    backend.removeTableModelListener( l );
  }

  /**
   * Returns the meta-attribute as Java-Object. The object type that is expected by the report engine is defined in the
   * TableMetaData property set. It is the responsibility of the implementor to map the native meta-data model into a
   * model suitable for reporting.
   * <p/>
   * Meta-data models that only describe meta-data for columns can ignore the row-parameter.
   *
   * @param row
   *          the row of the cell for which the meta-data is queried.
   * @param column
   *          the index of the column for which the meta-data is queried.
   * @return the meta-data object.
   */
  public DataAttributes getCellDataAttributes( final int row, final int column ) {
    if ( metaBackend != null ) {
      return metaBackend.getCellDataAttributes( row, column );
    }
    return EmptyDataAttributes.INSTANCE;
  }

  public boolean isCellDataAttributesSupported() {
    if ( metaBackend != null ) {
      return metaBackend.isCellDataAttributesSupported();
    }
    return false;
  }

  public DataAttributes getColumnAttributes( final int column ) {
    if ( metaBackend != null ) {
      return metaBackend.getColumnAttributes( column );
    }
    return EmptyDataAttributes.INSTANCE;
  }

  /**
   * Returns table-wide attributes. This usually contain hints about the data-source used to query the data as well as
   * hints on the sort-order of the data.
   *
   * @return
   */
  public DataAttributes getTableAttributes() {
    if ( metaBackend != null ) {
      return metaBackend.getTableAttributes();
    }
    return EmptyDataAttributes.INSTANCE;
  }
}
