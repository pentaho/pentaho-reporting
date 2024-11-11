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
