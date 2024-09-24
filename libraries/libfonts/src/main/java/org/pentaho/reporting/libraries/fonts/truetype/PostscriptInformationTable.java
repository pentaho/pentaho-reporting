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

package org.pentaho.reporting.libraries.fonts.truetype;

import org.pentaho.reporting.libraries.fonts.ByteAccessUtilities;

/**
 * Creation-Date: 06.11.2005, 20:24:42
 *
 * @author Thomas Morgner
 */
public class PostscriptInformationTable implements FontTable {
  public static final long TABLE_ID =
    ( 'p' << 24 | 'o' << 16 | 's' << 8 | 't' );
  private float version;
  private float italicAngle;
  private short underlinePosition;
  private short underlineThickness;
  private boolean fixedPitch;

  public PostscriptInformationTable( final byte[] data ) {
    version = ByteAccessUtilities.readFixed( data, 0 );
    italicAngle = ByteAccessUtilities.readFixed( data, 4 );
    underlinePosition = ByteAccessUtilities.readShort( data, 8 );
    underlineThickness = ByteAccessUtilities.readShort( data, 10 );
    fixedPitch = ByteAccessUtilities.readULong( data, 12 ) != 0;
  }

  public float getVersion() {
    return version;
  }

  public float getItalicAngle() {
    return italicAngle;
  }

  public short getUnderlinePosition() {
    return underlinePosition;
  }

  public short getUnderlineThickness() {
    return underlineThickness;
  }

  public boolean isFixedPitch() {
    return fixedPitch;
  }

  public long getName() {
    return TABLE_ID;
  }
}
