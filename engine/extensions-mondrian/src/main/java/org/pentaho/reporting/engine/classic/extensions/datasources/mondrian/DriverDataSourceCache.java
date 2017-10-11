/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import org.pentaho.reporting.libraries.base.util.LFUMap;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Mondrian uses the Identity-Hashkey for creating its cache-key. So we better return the same instance for the same
 * connection information, or caching will be effectively disabled.
 *
 * @author Thomas Morgner.
 */
public class DriverDataSourceCache {
  private static class DriverConnectionKey implements Serializable {
    private String jdbcConnectString;
    private Properties jdbcProperties;

    private DriverConnectionKey( final String jdbcConnectString, final Properties jdbcProperties ) {
      if ( jdbcConnectString == null ) {
        throw new NullPointerException();
      }
      if ( jdbcProperties == null ) {
        throw new NullPointerException();
      }

      this.jdbcConnectString = jdbcConnectString;
      this.jdbcProperties = (Properties) jdbcProperties.clone();
    }

    public boolean equals( final Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }

      final DriverConnectionKey that = (DriverConnectionKey) o;

      if ( !jdbcConnectString.equals( that.jdbcConnectString ) ) {
        return false;
      }
      if ( !jdbcProperties.equals( that.jdbcProperties ) ) {
        return false;
      }

      return true;
    }

    public int hashCode() {
      int result = jdbcConnectString.hashCode();
      result = 31 * result + jdbcProperties.hashCode();
      return result;
    }
  }

  /**
   * Implementation of {@link DataSource} which calls the good ol' {@link java.sql.DriverManager}.
   */
  private static class DriverManagerDataSource implements DataSource {
    private String jdbcConnectString;
    private PrintWriter logWriter;
    private int loginTimeout;
    private Properties jdbcProperties;

    public DriverManagerDataSource( final String jdbcConnectString,
                                    final Properties properties ) {
      if ( jdbcConnectString == null ) {
        throw new NullPointerException();
      }
      if ( properties == null ) {
        throw new NullPointerException();
      }
      this.jdbcConnectString = jdbcConnectString;
      this.jdbcProperties = (Properties) properties.clone();
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
      throw new SQLFeatureNotSupportedException();
    }

    public boolean equals( final Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }

      final DriverManagerDataSource that = (DriverManagerDataSource) o;

      if ( loginTimeout != that.loginTimeout ) {
        return false;
      }
      if ( !jdbcConnectString.equals( that.jdbcConnectString ) ) {
        return false;
      }
      if ( !jdbcProperties.equals( that.jdbcProperties ) ) {
        return false;
      }
      if ( logWriter != null ? !logWriter.equals( that.logWriter ) : that.logWriter != null ) {
        return false;
      }

      return true;
    }

    public int hashCode() {
      int result = jdbcConnectString.hashCode();
      result = 31 * result + ( logWriter != null ? logWriter.hashCode() : 0 );
      result = 31 * result + loginTimeout;
      result = 31 * result + jdbcProperties.hashCode();
      return result;
    }

    public Connection getConnection() throws SQLException {
      return DriverManager.getConnection( jdbcConnectString, jdbcProperties );
    }

    public Connection getConnection( final String username, final String password )
      throws SQLException {
      final Properties temp = (Properties) jdbcProperties.clone();
      if ( username != null ) {
        temp.put( "user", username );
      }
      if ( password != null ) {
        temp.put( "password", password );
      }
      return java.sql.DriverManager.getConnection( jdbcConnectString, temp );
    }

    public PrintWriter getLogWriter() throws SQLException {
      return logWriter;
    }

    public void setLogWriter( final PrintWriter out ) throws SQLException {
      logWriter = out;
    }

    public void setLoginTimeout( final int seconds ) throws SQLException {
      loginTimeout = seconds;
    }

    public int getLoginTimeout() throws SQLException {
      return loginTimeout;
    }

    public boolean isWrapperFor( Class c ) {
      return false;
    }

    public <T> T unwrap( Class<T> c ) throws SQLException {
      throw new SQLException( "This class cannot be unwrapped." );
    }
  }

  private static LFUMap cache;

  private DriverDataSourceCache() {
  }

  public static synchronized DataSource createDataSource( final String jdbcUrl, final Properties properties ) {
    if ( cache == null ) {
      cache = new LFUMap( 20 );
    }

    final DriverConnectionKey key = new DriverConnectionKey( jdbcUrl, properties );
    final Object o = cache.get( key );
    if ( o instanceof DataSource ) {
      return (DataSource) o;
    }
    final DriverManagerDataSource managerDataSource = new DriverManagerDataSource( jdbcUrl, properties );
    cache.put( key, managerDataSource );
    return managerDataSource;
  }
}
