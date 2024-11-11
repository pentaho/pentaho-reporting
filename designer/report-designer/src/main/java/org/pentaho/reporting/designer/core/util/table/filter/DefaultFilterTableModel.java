/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.designer.core.util.table.filter;

import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.BitSet;

public class DefaultFilterTableModel implements FilterTableModel {
  private class EventForwardHandler implements TableModelListener {
    private EventForwardHandler() {
    }

    public void tableChanged( final TableModelEvent e ) {
      recomputeRowCount();
      if ( e.getFirstRow() == 0 && e.getLastRow() == Integer.MAX_VALUE ) {
        // a table-data-changed event..
        applyFilter();
        fireTableModelEvent( new TableModelEvent( DefaultFilterTableModel.this,
          e.getFirstRow(), e.getLastRow(), e.getColumn(), e.getType() ) );
        return;
      }

      final TableModelEvent event = new TableModelEvent( DefaultFilterTableModel.this,
        mapFromModel( e.getFirstRow() ), mapFromModel( e.getLastRow() ), e.getColumn(), e.getType() );
      fireTableModelEvent( event );
    }
  }

  private EventListenerList eventListenerList;
  private TableModel backend;
  private int filterColumn;
  private CompoundFilter filters;
  private BitSet filteredSet;
  private int rowCount;

  public DefaultFilterTableModel( final TableModel backend, final int filterColumn ) {
    this.eventListenerList = new EventListenerList();
    this.filterColumn = filterColumn;
    this.filters = new CompoundFilter();
    this.filteredSet = new BitSet( backend.getRowCount() );

    this.backend = backend;
    this.backend.addTableModelListener( new EventForwardHandler() );
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
    rowCount = mapFromModel( backend.getRowCount() );
  }

  public Filter getFilter() {
    return filters;
  }

  public CompoundFilter getFilters() {
    return filters;
  }

  public void setFilters( final CompoundFilter filters ) {
    if ( filters == null ) {
      throw new NullPointerException();
    }
    this.filters = filters;
  }

  public void applyFilter() {
    final int rowCount = backend.getRowCount();
    for ( int r = 0; r < rowCount; r++ ) {
      final Object filterValue = backend.getValueAt( r, getFilterColumn() );
      final Filter.Result match = filters.isMatch( filterValue );
      if ( match == Filter.Result.REJECT ) {
        filteredSet.set( r, true );
      } else {
        filteredSet.set( r, false );
      }
    }
    recomputeRowCount();
  }

  public int getFilterColumn() {
    return filterColumn;
  }

  public Class getColumnClass( final int columnIndex ) {
    return backend.getColumnClass( columnIndex );
  }

  public int getColumnCount() {
    return backend.getColumnCount();
  }

  public String getColumnName( final int columnIndex ) {
    return backend.getColumnName( columnIndex );
  }

  public int getRowCount() {
    return rowCount;
  }


  public boolean isCellEditable( final int rowIndex, final int columnIndex ) {
    final int index = mapToModel( rowIndex );
    return backend.isCellEditable( index, columnIndex );
  }

  public Object getValueAt( final int rowIndex, final int columnIndex ) {
    return backend.getValueAt( mapToModel( rowIndex ), columnIndex );
  }

  public void setValueAt( final Object aValue, final int rowIndex, final int columnIndex ) {
    final int index = mapToModel( rowIndex );
    backend.setValueAt( aValue, index, columnIndex );
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
    final int size = backend.getRowCount();
    int effectiveRow = -1;
    for ( int i = 0; i < size; i++ ) {
      if ( filteredSet.get( i ) == false ) {
        effectiveRow += 1;
        if ( effectiveRow == row ) {
          return i;
        }
      }

    }
    throw new IndexOutOfBoundsException( "Unable to map row to model: " + row );
  }

  /**
   * Maps backend tablemodel row numbers to row numbers from the public view.
   *
   * @param row the row to map
   * @return the corresponding row in the public view.
   */
  public int mapFromModel( final int row ) {
    if ( row < 0 ) {
      return row;
    }

    final int size = backend.getRowCount();
    int retval = 0;
    for ( int i = 0; i < size; i++ ) {
      if ( filteredSet.get( i ) == false ) {
        if ( row == i ) {
          return retval;
        }
        retval += 1;
      } else {
        if ( row == i ) {
          return retval - 1;
        }
      }
    }
    return retval;
  }

}
