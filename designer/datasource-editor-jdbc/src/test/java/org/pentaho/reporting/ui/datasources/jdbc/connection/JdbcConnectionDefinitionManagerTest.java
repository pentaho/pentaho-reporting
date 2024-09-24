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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.prefs.Preferences;

import org.junit.Before;
import org.junit.Test;

public class JdbcConnectionDefinitionManagerTest {

  private JdbcConnectionDefinitionManager manager;

  @Before
  public void before() {
    Preferences preferences = mock( Preferences.class );
    Preferences nodePreferences = mock( Preferences.class );
    when( nodePreferences.node( "properties" ) ).thenReturn( mock( Preferences.class ) );
    when( preferences.node( anyString() ) ).thenReturn( nodePreferences );
    manager = new JdbcConnectionDefinitionManager( preferences, "TEST" );
  }

  @Test
  public void updateSourceList_with_DriverConnectionDefinition() {
    JdbcConnectionDefinition connectionDefinition =
        new DriverConnectionDefinition( "NAME", "DRIVER_CLASS", "CONNECTION_STRING", "USERNAME", "PASSWORD" );
    manager.updateSourceList( connectionDefinition );
    JdbcConnectionDefinition[] actual = manager.getSources();
    assertEquals( 1, actual.length );
    assertArrayEquals( new JdbcConnectionDefinition[] { connectionDefinition }, actual );
  }

  @Test
  public void updateSourceList_with_JndiConnectionDefinition() {
    JdbcConnectionDefinition connectionDefinition =
        new JndiConnectionDefinition( "NAME", "JNDI_NAME", "DATABASE_TYPE", "USERNAME", "PASSWORD" );
    manager.updateSourceList( connectionDefinition );
    JdbcConnectionDefinition[] actual = manager.getSources();
    assertEquals( 1, actual.length );
    assertArrayEquals( new JdbcConnectionDefinition[] { connectionDefinition }, actual );
  }

  @Test
  public void removeSource() {
    JdbcConnectionDefinition connectionDefinition =
        new JndiConnectionDefinition( "NAME", "JNDI_NAME", "DATABASE_TYPE", "USERNAME", "PASSWORD" );
    manager.updateSourceList( connectionDefinition );
    manager.removeSource( "NAME" );
    JdbcConnectionDefinition[] actual = manager.getSources();
    assertEquals( 0, actual.length );
  }

}
