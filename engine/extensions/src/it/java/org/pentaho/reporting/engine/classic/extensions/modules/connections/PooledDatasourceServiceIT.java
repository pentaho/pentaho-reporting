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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

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
