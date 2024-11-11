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


package org.pentaho.reporting.engine.classic.core.states.datarow;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class EmptyTableModel implements TableModel {
  public EmptyTableModel() {
  }

  public int getRowCount() {
    return 0;
  }

  public int getColumnCount() {
    return 0;
  }

  public String getColumnName( final int columnIndex ) {
    return null;
  }

  public Class getColumnClass( final int columnIndex ) {
    return null;
  }

  public boolean isCellEditable( final int rowIndex, final int columnIndex ) {
    return false;
  }

  public Object getValueAt( final int rowIndex, final int columnIndex ) {
    return null;
  }

  public void setValueAt( final Object aValue, final int rowIndex, final int columnIndex ) {

  }

  public void addTableModelListener( final TableModelListener l ) {

  }

  public void removeTableModelListener( final TableModelListener l ) {

  }
}
