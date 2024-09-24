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
