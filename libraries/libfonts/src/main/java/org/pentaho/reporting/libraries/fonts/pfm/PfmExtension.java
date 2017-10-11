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

package org.pentaho.reporting.libraries.fonts.pfm;

import org.pentaho.reporting.libraries.fonts.LEByteAccessUtilities;

import java.io.IOException;

/**
 * This table contains offsets to other tables. It directly follows the header.
 *
 * @author Thomas Morgner
 */
public class PfmExtension {
  private int extMetricsOffset;
  private int extentTable;
  private int originTable;
  private int pairKernTable;
  private int trackKernTable;
  private int driverInfo;
  public static final int LENGTH = 30;

  public PfmExtension( final byte[] data ) throws IOException {
    if ( LEByteAccessUtilities.readShort( data, 0 ) != 30 ) {
      throw new IOException( "Extended-Header must be 30 bytes long." );
    }
    extMetricsOffset = LEByteAccessUtilities.readLong( data, 2 );
    extentTable = LEByteAccessUtilities.readLong( data, 6 );
    originTable = LEByteAccessUtilities.readLong( data, 10 );
    pairKernTable = LEByteAccessUtilities.readLong( data, 14 );
    trackKernTable = LEByteAccessUtilities.readLong( data, 18 );

    // driver info is a pointer to the full-qualified font name. Unlike Adobes description, this is *not* part
    // of the postscript information section (which is no three-entry-section anyway.)
    driverInfo = LEByteAccessUtilities.readLong( data, 22 );
    // reserved 4 bytes follow
  }

  public int getExtMetricsOffset() {
    return extMetricsOffset;
  }

  public int getExtentTable() {
    return extentTable;
  }

  public int getOriginTable() {
    return originTable;
  }

  public int getPairKernTable() {
    return pairKernTable;
  }

  public int getTrackKernTable() {
    return trackKernTable;
  }

  public int getDriverInfo() {
    return driverInfo;
  }
}
