/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.misc.tablemodel;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;

/**
 * A TableModel that proxies an other tablemodel and cuts rows from the start and/or the end of the other tablemodel.
 *
 * @author Thomas Morgner
 */
public class SubSetTableModel implements TableModel {
  /**
   * A helper class, that translates tableevents received from the wrapped table model and forwards them with changed
   * indices to the registered listeners.
   */
  private final class TableEventTranslator implements TableModelListener {
    /**
     * the registered listeners.
     */
    private final ArrayList listeners;

    /**
     * Default Constructor.
     */
    private TableEventTranslator() {
      listeners = new ArrayList();
    }

    /**
     * This fine grain notification tells listeners the exact range of cells, rows, or columns that changed. The
     * received rows are translated to fit the external tablemodel size.
     *
     * @param e
     *          the event, that should be translated.
     */
    public void tableChanged( final TableModelEvent e ) {
      int firstRow = e.getFirstRow();
      if ( e.getFirstRow() > 0 ) {
        firstRow -= getStart();
      }

      int lastRow = e.getLastRow();
      if ( lastRow > 0 ) {
        lastRow -= getStart();
        lastRow -= ( getEnclosedModel().getRowCount() - getEnd() );
      }
      final int type = e.getType();
      final int column = e.getColumn();

      final TableModelEvent event = new TableModelEvent( SubSetTableModel.this, firstRow, lastRow, column, type );

      for ( int i = 0; i < listeners.size(); i++ ) {
        final TableModelListener l = (TableModelListener) listeners.get( i );
        l.tableChanged( event );
      }
    }

    /**
     * Adds the TableModelListener to this Translator.
     *
     * @param l
     *          the tablemodel listener
     */
    protected void addTableModelListener( final TableModelListener l ) {
      listeners.add( l );
    }

    /**
     * Removes the TableModelListener from this Translator.
     *
     * @param l
     *          the tablemodel listener
     */
    protected void removeTableModelListener( final TableModelListener l ) {
      listeners.remove( l );
    }
  }

  /**
   * the row that should be the first row.
   */
  private int start;

  /**
   * the row that should be the last row.
   */
  private int end;

  /**
   * the model.
   */
  private TableModel model;

  /**
   * the event translator.
   */
  private TableEventTranslator eventHandler;

  /**
   * Creates a new SubSetTableModel, the start and the end parameters define the new tablemodel row count. The parameter
   * <code>start</code> must be a positive integer and denotes the number or rows removed from the start of the
   * tablemodel. <code>end</code> is the number of the last translated row. Any row after <code>end</code> is ignored.
   * End must be greater or equal the given start row.
   *
   * @param start
   *          the number of rows that should be removed.
   * @param end
   *          the last row.
   * @param model
   *          the wrapped model
   * @throws NullPointerException
   *           if the given model is null
   * @throws IllegalArgumentException
   *           if start or end are invalid.
   */
  public SubSetTableModel( final int start, final int end, final TableModel model ) {
    if ( start < 0 ) {
      throw new IllegalArgumentException( "Start < 0" ); //$NON-NLS-1$
    }
    if ( end <= start ) {
      throw new IllegalArgumentException( "end < start" ); //$NON-NLS-1$
    }
    if ( model == null ) {
      throw new NullPointerException();
    }
    if ( end >= model.getRowCount() ) {
      throw new IllegalArgumentException( "End >= Model.RowCount" ); //$NON-NLS-1$
    }

    this.start = start;
    this.end = end;
    this.model = model;
    this.eventHandler = new TableEventTranslator();
  }

  /**
   * Translates the given row to fit for the wrapped tablemodel.
   *
   * @param rowIndex
   *          the original row index.
   * @return the translated row index.
   */
  private int getClientRowIndex( final int rowIndex ) {
    return rowIndex + start;
  }

