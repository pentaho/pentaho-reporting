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

import mondrian.olap.Connection;
import mondrian.olap.DriverManager;
import mondrian.olap.Util;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

public class DefaultMondrianConnectionProvider implements MondrianConnectionProvider {
  private static final Log logger = LogFactory.getLog( DefaultMondrianConnectionProvider.class );

  public DefaultMondrianConnectionProvider() {
  }

  protected String computeConnectionString( final Properties parameters ) {
    final StringBuffer connectionStr = new StringBuffer( 100 );
    connectionStr.append( "provider=mondrian" );

    connectionStr.append( "; " );
    connectionStr.append( "Catalog=" );
    connectionStr.append( parameters.getProperty( "Catalog" ) );

    final Enumeration objectEnumeration = parameters.keys();
    while ( objectEnumeration.hasMoreElements() ) {
      final String key = (String) objectEnumeration.nextElement();
      if ( "Catalog".equals( key ) ) {
        continue;
      }
      final Object value = parameters.getProperty( key );
      if ( value != null ) {
        connectionStr.append( "; " );
        connectionStr.append( key );
        connectionStr.append( "=" );
        connectionStr.append( value );
      }
    }
    return connectionStr.toString();
  }

  public Connection createConnection( final Properties properties, final DataSource dataSource )
    throws ReportDataFactoryException {
    logger.debug( "Creating Mondrian connection: " + Util.parseConnectString( computeConnectionString( properties ) ) );
    return DriverManager
      .getConnection( Util.parseConnectString( computeConnectionString( properties ) ), null, dataSource );
  }

  public Object getConnectionHash( final Properties properties ) throws ReportDataFactoryException {
    final ArrayList<Object> list = new ArrayList<Object>();
    list.add( getClass().getName() );
    list.add( properties.clone() );
    return list;
  }
}
