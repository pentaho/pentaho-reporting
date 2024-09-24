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
 * Copyright (c) 2000 - 2017 Hitachi Vantara, Simba Management Limited and Contributors...  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.emptyArray;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class DriverConnectionProviderTest {

  private static final String PROP_KEY = "test_key";
  private static final String PROP_VALUE = "test_val";

  private DriverConnectionProvider provider;

  @Before
  public void setUp() {
    provider = new DriverConnectionProvider();
  }

  @Test
  public void testSetProperty() {
    assertThat( provider.getProperty( PROP_KEY ), is( nullValue() ) );
    assertThat( provider.getPropertyNames(), is( emptyArray() ) );

    provider.setProperty( PROP_KEY, PROP_VALUE );
    assertThat( provider.getProperty( PROP_KEY ), is( equalTo( PROP_VALUE ) ) );
    assertThat( provider.getPropertyNames(), is( arrayContaining( PROP_KEY ) ) );

    provider.setProperty( PROP_KEY, null );
    assertThat( provider.getProperty( PROP_KEY ), is( nullValue() ) );
    assertThat( provider.getPropertyNames(), is( emptyArray() ) );
  }

  @Test( expected = NullPointerException.class )
  public void testCreateConnectionWithoutUrl() throws SQLException {
    provider.createConnection( "user", "password" );
  }

  @Test
  public void testEquals() {
    assertThat( provider.equals( provider ), is( equalTo( true ) ) );
    assertThat( provider.equals( null ), is( equalTo( false ) ) );
    assertThat( provider.equals( "incorrect" ), is( equalTo( false ) ) );

    DriverConnectionProvider newProvider = new DriverConnectionProvider();
    provider.setProperty( PROP_KEY, PROP_VALUE );
    assertThat( provider.equals( newProvider ), is( equalTo( false ) ) );
    newProvider.setProperty( PROP_KEY, PROP_VALUE );
    assertThat( provider.equals( newProvider ), is( equalTo( true ) ) );

    provider.setDriver( "test_driver" );
    assertThat( provider.equals( newProvider ), is( equalTo( false ) ) );
    newProvider.setDriver( "test_driver_0" );
    assertThat( provider.equals( newProvider ), is( equalTo( false ) ) );
    newProvider.setDriver( "test_driver" );
    assertThat( provider.equals( newProvider ), is( equalTo( true ) ) );
    provider.setDriver( null );
    assertThat( provider.equals( newProvider ), is( equalTo( false ) ) );
    newProvider.setDriver( null );
    assertThat( provider.equals( newProvider ), is( equalTo( true ) ) );

    provider.setUrl( "test_url" );
    assertThat( provider.equals( newProvider ), is( equalTo( false ) ) );
    newProvider.setUrl( "test_url_0" );
    assertThat( provider.equals( newProvider ), is( equalTo( false ) ) );
    newProvider.setUrl( "test_url" );
    assertThat( provider.equals( newProvider ), is( equalTo( true ) ) );
    provider.setUrl( null );
    assertThat( provider.equals( newProvider ), is( equalTo( false ) ) );
    newProvider.setUrl( null );
    assertThat( provider.equals( newProvider ), is( equalTo( true ) ) );
  }

  @SuppressWarnings( "unchecked" )
  @Test
  public void testGetConnectionHash() {
    provider.setDriver( "test_driver" );
    provider.setUrl( "test_url" );
    provider.setProperty( PROP_KEY, PROP_VALUE );

    Object result = provider.getConnectionHash();
    assertThat( result, is( instanceOf( List.class ) ) );
    List<Object> list = (List<Object>) result;
    assertThat( list.size(), is( equalTo( 4 ) ) );
    assertThat( (String) list.get( 0 ), is( equalTo( provider.getClass().getName() ) ) );
    assertThat( list.get( 1 ), is( instanceOf( Properties.class ) ) );
    assertThat( (String) list.get( 2 ), is( equalTo( "test_url" ) ) );
    assertThat( (String) list.get( 3 ), is( equalTo( "test_driver" ) ) );
  }
}