  /**
   * Returns the number of rows in the model. A <code>JTable</code> uses this method to determine how many rows it
   * should display. This method should be quick, as it is called frequently during rendering.
   *
   * @return the number of rows in the model
   * @see #getColumnCount
   */
  public int getRowCount() {
    final int rowCount = model.getRowCount();
    return rowCount - start - ( rowCount - end );
  }

  /**
   * Returns the number of columns in the model. A <code>JTable</code> uses this method to determine how many columns it
   * should create and display by default.
   *
   * @return the number of columns in the model
   * @see #getRowCount
   */
  public int getColumnCount() {
    return model.getColumnCount();
  }

  /**
   * Returns the name of the column at <code>columnIndex</code>. This is used to initialize the table's column header
   * name. Note: this name does not need to be unique; two columns in a table can have the same name.
   *
   * @param columnIndex
   *          the index of the column
   * @return the name of the column
   */
  public String getColumnName( final int columnIndex ) {
    return model.getColumnName( columnIndex );
  }

  /**
   * Returns the most specific superclass for all the cell values in the column. This is used by the <code>JTable</code>
   * to set up a default renderer and editor for the column.
   *
   * @param columnIndex
   *          the index of the column
   * @return the base ancestor class of the object values in the model.
   */
  public Class getColumnClass( final int columnIndex ) {
    return model.getColumnClass( columnIndex );
  }

  /**
   * Returns true if the cell at <code>rowIndex</code> and <code>columnIndex</code> is editable. Otherwise,
   * <code>setValueAt</code> on the cell will not change the value of that cell.
   *
   * @param rowIndex
   *          the row whose value to be queried
   * @param columnIndex
   *          the column whose value to be queried
   * @return true if the cell is editable
   * @see #setValueAt
   */
  public boolean isCellEditable( final int rowIndex, final int columnIndex ) {
    return model.isCellEditable( getClientRowIndex( rowIndex ), columnIndex );
  }

  /**
   * Returns the value for the cell at <code>columnIndex</code> and <code>rowIndex</code>.
   *
   * @param rowIndex
   *          the row whose value is to be queried
   * @param columnIndex
   *          the column whose value is to be queried
   * @return the value Object at the specified cell
   */
  public Object getValueAt( final int rowIndex, final int columnIndex ) {
    return model.getValueAt( getClientRowIndex( rowIndex ), columnIndex );
  }

  /**
   * Sets the value in the cell at <code>columnIndex</code> and <code>rowIndex</code> to <code>aValue</code>.
   *
   * @param aValue
   *          the new value
   * @param rowIndex
   *          the row whose value is to be changed
   * @param columnIndex
   *          the column whose value is to be changed
   * @see #getValueAt
   * @see #isCellEditable
   */
  public void setValueAt( final Object aValue, final int rowIndex, final int columnIndex ) {
    model.setValueAt( aValue, getClientRowIndex( rowIndex ), columnIndex );
  }

  /**
   * Adds a listener to the list that is notified each time a change to the data model occurs.
   *
   * @param l
   *          the TableModelListener
   */
  public void addTableModelListener( final TableModelListener l ) {
    eventHandler.addTableModelListener( l );
  }

  /**
   * Removes a listener from the list that is notified each time a change to the data model occurs.
   *
   * @param l
   *          the TableModelListener
   */
  public void removeTableModelListener( final TableModelListener l ) {
    eventHandler.removeTableModelListener( l );
  }

  /**
   * Returns the enclosed tablemodel, which is wrapped by this subset table model.
   *
   * @return the enclosed table model, never null.
   */
  protected TableModel getEnclosedModel() {
    return model;
  }

  /**
   * Returns the start row that should be mapped to row 0 of this model.
   *
   * @return the first row that should be visible.
   */
  protected int getStart() {
    return start;
  }

  /**
   * Returns the last row that should be visible.
   *
   * @return the number of the last row.
   */
  protected int getEnd() {
    return end;
  }
}
