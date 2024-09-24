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

package org.pentaho.reporting.engine.classic.extensions.datasources.pmd;

import org.pentaho.metadata.query.model.Selection;
import org.pentaho.reporting.engine.classic.core.MetaTableModel;
import org.pentaho.reporting.engine.classic.core.util.CloseableTableModel;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;

import javax.swing.event.TableModelListener;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class PmdMetaTableModel implements MetaTableModel, CloseableTableModel {
  private MetaTableModel parentTableModel;
  private Selection[] selections;
  private String[] columnNames;

  public PmdMetaTableModel( final MetaTableModel parentTableModel,
                            final List<Selection> selections ) {
    if ( parentTableModel == null ) {
      throw new NullPointerException();
    }
    if ( selections == null ) {
      throw new NullPointerException();
    }
    this.parentTableModel = parentTableModel;
    this.selections = new Selection[ selections.size() ];
    this.columnNames = new String[ selections.size() ];
    final Set<String> uniqueIds = new TreeSet<String>();
    for ( int i = 0; i < this.selections.length; i++ ) {
      this.selections[ i ] = selections.get( i );
      String columnName = this.selections[ i ].getLogicalColumn().getId();
      if ( uniqueIds.contains( columnName ) ) {
        // append ID with selection aggregation type
        columnName += ( ( this.selections[ i ].getAggregationType() != null ) ?
          ":" + this.selections[ i ].getAggregationType().toString() : "" );
      }
      uniqueIds.add( columnName );
      this.columnNames[ i ] = columnName;
    }
  }

  public DataAttributes getCellDataAttributes( final int row, final int column ) {
    return new PentahoMetaDataAttributes( parentTableModel.getCellDataAttributes( row, column ),
      selections[ column ].getLogicalColumn(), getColumnName( column ) );
  }

  public boolean isCellDataAttributesSupported() {
    return parentTableModel.isCellDataAttributesSupported();
  }

  public DataAttributes getColumnAttributes( final int column ) {
    return new PentahoMetaDataAttributes( parentTableModel.getColumnAttributes( column ),
      selections[ column ].getLogicalColumn(), getColumnName( column )  );
  }

  public DataAttributes getTableAttributes() {
    return parentTableModel.getTableAttributes();
  }

  public int getRowCount() {
    return parentTableModel.getRowCount();
  }

  public int getColumnCount() {
    return parentTableModel.getColumnCount();
  }

  public String getColumnName( final int columnIndex ) {
    return columnNames[ columnIndex ];
  }

  public Class getColumnClass( final int columnIndex ) {
    return parentTableModel.getColumnClass( columnIndex );
  }

  public boolean isCellEditable( final int rowIndex, final int columnIndex ) {
    return false;
  }

  public Object getValueAt( final int rowIndex, final int columnIndex ) {
    return parentTableModel.getValueAt( rowIndex, columnIndex );
  }

  public void setValueAt( final Object aValue, final int rowIndex, final int columnIndex ) {
  }

  public void addTableModelListener( final TableModelListener l ) {
    parentTableModel.addTableModelListener( l );
  }

  public void removeTableModelListener( final TableModelListener l ) {
    parentTableModel.removeTableModelListener( l );
  }

  /**
   * If this model has disposeable resources assigned, close them or dispose them.
   */
  public void close() {
    if ( parentTableModel instanceof CloseableTableModel ) {
      final CloseableTableModel closeableTableModel = (CloseableTableModel) parentTableModel;
      closeableTableModel.close();
    }
  }
}
