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
