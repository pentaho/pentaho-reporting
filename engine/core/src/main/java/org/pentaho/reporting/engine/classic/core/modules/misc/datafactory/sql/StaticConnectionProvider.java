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
