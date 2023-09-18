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
 * Copyright (c) 2002-2023 Hitachi Vantara..  All rights reserved.
 */

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
