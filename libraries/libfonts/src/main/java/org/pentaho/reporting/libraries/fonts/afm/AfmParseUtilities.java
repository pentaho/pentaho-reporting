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


package org.pentaho.reporting.libraries.fonts.afm;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Creation-Date: 22.07.2007, 16:38:32
 *
 * @author Thomas Morgner
 */
public class AfmParseUtilities {
  private AfmParseUtilities() {
  }

  public static int parseInt( final String key, final String wholeLine ) throws IOException {
    try {
      return Integer.parseInt( wholeLine.substring( key.length() ).trim() );
    } catch ( Exception e ) {
      throw new IOException( "Failed to parse value for Line '" + wholeLine + '\'' );
    }
  }

  public static double parseDouble( final String key, final String wholeLine ) throws IOException {
    try {
      return Double.parseDouble( wholeLine.substring( key.length() ).trim() );
    } catch ( Exception e ) {
      throw new IOException( "Failed to parse value for Line '" + wholeLine + '\'' );
    }
  }

  public static double[] parseDoubleArray( final String line, final int size ) throws IOException {
    final StringTokenizer strtok = new StringTokenizer( line );
    if ( strtok.countTokens() != ( size + 1 ) ) {
      throw new IOException( "Invalid array specification: " + line );
    }
    strtok.nextToken();
    try {
      final double[] retval = new double[ size ];
      for ( int i = 0; i < size; i++ ) {
        retval[ i ] = Double.parseDouble( strtok.nextToken() );
      }
      return retval;
    } catch ( Exception e ) {
      throw new IOException( "Invalid array specification: " + line );
    }
  }
}
