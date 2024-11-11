/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.extensions.modules.connections;

import org.hsqldb.jdbc.JDBCDriver;
import org.junit.Test;
import org.pentaho.database.IDatabaseDialect;
import org.pentaho.database.dialect.AbstractDatabaseDialect;

import java.sql.Driver;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 5/11/16.
 */
public class PooledDatasourceHelperTest {
  @Test
  public void testGetDriverLocator() {
    String testurl = "testurl";
    Driver driver = mock( Driver.class );
    AbstractDatabaseDialect dialect = mock( AbstractDatabaseDialect.class );
    when( dialect.getDriver( testurl ) ).thenReturn( driver );
    assertEquals( driver, PooledDatasourceHelper.getDriver( dialect, null, testurl ) );
  }

  @Test
  public void testGetDriverLegacy() {
    IDatabaseDialect dialect = mock( IDatabaseDialect.class );
    assertEquals( JDBCDriver.class, PooledDatasourceHelper.getDriver( dialect, JDBCDriver.class.getCanonicalName(), null ).getClass() );
  }
}
