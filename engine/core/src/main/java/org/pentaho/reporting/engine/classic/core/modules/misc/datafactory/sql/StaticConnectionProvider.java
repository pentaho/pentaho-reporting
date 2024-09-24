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

package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Creation-Date: 07.04.2006, 14:54:57
 *
 * @author Thomas Morgner
 */
public class StaticConnectionProvider implements ConnectionProvider {
  private Connection connection;

  public StaticConnectionProvider( final Connection connection ) {
    if ( connection == null ) {
      throw new NullPointerException();
    }
    this.connection = connection;
  }

  /**
   * Although named getConnection() this method should always return a new connection when being queried or should wrap
   * the connection in a way so that calls to "close()" on that connection do not prevent subsequent calls to this
   * method to fail.
   *
   * @return
   * @throws java.sql.SQLException
   */
  public Connection createConnection( final String user, final String password ) throws SQLException {
    return connection;
  }

  public Object getConnectionHash() {
    final ArrayList<Object> list = new ArrayList<Object>();
    list.add( getClass().getName() );
    list.add( System.identityHashCode( connection ) );
    list.add( String.valueOf( connection ) );
    return list;
  }
}
