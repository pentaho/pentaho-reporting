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
 * Copyright (c) 2005-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.modules.connections;

import org.pentaho.database.model.DatabaseAccessType;
import org.pentaho.database.model.DatabaseConnection;
import org.pentaho.database.service.IDatabaseDialectService;
import org.pentaho.database.util.DatabaseTypeHelper;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.misc.connections.DataBaseConnectionAttributes;
import org.pentaho.reporting.libraries.base.boot.ObjectFactory;

public class ConnectionUtil {

  public static final String CONNECTION_NAME = "Memory";
  public static final String DRIVER_CLASS = "org.hsqldb.jdbcDriver";
  public static final String CON_URL = "jdbc:hsqldb:mem:SampleData";
  public static final String USER_NAME = "pentaho_user";
  public static final String USER_PASSWORD = "password";

  private ConnectionUtil() {
  }

  public static DatabaseConnection createConnection() {
    final ObjectFactory objectFactory = ClassicEngineBoot.getInstance().getObjectFactory();
    final IDatabaseDialectService dialectService = objectFactory.get( IDatabaseDialectService.class );
    final DatabaseTypeHelper databaseTypeHelper = new DatabaseTypeHelper( dialectService.getDatabaseTypes() );

    final DatabaseConnection con = new DatabaseConnection();
    con.setId( CONNECTION_NAME );
    con.setName( CONNECTION_NAME );
    con.setAccessType( DatabaseAccessType.NATIVE );
    con.setDatabaseType( databaseTypeHelper.getDatabaseTypeByShortName( "GENERIC" ) );
    con.setUsername( USER_NAME );
    con.setPassword( USER_PASSWORD );
    return con;
  }

  public static DatabaseConnection createConnectionWithAttrs() {
    DatabaseConnection con = createConnection();
    con.getAttributes().put( DatabaseConnection.ATTRIBUTE_CUSTOM_DRIVER_CLASS, DRIVER_CLASS );
    con.getAttributes().put( DatabaseConnection.ATTRIBUTE_CUSTOM_URL, CON_URL );
    con.getAttributes().put( DataBaseConnectionAttributes.MAX_ACTIVE_KEY, "10" );
    con.getAttributes().put( DataBaseConnectionAttributes.MAX_WAIT_KEY, "5" );
    con.getAttributes().put( DataBaseConnectionAttributes.MAX_IDLE_KEY, "9" );
    con.getAttributes().put( DataBaseConnectionAttributes.QUERY_KEY, "select" );
    return con;
  }
}
