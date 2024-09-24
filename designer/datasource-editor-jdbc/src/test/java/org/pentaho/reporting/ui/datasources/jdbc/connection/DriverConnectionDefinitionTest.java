/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */
package org.pentaho.reporting.ui.datasources.jdbc.connection;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class DriverConnectionDefinitionTest {

  private DriverConnectionDefinition connectionDefinition;

  @Before
  public void before() {
    connectionDefinition =
        new DriverConnectionDefinition( "TEST_NAME", "TEST_DRIVER_CLASS", "TEST_CONNECTION_STRING", "TEST_USER",
            "TEST_PASSWORD" );
  }

  @Test( expected = NullPointerException.class )
  public void exceptionOnNullDriverClass() {
    new DriverConnectionDefinition( "TEST_NAME", null, "TEST_CONNECTION_STRING", "TEST_USER", "TEST_PASSWORD" );
  }

  @Test( expected = NullPointerException.class )
  public void exceptionOnNullConnectionString() {
    new DriverConnectionDefinition( "TEST_NAME", "TEST_DRIVER_CLASS", null, "TEST_USER", "TEST_PASSWORD" );
  }

  @Test
  public void testSetGetPort() {
    connectionDefinition.setPort( "3608" );
    assertEquals( "3608", connectionDefinition.getPort() );
  }

  @Test
  public void testSetGetDatabaseName() {
    connectionDefinition.setDatabaseName( "TEST_DATABASE_NAME" );
    assertEquals( "TEST_DATABASE_NAME", connectionDefinition.getDatabaseName() );
  }

  @Test
  public void testSetGetHostName() {
    connectionDefinition.setHostName( "TEST_HOST_NAME" );
    assertEquals( "TEST_HOST_NAME", connectionDefinition.getHostName() );
  }

  @Test
  public void testSetGetDatabaseType() {
    connectionDefinition.setDatabaseType( "TEST_DATABASE_TYPE" );
    assertEquals( "TEST_DATABASE_TYPE", connectionDefinition.getDatabaseType() );
  }

}
