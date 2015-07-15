package org.pentaho.reporting.libraries.designtime.swing.table;

import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public abstract class RowMapperTableModel implements TableModel {
  private class EventForwardHandler implements TableModelListener {
    private EventForwardHandler() {
    }

    public void tableChanged( final TableModelEvent e ) {
      recomputeRowCount();
      if ( e.getFirstRow() == 0 && e.getLastRow() == Integer.MAX_VALUE ) {
        fireTableModelEvent( new TableModelEvent( RowMapperTableModel.this,
          e.getFirstRow(), e.getLastRow(), e.getColumn(), e.getType() ) );
        return;
      }

      final TableModelEvent event = new TableModelEvent( RowMapperTableModel.this,
        mapFromModel( e.getFirstRow() ), mapFromModel( e.getLastRow() ), e.getColumn(), e.getType() );
      fireTableModelEvent( event );
    }
  }

  private EventListenerList eventListenerList;
  private TableModel parent;
  private int rowCount;

  public RowMapperTableModel( final TableModel parent ) {
    if ( parent == null ) {
      throw new NullPointerException();
    }
    this.eventListenerList = new EventListenerList();
    this.parent = parent;
    this.parent.addTableModelListener( new EventForwardHandler() );
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
    return parent.isCellEditable( index, columnIndex );
  }

  public Object getValueAt( final int rowIndex, final int columnIndex ) {
    return parent.getValueAt( mapToModel( rowIndex ), columnIndex );
  }

  public void setValueAt( final Object aValue, final int rowIndex, final int columnIndex ) {
    final int index = mapToModel( rowIndex );
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
      if ( isFiltered( i ) ) {
        continue;
      }

      if ( effectiveRow == row ) {
        return i;
      }

      effectiveRow += 1;
    }
    throw new IndexOutOfBoundsException( "Unable to map row to model: " + row );
  }

  protected abstract boolean isFiltered( int row );

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
      if ( isFiltered( i ) ) {
        continue;
      }

      if ( row == i ) {
        return retval;
      }
      retval += 1;
    }
    return retval;
  }


}
