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
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.SqlScriptUtils;
import org.pentaho.reporting.libraries.base.util.StringUtils;

public class DriverConnectionProvider implements ConnectionProvider {
  private static final Log logger = LogFactory.getLog( DriverConnectionProvider.class );
  private Properties properties;
  private String url;
  private String driver;

  public DriverConnectionProvider() {
    this.properties = new Properties();
  }

  public String getProperty( final String key ) {
    return properties.getProperty( key );
  }

  public Object setProperty( final String key, final String value ) {
    if ( value == null ) {
      return properties.remove( key );
    } else {
      return properties.setProperty( key, value );
    }
  }

  public String getUrl() {
    return url;
  }

  public void setUrl( final String url ) {
    this.url = url;
  }

  public String getDriver() {
    return driver;
  }

  public void setDriver( final String driver ) {
    this.driver = driver;
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
    if ( url == null ) {
      throw new NullPointerException( "URL must not be null when connecting" ); //$NON-NLS-1$
    }

    Driver driverImpl = null;
    try {
      if ( driver != null ) {
        driverImpl = ObjectUtilities.loadAndInstantiate( driver, getClass(), Driver.class );
        if ( driverImpl == null ) {
          logger.warn( "Unable to load specified driver class: " + driver
              + ". See ObjectUtilities logger for error details." );
        }
      }
    } catch ( Throwable e ) {
      throw new SQLException( "Unable to load the driver: " + driver, e.getMessage() ); //$NON-NLS-1$
    }

    final Properties p = new Properties();
    for ( final Map.Entry entry : properties.entrySet() ) {
      final String entryKey = (String) entry.getKey();
      if ( isFilteredKey( entryKey ) ) {
        continue;
      }
      p.setProperty( entryKey, (String) entry.getValue() );
    }
    if ( user != null ) {
      p.setProperty( "user", user );
    }
    if ( password != null ) {
      p.setProperty( "password", password );
    }

    final Connection connection;
    if ( driverImpl != null ) {
      connection = driverImpl.connect( url, p );
    } else {
      connection = DriverManager.getConnection( url, p );
    }
    if ( connection == null ) {
      throw new SQLException( "Driver Manager returned no connection. Your java-implementation is weird." );
    }
    String sqlConnect = p.getProperty( "SQL_CONNECT" );
    if ( !StringUtils.isEmpty( sqlConnect ) ) {
      SqlScriptUtils.execStatements( sqlConnect, connection, false );
    }

    return connection;
  }

  private boolean isFilteredKey( final String key ) {
    if ( key.startsWith( "::" ) ) {
      return true;
    }

    return false;
  }

  public Object getConnectionHash() {
    final ArrayList<Object> list = new ArrayList<Object>();
    list.add( getClass().getName() );
    list.add( properties.clone() );
    list.add( url );
    list.add( driver );
    return list;
  }

  public String[] getPropertyNames() {
    final Set<Object> objects = properties.keySet();
    // noinspection SuspiciousToArrayCall
    return objects.toArray( new String[properties.size()] );
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final DriverConnectionProvider that = (DriverConnectionProvider) o;

    if ( driver != null ? !driver.equals( that.driver ) : that.driver != null ) {
      return false;
    }
    if ( !properties.equals( that.properties ) ) {
      return false;
    }
    if ( url != null ? !url.equals( that.url ) : that.url != null ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result;
    result = ( properties != null ? properties.hashCode() : 0 );
    result = 31 * result + ( url != null ? url.hashCode() : 0 );
    result = 31 * result + ( driver != null ? driver.hashCode() : 0 );
    return result;
  }
}
