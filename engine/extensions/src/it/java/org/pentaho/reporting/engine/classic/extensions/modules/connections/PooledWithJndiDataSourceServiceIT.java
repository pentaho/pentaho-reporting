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


package org.pentaho.reporting.engine.classic.extensions.modules.connections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import javax.naming.spi.NamingManager;
import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugJndiContextFactoryBuilder;

public class PooledWithJndiDataSourceServiceIT {

  @BeforeClass
  public static void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testQueryFallback() throws Exception {
    PooledWithJndiDataSourceService service = new PooledWithJndiDataSourceService();
    NamingManager.setInitialContextFactoryBuilder( new DebugJndiContextFactoryBuilder() );

    DataSource ds = service.queryFallback( "SampleData" );

    assertThat( ds, is( notNullValue() ) );
    assertThat( ds.getConnection(), is( notNullValue() ) );
  }
}
