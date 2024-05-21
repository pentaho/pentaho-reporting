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
 *  Copyright (c) 2006 - 2024 Hitachi Vantara..  All rights reserved.
 */
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
