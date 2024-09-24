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
 * Copyright (c) 2001 - 2015 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

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
