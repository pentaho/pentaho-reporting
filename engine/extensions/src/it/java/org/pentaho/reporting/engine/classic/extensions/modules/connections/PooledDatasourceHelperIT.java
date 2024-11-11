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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.sql.Connection;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.PoolingDataSource;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.database.model.DatabaseConnection;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;

public class PooledDatasourceHelperIT {

  @BeforeClass
  public static void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testCreatePool() throws Exception {
    DatabaseConnection con = ConnectionUtil.createConnection();
    con.getAttributes().put( DatabaseConnection.ATTRIBUTE_CUSTOM_DRIVER_CLASS, ConnectionUtil.DRIVER_CLASS );
    con.getAttributes().put( DatabaseConnection.ATTRIBUTE_CUSTOM_URL, ConnectionUtil.CON_URL );

    PoolingDataSource poolingDataSource = PooledDatasourceHelper.setupPooledDataSource( con );

    assertThat( poolingDataSource, is( notNullValue() ) );
    Connection connection = poolingDataSource.getConnection();
    assertThat( connection, is( notNullValue() ) );
    connection.close();

    DataSourceCacheManager cacheManager =
        ClassicEngineBoot.getInstance().getObjectFactory().get( DataSourceCacheManager.class );
    DataSource ds = cacheManager.getDataSourceCache().get( con.getName() );
    assertThat( ds, is( instanceOf( PoolingDataSource.class ) ) );
    assertThat( (PoolingDataSource) ds, is( equalTo( poolingDataSource ) ) );
  }

  @Test
  public void testCreatePoolWithAttrs() throws Exception {
    DatabaseConnection con = ConnectionUtil.createConnectionWithAttrs();

    PoolingDataSource poolingDataSource = PooledDatasourceHelper.setupPooledDataSource( con );

    assertThat( poolingDataSource, is( notNullValue() ) );
    Connection connection = poolingDataSource.getConnection();
    assertThat( connection, is( notNullValue() ) );
    connection.close();

    DataSourceCacheManager cacheManager =
        ClassicEngineBoot.getInstance().getObjectFactory().get( DataSourceCacheManager.class );
    DataSource ds = cacheManager.getDataSourceCache().get( con.getName() );
    assertThat( ds, is( instanceOf( PoolingDataSource.class ) ) );
    assertThat( (PoolingDataSource) ds, is( equalTo( poolingDataSource ) ) );
  }
}
