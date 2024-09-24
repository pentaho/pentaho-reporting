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

package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

public class StaticConnectionProviderTest {

  @Test( expected = NullPointerException.class )
  public void testCreateWitoutConnection() {
    new StaticConnectionProvider( null );
  }

  @Test
  public void testCreateConnection() throws SQLException {
    Connection connection = mock( Connection.class );
    StaticConnectionProvider provider = new StaticConnectionProvider( connection );
    Connection result = provider.createConnection( "user", "password" );
    assertThat( result, is( equalTo( connection ) ) );
  }

  @SuppressWarnings( "unchecked" )
  @Test
  public void testGetConnectionHash() {
    Connection connection = mock( Connection.class );
    StaticConnectionProvider provider = new StaticConnectionProvider( connection );
    Object result = provider.getConnectionHash();
    assertThat( result, is( instanceOf( List.class ) ) );
    List<Object> list = (List<Object>) result;
    assertThat( list.size(), is( equalTo( 3 ) ) );
    assertThat(
        (String) list.get( 0 ),
        is( equalTo( "org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.StaticConnectionProvider" ) ) );
    assertThat( (Integer) list.get( 1 ), is( equalTo( connection.hashCode() ) ) );
    assertThat( (String) list.get( 2 ), is( equalTo( connection.toString() ) ) );
  }
}
