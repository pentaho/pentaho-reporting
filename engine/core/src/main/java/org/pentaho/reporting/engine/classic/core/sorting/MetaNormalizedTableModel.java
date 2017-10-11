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
import org.pentaho.reporting.engine.classic.core.wizard.EmptyDataAttributes;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class MetaNormalizedTableModel implements MetaTableModel {
  private MetaTableModel metaTableModel;
  private TableModel tableModel;

  public MetaNormalizedTableModel( final TableModel model ) {
    ArgumentNullException.validate( "model", model );

    tableModel = model;
    if ( model instanceof MetaTableModel ) {
      metaTableModel = (MetaTableModel) model;
    } else {
      metaTableModel = new EmptyMetaTableModel();
    }
  }

  public DataAttributes getCellDataAttributes( final int row, final int column ) {
    return metaTableModel.getCellDataAttributes( row, column );
  }

  public boolean isCellDataAttributesSupported() {
    return metaTableModel.isCellDataAttributesSupported();
  }

  public DataAttributes getColumnAttributes( final int column ) {
    return metaTableModel.getColumnAttributes( column );
  }

  public DataAttributes getTableAttributes() {
    return metaTableModel.getTableAttributes();
  }

  public int getRowCount() {
    return tableModel.getRowCount();
  }

  public int getColumnCount() {
    return tableModel.getColumnCount();
  }

  public String getColumnName( final int columnIndex ) {
    return tableModel.getColumnName( columnIndex );
  }

  public Class<?> getColumnClass( final int columnIndex ) {
    return tableModel.getColumnClass( columnIndex );
  }

  public boolean isCellEditable( final int rowIndex, final int columnIndex ) {
    return false;
  }

  public Object getValueAt( final int rowIndex, final int columnIndex ) {
    return tableModel.getValueAt( rowIndex, columnIndex );
  }

  public void setValueAt( final Object aValue, final int rowIndex, final int columnIndex ) {
    throw new UnsupportedOperationException();
  }

  public void addTableModelListener( final TableModelListener l ) {
    throw new UnsupportedOperationException();
  }

  public void removeTableModelListener( final TableModelListener l ) {
    throw new UnsupportedOperationException();
  }

  private static class EmptyMetaTableModel implements MetaTableModel {
    public DataAttributes getCellDataAttributes( final int row, final int column ) {
      return EmptyDataAttributes.INSTANCE;
    }

    public boolean isCellDataAttributesSupported() {
      return false;
    }

    public DataAttributes getColumnAttributes( final int column ) {
      return EmptyDataAttributes.INSTANCE;
    }

    public DataAttributes getTableAttributes() {
      return EmptyDataAttributes.INSTANCE;
    }

    public int getRowCount() {
      throw new UnsupportedOperationException();
    }

    public int getColumnCount() {
      throw new UnsupportedOperationException();
    }

    public String getColumnName( final int columnIndex ) {
      throw new UnsupportedOperationException();
    }

    public Class<?> getColumnClass( final int columnIndex ) {
      throw new UnsupportedOperationException();
    }

    public boolean isCellEditable( final int rowIndex, final int columnIndex ) {
      throw new UnsupportedOperationException();
    }

    public Object getValueAt( final int rowIndex, final int columnIndex ) {
      throw new UnsupportedOperationException();
    }

    public void setValueAt( final Object aValue, final int rowIndex, final int columnIndex ) {
      throw new UnsupportedOperationException();
    }

    public void addTableModelListener( final TableModelListener l ) {
      throw new UnsupportedOperationException();
    }

    public void removeTableModelListener( final TableModelListener l ) {
      throw new UnsupportedOperationException();
    }
  }
}
