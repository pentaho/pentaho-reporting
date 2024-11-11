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


package org.pentaho.reporting.engine.classic.core;

/**
 * The datarow is used to access the current row in the <code>TableModel</code>, <code>Expression</code>s and
 * <code>Function</code>s using a generic interface.
 * <p/>
 * The Engine assumes, that the tablemodels given for reporting are immutable and do not change during the report
 * processing.
 *
 * @author Thomas Morgner
 * @see org.pentaho.reporting.engine.classic.core.function.Expression
 * @see org.pentaho.reporting.engine.classic.core.function.Function
 * @see javax.swing.table.TableModel
 */
public interface DataRow {
  /**
   * Returns the value of the function, expression or column using its specific name. The given name is translated into
   * a valid column number and the the column is queried. For functions and expressions, the <code>getValue()</code>
   * method is called and for columns from the tablemodel the tablemodel method <code>getValueAt(row, column)</code>
   * gets called.
   *
   * @param col
   *          the item index.
   * @return the value.
   */
  public Object get( String col );

  /**
   * Returns the known column names, this data-row understands. The column names may change over time but do not change
   * while a event is processed by a function. The array returned is a copy of the internal data-storage and can be
   * safely modified.
   *
   * @return the column names as array.
   */
  public String[] getColumnNames();

  /**
   * Checks whether the value contained in the column has changed since the last advance-operation.
   *
   * @param name
   *          the name of the column.
   * @return true, if the value has changed, false otherwise.
   */
  public boolean isChanged( String name );
}
