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

package org.pentaho.reporting.engine.classic.core.states;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.sorting.SortConstraint;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;

import java.util.Collections;
import java.util.List;

/**
 * A data-row wrapper that adds a new artificial parameter to the original datarow that holds the query-limit.
 *
 * @author Thomas Morgner
 */
public class QueryDataRowWrapper implements DataRow {
  private final List<SortConstraint> sortConstraints;
  private final DataRow backend;
  private final Integer queryTimeout;
  private final Integer queryLimit;

  @Deprecated
  public QueryDataRowWrapper( final DataRow backend, final Integer queryTimeout, final Integer queryLimit ) {
    this( backend, queryTimeout, queryLimit, workAroundBrokenJavaGenerics() );
  }

  public QueryDataRowWrapper( final DataRow backend, final Integer queryTimeout, final Integer queryLimit,
      final List<SortConstraint> sortConstraints ) {
    ArgumentNullException.validate( "backend", backend );
    ArgumentNullException.validate( "sortConstraints", sortConstraints );

    this.backend = backend;
    this.queryTimeout = queryTimeout;
    this.queryLimit = queryLimit;
    this.sortConstraints = sortConstraints;
  }

  @Deprecated
  public QueryDataRowWrapper( final DataRow backend, final int queryLimit, final int queryTimeout ) {
    this( backend, queryTimeout, queryLimit, workAroundBrokenJavaGenerics() );
  }

  private static List<SortConstraint> workAroundBrokenJavaGenerics() {
    return Collections.emptyList();
  }

  public QueryDataRowWrapper( final DataRow backend, final int queryTimeout, final int queryLimit,
      final List<SortConstraint> sortConstraints ) {
    this( backend, Integer.valueOf( queryTimeout ), Integer.valueOf( queryLimit ), sortConstraints );
  }

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
  public Object get( final String col ) {
    if ( DataFactory.QUERY_LIMIT.equals( col ) ) {
      return queryLimit;
    }
    if ( DataFactory.QUERY_TIMEOUT.equals( col ) ) {
      return queryTimeout;
    }
    if ( DataFactory.QUERY_SORT.equals( col ) ) {
      return sortConstraints;
    }
    return backend.get( col );
  }

  public String[] getColumnNames() {
    final String[] cols = backend.getColumnNames();
    final String[] retval = new String[cols.length + 3];
    System.arraycopy( cols, 0, retval, 3, cols.length );
    retval[0] = DataFactory.QUERY_LIMIT;
    retval[1] = DataFactory.QUERY_TIMEOUT;
    retval[2] = DataFactory.QUERY_SORT;
    return retval;
  }

  /**
   * Checks whether the value contained in the column has changed since the last advance-operation.
   *
   * @param name
   *          the name of the column.
   * @return true, if the value has changed, false otherwise.
   */
  public boolean isChanged( final String name ) {
    if ( DataFactory.QUERY_LIMIT.equals( name ) ) {
      return false;
    }
    if ( DataFactory.QUERY_TIMEOUT.equals( name ) ) {
      return false;
    }
    if ( DataFactory.QUERY_SORT.equals( name ) ) {
      return false;
    }
    return backend.isChanged( name );
  }
}
