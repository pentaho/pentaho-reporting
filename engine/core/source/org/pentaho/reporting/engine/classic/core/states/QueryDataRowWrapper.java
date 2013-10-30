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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.states;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.util.IntegerCache;

/**
 * A data-row wrapper that adds a new artificial parameter to the original datarow that holds the query-limit.
 *
 * @author Thomas Morgner
 */
public class QueryDataRowWrapper implements DataRow
{
  private DataRow backend;
  private Integer queryTimeout;
  private Integer queryLimit;

  public QueryDataRowWrapper(final DataRow backend,
                             final Integer queryTimeout,
                             final Integer queryLimit)
  {
    if (backend == null)
    {
      throw new NullPointerException();
    }
    this.backend = backend;
    this.queryTimeout = queryTimeout;
    this.queryLimit = queryLimit;
  }

  public QueryDataRowWrapper(final DataRow backend, final int queryLimit, final int queryTimeout)
  {
    if (backend == null)
    {
      throw new NullPointerException();
    }
    this.backend = backend;
    this.queryTimeout = IntegerCache.getInteger(queryTimeout);
    this.queryLimit = IntegerCache.getInteger(queryLimit);
  }

  /**
   * Returns the value of the function, expression or column using its specific name. The given name is translated into
   * a valid column number and the the column is queried. For functions and expressions, the <code>getValue()</code>
   * method is called and for columns from the tablemodel the tablemodel method <code>getValueAt(row, column)</code>
   * gets called.
   *
   * @param col the item index.
   * @return the value.
   */
  public Object get(final String col)
  {
    if (DataFactory.QUERY_LIMIT.equals(col))
    {
      return queryLimit;
    }
    if (DataFactory.QUERY_TIMEOUT.equals(col))
    {
      return queryTimeout;
    }
    return backend.get(col);
  }

  public String[] getColumnNames()
  {
    final String[] cols = backend.getColumnNames();
    final String[] retval = new String[cols.length + 2];
    System.arraycopy(cols, 0, retval, 2, cols.length);
    retval[0] = DataFactory.QUERY_LIMIT;
    retval[1] = DataFactory.QUERY_TIMEOUT;
    return retval;
  }

  /**
   * Checks whether the value contained in the column has changed since the last advance-operation.
   *
   * @param name the name of the column.
   * @return true, if the value has changed, false otherwise.
   */
  public boolean isChanged(final String name)
  {
    if (DataFactory.QUERY_LIMIT.equals(name))
    {
      return false;
    }
    if (DataFactory.QUERY_TIMEOUT.equals(name))
    {
      return false;
    }
    return backend.isChanged(name);
  }
}
