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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

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
