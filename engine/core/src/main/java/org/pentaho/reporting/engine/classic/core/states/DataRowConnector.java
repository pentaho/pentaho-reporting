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

import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.filter.DataSource;
import org.pentaho.reporting.engine.classic.core.filter.DataTarget;

/**
 * This is the connection-proxy to the various data sources contained in the elements. During report processing the
 * report states get cloned while the elements remain uncloned. The DataRowConnector connects the DataRowBackend (which
 * contains the data) with the stateless elements.
 *
 * @author Thomas Morgner
 */
public final class DataRowConnector implements DataRow {
  /**
   * The data row backend.
   */
  private DataRow dataRow;

  /**
   * Default constructor.
   */
  public DataRowConnector() {
  }

  /**
   * Returns the assigned data row backend.
   *
   * @return the currently assigned DataRowBackend for this DataRowConnector.
   */
  public DataRow getDataRowBackend() {
    return dataRow;
  }

  public String[] getColumnNames() {
    if ( dataRow == null ) {
      throw new IllegalStateException( "Not connected" );
    }
    return dataRow.getColumnNames();
  }

  /**
   * Sets the data row backend for this DataRowConnector. The backend actually contains the data which will be queried,
   * while this DataRowConnector is simply a proxy forwarding all requests to the backend.
   *
   * @param dataRow
   *          the data row backend
   */
  public void setDataRowBackend( final DataRow dataRow ) {
    this.dataRow = dataRow;
  }

  /**
   * Returns the value of the column, function or expression using its name.
   *
   * @param col
   *          the column, function or expression index.
   * @return The column, function or expression value.
   * @throws java.lang.IllegalStateException
   *           if there is no backend connected
   */
  public Object get( final String col ) {
    if ( dataRow == null ) {
      throw new IllegalStateException( "Not connected" );
    }
    return dataRow.get( col );
  }

  /**
   * Queries the last datasource in the chain of targets and filters.
   * <p/>
   * The last datasource is used to feed data into the data processing chain. The result of this computation is
   * retrieved by the element using the registered datasource to query the queue.
   *
   * @param e
   *          the data target.
   * @return The last DataSource in the chain.
   * @deprecated no longer used.
   */
  public static DataSource getLastDatasource( final DataTarget e ) {
    if ( e == null ) {
      throw new NullPointerException();
    }
    DataSource s = e.getDataSource();
    while ( s instanceof DataTarget ) {
      final DataTarget tgt = (DataTarget) s;
      s = tgt.getDataSource();
    }
    return s;
  }

  /**
   * Returns a string describing the object.
   *
   * @return The string.
   */
  public String toString() {
    if ( dataRow == null ) {
      return "org.pentaho.reporting.engine.classic.core.states.DataRowConnector=Not Connected";
    }
    return "org.pentaho.reporting.engine.classic.core.states.DataRowConnector=Connected:" + dataRow;

  }

  public boolean isChanged( final String name ) {
    return dataRow.isChanged( name );
  }

}
