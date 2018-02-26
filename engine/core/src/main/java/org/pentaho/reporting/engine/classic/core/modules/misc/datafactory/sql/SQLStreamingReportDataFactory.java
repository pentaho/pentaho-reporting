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
 * Copyright (c) 2018 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.pentaho.di.trans.dataservice.jdbc.api.IThinStatement;

public class SQLStreamingReportDataFactory extends SQLReportDataFactory {

  private final int windowRowSize;
  private final long windowMillisSize;
  private final long windowRate;

  public SQLStreamingReportDataFactory( final ConnectionProvider connectionProvider, int windowRowSize, long windowMillisSize, long windowRate ) {
    super( connectionProvider );
    this.windowRowSize = windowRowSize;
    this.windowMillisSize = windowMillisSize;
    this.windowRate = windowRate;
  }

  @Override
  public ResultSet performQuery( Statement statement, final String translatedQuery, final String[] preparedParameterNames )
    throws SQLException {
    final ResultSet res;
    if ( preparedParameterNames.length == 0 ) {
      res = ( (IThinStatement) statement ).executeQuery( translatedQuery, windowRowSize, windowMillisSize, windowRate );
    } else {
      final PreparedStatement pstmt = (PreparedStatement) statement;
      res = pstmt.executeQuery();
    }
    return res;
  }
}
