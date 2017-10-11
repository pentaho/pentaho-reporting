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
