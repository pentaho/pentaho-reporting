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
import org.pentaho.reporting.engine.classic.core.elementfactory.CrosstabDimension;
import org.pentaho.reporting.libraries.designtime.swing.bulk.BulkDataProvider;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Arrays;

public class CrosstabDimensionTableModel extends AbstractTableModel implements BulkDataProvider {
  private ArrayList<CrosstabDimension> data;

  public CrosstabDimensionTableModel() {
    data = new ArrayList<CrosstabDimension>();
  }

  public void setData( final CrosstabDimension[] data ) {
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
    return 4;
  }

  public Object getValueAt( final int rowIndex, final int columnIndex ) {
    final CrosstabDimension crosstabDimension = data.get( rowIndex );
    switch( columnIndex ) {
      case 0:
        return crosstabDimension.getField();
      case 1:
        return crosstabDimension.getTitle();
      case 2:
        return crosstabDimension.isPrintSummary();
      case 3:
        return crosstabDimension.getSummaryTitle();
      default:
        throw new IllegalStateException();
    }
  }

  public void setValueAt( final Object value, final int rowIndex, final int columnIndex ) {
    final CrosstabDimension crosstabDimension = get( rowIndex );
    switch( columnIndex ) {
      case 1:
        crosstabDimension.setTitle( (String) value );
        break;
      case 2:
        if ( value != null ) {
          crosstabDimension.setPrintSummary( (Boolean) value );
        } else {
          crosstabDimension.setPrintSummary( false );
        }
        break;
      case 3:
        crosstabDimension.setSummaryTitle( (String) value );
        break;
      default:
        throw new IllegalStateException();
    }
    data.set( rowIndex, crosstabDimension );
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
        return String.class;
      case 2:
        return Boolean.class;
      case 3:
        return String.class;
      default:
        throw new IllegalStateException();
    }
  }

  public String getColumnName( final int column ) {
    switch( column ) {
      case 0:
        return Messages.getString( "CrosstabDimensionTableModel.Field" );
      case 1:
        return Messages.getString( "CrosstabDimensionTableModel.TitleHeader" );
      case 2:
        return Messages.getString( "CrosstabDimensionTableModel.PrintSummary" );
      case 3:
        return Messages.getString( "CrosstabDimensionTableModel.SummaryHeader" );
      default:
        throw new IllegalStateException();
    }
  }

  public void add( final CrosstabDimension o ) {
    data.add( o );
    fireTableDataChanged();
  }

  public void add( final int idx, final CrosstabDimension item ) {
    data.add( idx, item );
    fireTableDataChanged();
  }

  public CrosstabDimension get( final int index ) {
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

  public CrosstabDimension[] toArray() {
    return data.toArray( new CrosstabDimension[ data.size() ] );
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
      this.data.add( (CrosstabDimension) o );
    }
    fireTableDataChanged();
  }
}
