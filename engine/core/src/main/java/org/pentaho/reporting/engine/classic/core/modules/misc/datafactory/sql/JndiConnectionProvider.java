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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.misc.connections.DataSourceService;
import org.pentaho.reporting.engine.classic.core.modules.misc.connections.DatasourceServiceException;
import org.pentaho.reporting.libraries.base.util.StringUtils;

public class JndiConnectionProvider implements ConnectionProvider {
  private transient DataSourceService dataSourceService;

  private static final Log logger = LogFactory.getLog( JndiConnectionProvider.class );
  private String connectionPath;
  private String username;
  private String password;

  public JndiConnectionProvider() {
    dataSourceService = ClassicEngineBoot.getInstance().getObjectFactory().get( DataSourceService.class );
  }

  public JndiConnectionProvider( final String connectionPath, final String username, final String password ) {
    this();
    this.connectionPath = connectionPath;
    this.username = username;
    this.password = password;
  }

  public String getConnectionPath() {
    return connectionPath;
  }

  public void setConnectionPath( final String connectionPath ) {
    this.connectionPath = connectionPath;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername( final String username ) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword( final String password ) {
    this.password = password;
  }

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
  public Connection createConnection( final String user, final String password ) throws SQLException {
    if ( connectionPath == null ) {
      throw new SQLException( "JNDI DataSource is invalid; no connection path is defined." );
    }
    try {
      final DataSource ds = dataSourceService.getDataSource( connectionPath );

      final String realUser;
      final String realPassword;
      if ( StringUtils.isEmpty( username ) == false ) {
        realUser = username;
      } else {
        realUser = user;
      }
      if ( StringUtils.isEmpty( this.password ) == false ) {
        realPassword = this.password;
      } else {
        realPassword = password;
      }

      if ( StringUtils.isEmpty( realUser ) ) {
        final Connection connection = ds.getConnection();
        if ( connection == null ) {
          throw new SQLException( "JNDI DataSource is invalid; it returned null without throwing a meaningful error." );
        }
        return connection;
      }

      final Connection connection = ds.getConnection( realUser, realPassword );
      if ( connection == null ) {
        throw new SQLException( "JNDI DataSource is invalid; it returned null without throwing a meaningful error." );
      }
      return connection;
    } catch ( DatasourceServiceException ne ) {
      logger.warn( "Failed to access the JDNI-System", ne );
      throw new SQLException( "Failed to access the JNDI system", ne );
    }
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final JndiConnectionProvider that = (JndiConnectionProvider) o;

    if ( connectionPath != null ? !connectionPath.equals( that.connectionPath ) : that.connectionPath != null ) {
      return false;
    }
    if ( password != null ? !password.equals( that.password ) : that.password != null ) {
      return false;
    }
    if ( username != null ? !username.equals( that.username ) : that.username != null ) {
      return false;
    }

    return true;
  }

  public Object getConnectionHash() {
    final ArrayList<Object> list = new ArrayList<Object>();
    list.add( getClass().getName() );
    list.add( connectionPath );
    list.add( username );
    return list;
  }

  public int hashCode() {
    int result;
    result = ( connectionPath != null ? connectionPath.hashCode() : 0 );
    result = 31 * result + ( username != null ? username.hashCode() : 0 );
    result = 31 * result + ( password != null ? password.hashCode() : 0 );
    return result;
  }

  private void readObject( final ObjectInputStream stream ) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    dataSourceService = ClassicEngineBoot.getInstance().getObjectFactory().get( DataSourceService.class );
  }

}
