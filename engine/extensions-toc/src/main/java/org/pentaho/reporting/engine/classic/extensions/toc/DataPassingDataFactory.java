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
