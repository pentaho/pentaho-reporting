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

