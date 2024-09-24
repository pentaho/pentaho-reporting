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

package org.pentaho.reporting.designer.core.editor.crosstab;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.engine.classic.core.elementfactory.CrosstabDetail;
import org.pentaho.reporting.engine.classic.core.metadata.propertyeditors.AggregationTypePropertyEditor;
import org.pentaho.reporting.libraries.designtime.swing.bulk.BulkDataProvider;
import org.pentaho.reporting.libraries.designtime.swing.table.PropertyTableModel;

import javax.swing.table.AbstractTableModel;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Arrays;

public class CrosstabDetailTableModel extends AbstractTableModel implements BulkDataProvider, PropertyTableModel {
  private ArrayList<CrosstabDetail> data;

  public CrosstabDetailTableModel() {
    data = new ArrayList<CrosstabDetail>();
  }

  public void setData( final CrosstabDetail[] data ) {
    if ( data == null ) {
      throw new NullPointerException();
    }
    this.data.clear();
    this.data.addAll( Arrays.asList( data ) );

    fireTableDataChanged();
  }

  public int getRowCount() {
    return data.size();
  }

  public int getColumnCount() {
    return 2;
  }

  public Object getValueAt( final int rowIndex, final int columnIndex ) {
    final CrosstabDetail crosstabDimension = data.get( rowIndex );
    switch( columnIndex ) {
      case 0:
        return crosstabDimension.getField();
      case 1:
        return crosstabDimension.getAggregation();
      default:
        throw new IllegalStateException();
    }
  }

  public void setValueAt( final Object value, final int rowIndex, final int columnIndex ) {
    final CrosstabDetail crosstabDetail = get( rowIndex );
    switch( columnIndex ) {
      case 1:
        crosstabDetail.setAggregation( (Class) value );
        break;
      default:
        throw new IllegalStateException();
    }
    fireTableCellUpdated( rowIndex, columnIndex );
  }

  public boolean isCellEditable( final int rowIndex, final int columnIndex ) {
    return columnIndex != 0;
  }

  public Class getColumnClass( final int columnIndex ) {
    switch( columnIndex ) {
      case 0:
        return String.class;
      case 1:
        return Class.class;
      default:
        throw new IllegalStateException();
    }
  }

  public String getColumnName( final int column ) {
    switch( column ) {
      case 0:
        return Messages.getString( "CrosstabDetailTableModel.Field" );
      case 1:
        return Messages.getString( "CrosstabDetailTableModel.Aggregation" );
      default:
        throw new IllegalStateException();
    }
  }

  public void add( final CrosstabDetail o ) {
    data.add( o );
    fireTableDataChanged();
  }

  public void add( final int idx, final CrosstabDetail item ) {
    data.add( idx, item );
    fireTableDataChanged();
  }

  public CrosstabDetail get( final int index ) {
    return data.get( index );
  }

  public void remove( final int index ) {
    data.remove( index );
    fireTableDataChanged();
  }

  public void clear() {
    data.clear();
    fireTableDataChanged();
  }

  public CrosstabDetail[] toArray() {
    return data.toArray( new CrosstabDetail[ data.size() ] );
  }

  public int size() {
    return getRowCount();
  }

  public int getBulkDataSize() {
    return getRowCount();
  }

  public Object[] getBulkData() {
    return toArray();
  }

  public void setBulkData( final Object[] data ) {
    this.data.clear();
    for ( int i = 0; i < data.length; i++ ) {
      final Object o = data[ i ];
      this.data.add( (CrosstabDetail) o );
    }
    fireTableDataChanged();
  }

  public Class getClassForCell( final int row, final int col ) {
    return getColumnClass( col );
  }

  public PropertyEditor getEditorForCell( final int row, final int column ) {
    if ( column == 1 ) {
      return new AggregationTypePropertyEditor();
    }
    return null;
  }
}
