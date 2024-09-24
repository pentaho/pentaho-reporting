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
