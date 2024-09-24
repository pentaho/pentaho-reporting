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

package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner;

public class ReportDesignerParserUtil {
  private ReportDesignerParserUtil() {
  }

  public static String normalizeFormula( String s ) {
    if ( s == null ) {
      return null;
    }
    if ( s.startsWith( "report:" ) ) {
      s = "=" + s.substring( "report:".length() ).trim();
    } else {
      s = s.trim();
    }
    if ( s.endsWith( ";" ) ) {
      s = s.substring( 0, s.length() - 1 );
    }
    return s;
  }

}
