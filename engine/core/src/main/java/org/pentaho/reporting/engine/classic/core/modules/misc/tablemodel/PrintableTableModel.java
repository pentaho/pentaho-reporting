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
