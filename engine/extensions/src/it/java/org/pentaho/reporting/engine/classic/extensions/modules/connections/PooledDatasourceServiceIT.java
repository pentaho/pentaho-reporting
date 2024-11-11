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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.misc.connections.DatasourceServiceException;

public class PooledDatasourceServiceIT {

  private PooledDatasourceService service;
  private DataSourceCacheManager cacheManager;

  @BeforeClass
  public static void init() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Before
  public void setUp() throws Exception {
    cacheManager = ClassicEngineBoot.getInstance().getObjectFactory().get( DataSourceCacheManager.class );
    service = new PooledDatasourceService();
  }

  @Test( expected = DatasourceServiceException.class )
  public void testRetrieveDatasourceServiceException() throws Exception {
    service.retrieve( "incorrect_conn_name" );
  }

  @Test
  public void testRetrieve() throws Exception {
    DataSource ds = service.retrieve( ConnectionUtil.CONNECTION_NAME );

    assertThat( ds, is( notNullValue() ) );
    assertThat( ds.getConnection(), is( notNullValue() ) );
  }

  @Test
  public void testGetDSBoundName() {
    String name = service.getDSBoundName( "test_name" );
    assertThat( name, is( equalTo( "test_name" ) ) );
  }

  @Test
  public void testGetDataSourceFromCache() throws Exception {
    service.retrieve( ConnectionUtil.CONNECTION_NAME );
    DataSource result = service.getDataSource( ConnectionUtil.CONNECTION_NAME );
    assertThat( result, is( notNullValue() ) );
    assertThat( result.getConnection(), is( notNullValue() ) );
  }

  @Test
  public void testGetDataSource() throws Exception {
    service.clearDataSource( ConnectionUtil.CONNECTION_NAME );
    DataSource ds = service.getDataSource( ConnectionUtil.CONNECTION_NAME );
    assertThat( ds, is( notNullValue() ) );
    assertThat( ds.getConnection(), is( notNullValue() ) );
  }

  @Test
  public void testClearCache() throws Exception {
    service.retrieve( ConnectionUtil.CONNECTION_NAME );
    DataSource ds = cacheManager.getDataSourceCache().get( ConnectionUtil.CONNECTION_NAME );
    assertThat( ds, is( notNullValue() ) );
    service.clearCache();
    ds = cacheManager.getDataSourceCache().get( ConnectionUtil.CONNECTION_NAME );
    assertThat( ds, is( nullValue() ) );
  }

  @Test
  public void testClearDataSource() throws Exception {
    service.retrieve( ConnectionUtil.CONNECTION_NAME );
    DataSource ds = cacheManager.getDataSourceCache().get( ConnectionUtil.CONNECTION_NAME );
    assertThat( ds, is( notNullValue() ) );
    service.clearDataSource( ConnectionUtil.CONNECTION_NAME );
    ds = cacheManager.getDataSourceCache().get( ConnectionUtil.CONNECTION_NAME );
    assertThat( ds, is( nullValue() ) );
  }
}
