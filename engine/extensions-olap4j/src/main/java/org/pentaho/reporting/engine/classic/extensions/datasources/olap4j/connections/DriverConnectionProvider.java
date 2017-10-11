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

package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections;

import org.olap4j.OlapConnection;
import org.olap4j.OlapWrapper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

public class DriverConnectionProvider implements OlapConnectionProvider {
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

  public OlapConnection createConnection( final String user, final String password ) throws SQLException {
    if ( url == null ) {
      throw new NullPointerException( "URL must not be null when connecting" ); //$NON-NLS-1$
    }

    try {
      if ( driver != null ) {
        Class.forName( driver );
      }
    } catch ( Throwable e ) {
      throw new SQLException( "Unable to load the driver: " + driver, e.getMessage() ); //$NON-NLS-1$
    }

    final Properties p = new Properties();
    for ( final String entryKey : properties.stringPropertyNames() ) {
      if ( isFilteredKey( entryKey ) ) {
        continue;
      }
      p.setProperty( entryKey, properties.getProperty( entryKey ) );
    }

    if ( user != null ) {
      p.setProperty( "user", user ); // NON-NLS
    }
    if ( password != null ) {
      p.setProperty( "password", password );// NON-NLS
    }
    final Connection connection = DriverManager.getConnection( url, p );
    if ( connection instanceof OlapConnection ) {
      return (OlapConnection) connection;
    }
    if ( connection instanceof OlapWrapper ) {
      final OlapWrapper wrapper = (OlapWrapper) connection;
      final OlapConnection olapConnection = wrapper.unwrap( OlapConnection.class );
      if ( olapConnection == null ) {
        throw new SQLException( "Unable to unwrap the connection: " + driver ); //$NON-NLS-1$
      }
      return olapConnection;
    }
    throw new SQLException( "Unable to unwrap the connection: " + driver ); //$NON-NLS-1$
  }

  private boolean isFilteredKey( final String key ) {
    if ( key.startsWith( "::" ) ) {
      return true;
    }

    return false;
  }

  public String[] getPropertyNames() {
    return properties.stringPropertyNames().toArray( new String[ properties.size() ] );
  }

  public Object getConnectionHash() {
    final ArrayList<Object> list = new ArrayList<Object>();
    list.add( getClass().getName() );
    list.add( properties.clone() );
    list.add( url );
    list.add( driver );
    return list;
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
    if ( properties.equals( that.properties ) ) {
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
