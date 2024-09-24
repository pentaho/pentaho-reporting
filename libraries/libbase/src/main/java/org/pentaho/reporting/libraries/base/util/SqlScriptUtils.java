/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.libraries.base.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * User: Dzmitry Stsiapanau Date: 8/21/14 Time: 3:38 PM
 */
public class SqlScriptUtils {
  /**
   * Execute a series of SQL statements, separated by ;
   * <p/>
   * We are already connected...
   * <p/>
   * Multiple statements have to be split into parts We use the ";" to separate statements...
   * <p/>
   * We keep the results in Result object from Jobs
   *
   * @param script     The SQL script to be execute
   * @param connection connection to db
   * @return A result with counts of the number or records updates, inserted, deleted or read.
   */
  public static boolean execStatements( final String script,
                                        final Connection connection,
                                        final boolean stopOnError ) {
    Boolean result = true;

    // Deleting all the single-line and multi-line comments from the string
    String all = SqlCommentScrubber.removeComments( script ); // scrubDoubleHyphenComments(script);

    String[] statements = all.split( ";" );
    String stat;
    Boolean singleResult = true;
    for ( int i = 0; i < statements.length; i++ ) {
      stat = statements[ i ];
      if ( !StringUtils.onlySpaces( stat ) ) {
        String sql = StringUtils.trim( stat );
        // any kind of statement
        Statement stmt = null;
        try {
          stmt = connection.createStatement();
          singleResult = stmt.execute( sql );
        } catch ( SQLException e ) {
          singleResult = false;
        } finally {
          try {
            stmt.close();
          } catch ( SQLException e ) {
            singleResult = false;
          }
        }

        if ( !singleResult ) {
          result = singleResult;
          if ( stopOnError ) {
            return result;
          }
        }
      }
    }
    return result;
  }
}

