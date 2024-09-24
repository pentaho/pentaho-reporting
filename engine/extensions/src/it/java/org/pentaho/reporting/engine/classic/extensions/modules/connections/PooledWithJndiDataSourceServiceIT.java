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
 * Copyright (c) 2005-2017 Hitachi Vantara..  All rights reserved.
 */

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
