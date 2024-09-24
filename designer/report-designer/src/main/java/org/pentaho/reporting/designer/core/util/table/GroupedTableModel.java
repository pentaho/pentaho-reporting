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

package org.pentaho.reporting.designer.core.util.table;

import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class GroupedTableModel implements TableModel {
  private class EventForwardHandler implements TableModelListener {
    private EventForwardHandler() {
    }

    public void tableChanged( final TableModelEvent e ) {
      recomputeRowCount();
      if ( e.getFirstRow() == 0 && e.getLastRow() == Integer.MAX_VALUE ) {
        fireTableModelEvent( new TableModelEvent( GroupedTableModel.this,
          e.getFirstRow(), e.getLastRow(), e.getColumn(), e.getType() ) );
        return;
      }

      final TableModelEvent event = new TableModelEvent( GroupedTableModel.this,
        mapFromModel( e.getFirstRow() ), mapFromModel( e.getLastRow() ), e.getColumn(), e.getType() );
      fireTableModelEvent( event );
    }
  }

  private EventListenerList eventListenerList;
  private GroupingModel parent;
  private int rowCount;

  public GroupedTableModel( final GroupingModel parent ) {
    if ( parent == null ) {
      throw new NullPointerException();
    }
    this.eventListenerList = new EventListenerList();
    this.parent = parent;
    this.parent.addTableModelListener( new EventForwardHandler() );
    recomputeRowCount();
  }

  protected void fireTableModelEvent( final TableModelEvent event ) {
    final TableModelListener[] listeners = eventListenerList.getListeners( TableModelListener.class );
    for ( int i = 0; i < listeners.length; i++ ) {
      final TableModelListener listener = listeners[ i ];
      listener.tableChanged( event );
    }
  }

  protected void recomputeRowCount() {
    rowCount = mapFromModel( parent.getRowCount() );
  }

  public int getRowCount() {
    return rowCount;
  }

  public int getColumnCount() {
    return parent.getColumnCount();
  }

  public String getColumnName( final int columnIndex ) {
    return parent.getColumnName( columnIndex );
  }

  public Class getColumnClass( final int columnIndex ) {
    return parent.getColumnClass( columnIndex );
  }

  public boolean isCellEditable( final int rowIndex, final int columnIndex ) {
    final int index = mapToModel( rowIndex );
    if ( parent.isHeaderRow( index ) ) {
      return true;
    }

    return parent.isCellEditable( index, columnIndex );
  }

  public Object getValueAt( final int rowIndex, final int columnIndex ) {
    return parent.getValueAt( mapToModel( rowIndex ), columnIndex );
  }

  public void setValueAt( final Object aValue, final int rowIndex, final int columnIndex ) {
    final int index = mapToModel( rowIndex );
    if ( parent.isHeaderRow( index ) ) {
      recomputeRowCount();
      fireTableModelEvent( new TableModelEvent( this ) );
      return;
    }

    parent.setValueAt( aValue, index, columnIndex );
  }

  public void addTableModelListener( final TableModelListener l ) {
    eventListenerList.add( TableModelListener.class, l );
  }

  public void removeTableModelListener( final TableModelListener l ) {
    eventListenerList.remove( TableModelListener.class, l );
  }

  /**
   * Maps public row numbers to row numbers from the parent table model.
   *
   * @param row the row to map
   * @return the corresponding row in the parent table model.
   */
  public int mapToModel( final int row ) {
    final int size = parent.getRowCount();
    int effectiveRow = 0;
    for ( int i = 0; i < size; i++ ) {
      final GroupingHeader groupHeader = parent.getGroupHeader( i );
      if ( groupHeader == null ||
        groupHeader.isCollapsed() == false ||
        parent.isHeaderRow( i ) ) {
        if ( effectiveRow == row ) {
          return i;
        }

        effectiveRow += 1;
      }

    }
    throw new IndexOutOfBoundsException( "Unable to map row to model: " + row );
  }

  /**
   * Maps parent tablemodel row numbers to row numbers from the public view.
   *
   * @param row the row to map
   * @return the corresponding row in the public view.
   */
  public int mapFromModel( final int row ) {
    if ( row < 0 ) {
      return row;
    }

    final int size = parent.getRowCount();
    int retval = 0;
    for ( int i = 0; i < size; i++ ) {
      final GroupingHeader groupHeader = parent.getGroupHeader( i );
      if ( groupHeader == null ||
        groupHeader.isCollapsed() == false ||
        parent.isHeaderRow( i ) ) {
        if ( row == i ) {
          return retval;
        }
        retval += 1;
      } else if ( groupHeader.isCollapsed() ) {
        if ( row == i ) {
          return retval - 1;
        }
      }

    }
    return retval;
  }


}
