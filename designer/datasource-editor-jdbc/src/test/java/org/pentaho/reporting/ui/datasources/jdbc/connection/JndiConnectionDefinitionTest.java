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

public class JndiConnectionDefinitionTest {

  private JndiConnectionDefinition connectionDefinition;

  @Before
  public void before() {
    connectionDefinition =
        new JndiConnectionDefinition( "TEST_NAME", "TEST_JNDI_NAME", "TEST_DATABASE_TYPE", "TEST_USER", "TEST_PASSWORD" );
  }

  @Test( expected = IllegalArgumentException.class )
  public void exceptionOnEmptyJndiName() {
    new JndiConnectionDefinition( "TEST_NAME", null, "TEST_DATABASE_TYPE", "TEST_USER", "TEST_PASSWORD" );
  }

  @Test
  public void getUsername() {
    assertEquals( "TEST_USER", connectionDefinition.getUsername() );
  }

  @Test
  public void getPassword() {
    assertEquals( "TEST_PASSWORD", connectionDefinition.getPassword() );
  }

  @Test
  public void getDatabaseType() {
    assertEquals( "TEST_DATABASE_TYPE", connectionDefinition.getDatabaseType() );
  }

}
