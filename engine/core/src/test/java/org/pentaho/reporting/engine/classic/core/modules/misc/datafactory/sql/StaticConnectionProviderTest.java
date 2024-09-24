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
