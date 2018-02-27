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
 * Copyright (c) 2018 Hitachi Vantara, Simba Management Limited and Contributors...  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.trans.dataservice.jdbc.api.IThinStatement;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SQLStreamingReportDataFactoryTest {
  private SQLStreamingReportDataFactory dataFactory;
  private ConnectionProvider connectionProvider;
  private Statement statement;
  private final int windowRowSize = 100;
  private final long windowMillisSize = 0;
  private final long windowRate = 1000;
  private String query = "SELECT * FROM \"sales4\"";
  private String[] preparedParameterNames = new String[0];

  @Before
  public void setUp() {
    connectionProvider = mock( ConnectionProvider.class );
    dataFactory = new SQLStreamingReportDataFactory( connectionProvider, windowRowSize, windowMillisSize, windowRate );
    statement = mock( IThinStatement.class );
  }

  @Test
  public void testQueryExecution() throws SQLException {
    dataFactory.performQuery( statement, query, preparedParameterNames );
    verify( (IThinStatement) statement ).executeQuery( query, windowRowSize, windowMillisSize, windowRate );
  }

  @Test
  public void testPreparedStatementIsExecuted() throws SQLException {
    preparedParameterNames = new String[]{"some prepared parameter"};
    statement = mock( PreparedStatement.class );
    dataFactory.performQuery( statement, query, preparedParameterNames );
    verify( (PreparedStatement) statement ).executeQuery();
  }
}
