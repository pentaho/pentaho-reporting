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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import javax.sql.DataSource;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

public class StaticDataSourceCacheManagerTest {

  private static final String DS_NAME = "test_ds_name";

  private StaticDataSourceCacheManager cachemanager = new StaticDataSourceCacheManager();

  @Test
  public void testPut() {
    assertThat( cachemanager.get( DS_NAME ), is( nullValue() ) );
    DataSource ds = mock( DataSource.class );
    cachemanager.put( DS_NAME, ds );
    assertThat( cachemanager.get( DS_NAME ), is( notNullValue() ) );
  }

  @Test
  public void testClear() {
    DataSource ds = mock( DataSource.class );
    cachemanager.put( DS_NAME, ds );
    assertThat( cachemanager.get( DS_NAME ), is( notNullValue() ) );
    cachemanager.clear();
    assertThat( cachemanager.get( DS_NAME ), is( nullValue() ) );
  }

  @Test
  public void testRemove() {
    DataSource ds = mock( DataSource.class );
    cachemanager.put( DS_NAME, ds );
    assertThat( cachemanager.get( DS_NAME ), is( notNullValue() ) );
    cachemanager.remove( DS_NAME );
    assertThat( cachemanager.get( DS_NAME ), is( nullValue() ) );
  }

  @Test
  public void testGet() {
    DataSource ds = mock( DataSource.class );
    cachemanager.put( DS_NAME, ds );
    assertThat( cachemanager.get( DS_NAME ), is( notNullValue() ) );
  }

  @Test
  public void testGetDataSourceCache() {
    DataSourceCache cache = cachemanager.getDataSourceCache();
    assertThat( cache, is( notNullValue() ) );
    assertThat( cache, is( CoreMatchers.instanceOf( StaticDataSourceCacheManager.class ) ) );
    assertThat( (StaticDataSourceCacheManager) cache, is( equalTo( cachemanager ) ) );
  }
}
