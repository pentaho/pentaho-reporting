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
