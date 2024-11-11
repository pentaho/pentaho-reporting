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


package org.pentaho.reporting.engine.classic.extensions.toc;

import org.pentaho.reporting.engine.classic.core.AbstractDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;

import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class DataPassingDataFactory extends AbstractDataFactory {
  private static class WrapperDataModel implements TableModel {
    private TableModel model;

    private WrapperDataModel() {
      this.model = new DefaultTableModel();
    }

    public TableModel getModel() {
      return model;
    }

    public void setModel( final TableModel model ) {
      this.model = model;
    }

    public void addTableModelListener( final TableModelListener l ) {
      model.addTableModelListener( l );
    }

    public Class<?> getColumnClass( final int columnIndex ) {
      return model.getColumnClass( columnIndex );
    }

    public int getColumnCount() {
      return model.getColumnCount();
    }

    public String getColumnName( final int columnIndex ) {
      return model.getColumnName( columnIndex );
    }

    public int getRowCount() {
      return model.getRowCount();
    }

    public Object getValueAt( final int rowIndex, final int columnIndex ) {
      return model.getValueAt( rowIndex, columnIndex );
    }

    public boolean isCellEditable( final int rowIndex, final int columnIndex ) {
      return model.isCellEditable( rowIndex, columnIndex );
    }

    public void removeTableModelListener( final TableModelListener l ) {
      model.removeTableModelListener( l );
    }

    public void setValueAt( final Object aValue, final int rowIndex, final int columnIndex ) {
      model.setValueAt( aValue, rowIndex, columnIndex );
    }
  }

  private String queryName;
  private WrapperDataModel dataModel;

  public DataPassingDataFactory( final String queryName ) {
    if ( queryName == null ) {
      throw new NullPointerException();
    }
    this.queryName = queryName;
    this.dataModel = new WrapperDataModel();
  }

  public void cancelRunningQuery() {

  }

  public TableModel queryData( final String query, final DataRow parameters ) throws ReportDataFactoryException {
    if ( !queryName.equals( query ) ) {
      throw new ReportDataFactoryException( "No such query" );
    }
    final Object o = parameters.get( query );
    if ( o instanceof TableModel ) {
      dataModel.setModel( (TableModel) o );
    }

    return dataModel;
  }

  public DataFactory derive() {
    return clone();
  }

  public DataFactory clone() {
    return super.clone();
  }

  public void close() {

  }

  public boolean isQueryExecutable( final String query, final DataRow parameters ) {
    return queryName.equals( query );
  }

  public String[] getQueryNames() {
    return new String[] { queryName };
  }
}
