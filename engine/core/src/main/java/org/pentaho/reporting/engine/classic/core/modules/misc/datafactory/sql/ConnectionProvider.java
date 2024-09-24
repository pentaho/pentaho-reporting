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

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Creation-Date: Dec 12, 2006, 1:53:44 PM
 *
 * @author Thomas Morgner
 */
public interface ConnectionProvider extends Serializable {
  /**
   * Although named getConnection() this method should always return a new connection when being queried or should wrap
   * the connection in a way so that calls to "close()" on that connection do not prevent subsequent calls to this
   * method to fail.
   *
   * @param user
   *          the user name.
   * @param password
   *          the password.
   * @return the connection.
   * @throws SQLException
   *           if the connection has errors.
   */
  public Connection createConnection( String user, String password ) throws SQLException;

  public Object getConnectionHash();
}
