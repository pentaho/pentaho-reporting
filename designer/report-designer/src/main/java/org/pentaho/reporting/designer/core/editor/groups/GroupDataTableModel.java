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

package org.pentaho.reporting.designer.core.editor.groups;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.libraries.designtime.swing.bulk.BulkDataProvider;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class GroupDataTableModel extends AbstractTableModel implements BulkDataProvider {
  private ArrayList<GroupDataEntry> data;

  public GroupDataTableModel() {
    data = new ArrayList();
  }

  public void add( final GroupDataEntry dataEntry ) {
    if ( data.add( dataEntry ) ) {
      fireTableDataChanged();
    }
  }

  public void remove( final GroupDataEntry dataEntry ) {
    if ( data.remove( dataEntry ) ) {
      fireTableDataChanged();
    }
  }

  public void update( final int index, final GroupDataEntry dataEntry ) {
    if ( dataEntry == null ) {
      throw new NullPointerException();
    }
    data.set( index, dataEntry );
    fireTableRowsUpdated( index, index );
  }

  public GroupDataEntry get( final int index ) {
    return data.get( index );
  }

  public GroupDataEntry[] toArray() {
    return data.toArray( new GroupDataEntry[ data.size() ] );
  }

  public String getColumnName( final int column ) {
    if ( column == 0 ) {
      return Messages.getString( "GroupDataTableModel.Name" );
    }
    return Messages.getString( "GroupDataTableModel.Fields" );
  }

  public Class getColumnClass( final int columnIndex ) {
    if ( columnIndex == 0 ) {
      return String.class;
    }
    return GroupDataEntry.class;
  }

  public boolean isCellEditable( final int rowIndex, final int columnIndex ) {
    return true;
  }

  public void setValueAt( final Object aValue, final int rowIndex, final int columnIndex ) {
    if ( columnIndex == 0 ) {
      final GroupDataEntry dataEntry = get( rowIndex );
      final GroupDataEntry newEntry = new GroupDataEntry
        ( dataEntry.getInstanceID(), (String) aValue, dataEntry.getFields() );
      update( rowIndex, newEntry );
      return;
    }

    update( rowIndex, (GroupDataEntry) aValue );
  }

  public int getRowCount() {
    return data.size();
  }

  public int getColumnCount() {
    return 2;
  }

  public Object getValueAt( final int rowIndex, final int columnIndex ) {
    final GroupDataEntry dataEntry = get( rowIndex );
    if ( columnIndex == 0 ) {
      return dataEntry.getName();
    }

    return dataEntry;
  }

  public void clear() {
    data.clear();
    fireTableDataChanged();
  }

  public int getSize() {
    return data.size();
  }

  public int getBulkDataSize() {
    return data.size();
  }

  public Object[] getBulkData() {
    return data.toArray();
  }

  public void setBulkData( final Object[] data ) {
    this.data.clear();
    for ( int i = 0; i < data.length; i++ ) {
      final GroupDataEntry o = (GroupDataEntry) data[ i ];
      this.data.add( o );
    }
    fireTableDataChanged();
  }
}
