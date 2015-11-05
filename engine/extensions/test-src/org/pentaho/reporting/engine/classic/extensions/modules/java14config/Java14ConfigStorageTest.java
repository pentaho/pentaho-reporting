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
 * Copyright (c) 2005-2015 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.modules.java14config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Vector;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.pentaho.reporting.engine.classic.core.modules.misc.configstore.base.ConfigStoreException;
import org.pentaho.reporting.libraries.base.config.Configuration;

public class Java14ConfigStorageTest {

  private static final String TEST_PATH = "path";

  private Java14ConfigStorage storage;
  private Preferences base;

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Before
  public void setUp() {
    base = mock( Preferences.class );
    storage = new Java14ConfigStorage( base );
  }

  @Test
  public void testStoreIllegalException() throws Exception {
    expectedEx.expect( IllegalArgumentException.class );
    expectedEx.expectMessage( "The give path is not valid." );

    String configPath = ".";
    Configuration config = mock( Configuration.class );

    storage.store( configPath, config );
  }

  @Test
  public void testStore() throws Exception {
    Preferences pref = mock( Preferences.class );
    checkStoreMethod( pref );
  }

  @Test
  public void testStoreException() throws Exception {
    expectedEx.expect( ConfigStoreException.class );
    expectedEx.expectMessage( "Failed to store config" + TEST_PATH );

    Preferences pref = mock( Preferences.class );
    doThrow( BackingStoreException.class ).when( pref ).sync();

    checkStoreMethod( pref );
  }

  private void checkStoreMethod( Preferences pref ) throws Exception {
    Configuration config = mock( Configuration.class );
    Vector<String> keys = new Vector<String>();
    keys.add( "key_0" );
    keys.add( "key_1" );

    doReturn( pref ).when( base ).node( TEST_PATH );
    doReturn( keys.elements() ).when( config ).getConfigProperties();
    doReturn( "prop_0" ).when( config ).getConfigProperty( "key_0" );
    doReturn( null ).when( config ).getConfigProperty( "key_1" );

    storage.store( TEST_PATH, config );

    verify( pref ).clear();
    verify( pref ).put( "key_0", "prop_0" );
    verify( pref ).sync();
  }

  @Test
  public void testLoadIllegalException() throws Exception {
    expectedEx.expect( IllegalArgumentException.class );
    expectedEx.expectMessage( "The give path is not valid." );

    String configPath = ".";
    Configuration config = mock( Configuration.class );

    storage.load( configPath, config );
  }

  @Test
  public void testLoad() throws Exception {
    Preferences pref = mock( Preferences.class );
    Configuration config = mock( Configuration.class );
    String[] keysArray = new String[] { "key_0", "key_1" };

    doReturn( pref ).when( base ).node( TEST_PATH );
    doReturn( keysArray ).when( pref ).keys();
    doReturn( "val_0" ).when( pref ).get( "key_0", null );
    doReturn( null ).when( pref ).get( "key_1", null );

    Configuration conf = storage.load( TEST_PATH, config );

    assertThat( conf, is( notNullValue() ) );
    assertThat( conf.getConfigProperty( "key_0" ), is( equalTo( "val_0" ) ) );
  }

  @Test
  public void testLoadException() throws Exception {
    Preferences pref = mock( Preferences.class );
    Configuration config = mock( Configuration.class );

    expectedEx.expect( ConfigStoreException.class );
    expectedEx.expectMessage( "Failed to load config" + TEST_PATH );

    doReturn( pref ).when( base ).node( TEST_PATH );
    doThrow( BackingStoreException.class ).when( pref ).keys();

    storage.load( TEST_PATH, config );
  }

  @Test
  public void testIsAvailableIllegalException() throws Exception {
    expectedEx.expect( IllegalArgumentException.class );
    expectedEx.expectMessage( "The give path is not valid." );
    storage.isAvailable( "." );
  }

  @Test
  public void testIsAvailable() throws Exception {
    doReturn( true ).when( base ).nodeExists( TEST_PATH );
    boolean result = storage.isAvailable( TEST_PATH );
    assertThat( result, is( equalTo( true ) ) );

    doThrow( BackingStoreException.class ).when( base ).nodeExists( TEST_PATH );
    result = storage.isAvailable( TEST_PATH );
    assertThat( result, is( equalTo( false ) ) );
  }
}
