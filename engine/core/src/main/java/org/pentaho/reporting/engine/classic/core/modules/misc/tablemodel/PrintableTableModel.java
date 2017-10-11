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

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * A tablemodel that allows to override the column names. This is usefull in internationalized environments, where the
 * tablemodel returns diffent columnnames depending on the current locale.
 *
 * @author LordOfCode
 */
public class PrintableTableModel implements TableModel {

  /**
   * The original TableModel.
   */
  private TableModel model;
  /**
   * The column keys to retrieve the internationalized names from the ResourceBundle.
   */
  private String[] i18nKeys;

  public PrintableTableModel( final TableModel source, final String[] keys ) {
    model = source;
    i18nKeys = (String[]) keys.clone();
  }

  public int getColumnCount() {
    return model.getColumnCount();
  }

  public int getRowCount() {
    return model.getRowCount();
  }

  public boolean isCellEditable( final int rowIndex, final int columnIndex ) {
    return model.isCellEditable( rowIndex, columnIndex );
  }

  public Class getColumnClass( final int columnIndex ) {
    return model.getColumnClass( columnIndex );
  }

  public Object getValueAt( final int rowIndex, final int columnIndex ) {
    return model.getValueAt( rowIndex, columnIndex );
  }

  public void setValueAt( final Object aValue, final int rowIndex, final int columnIndex ) {
    model.setValueAt( aValue, rowIndex, columnIndex );
  }

  /**
   * Retrieves the internationalized column name from the string array.
   *
   * @see TableModel#getColumnName(int)
   */
  public String getColumnName( final int columnIndex ) {
    if ( columnIndex < i18nKeys.length ) {
      final String columnName = i18nKeys[columnIndex];
      if ( columnName != null ) {
        return columnName;
      }
    }
    return model.getColumnName( columnIndex );
  }

  public void addTableModelListener( final TableModelListener l ) {
    model.addTableModelListener( l );
  }

  public void removeTableModelListener( final TableModelListener l ) {
    model.removeTableModelListener( l );
  }
}
