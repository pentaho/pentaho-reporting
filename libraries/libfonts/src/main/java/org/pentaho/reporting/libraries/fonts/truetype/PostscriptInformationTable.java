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
