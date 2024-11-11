/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.fonts;

/**
 * Reads byte-data using a Little-Endian access schema. Little-Endian is used for Type1 fonts.
 *
 * @author Thomas Morgner
 */
public class LEByteAccessUtilities {
  private LEByteAccessUtilities() {
  }

  public static int readUShort( final byte[] data, final int pos ) {
    return ( ( data[ pos + 1 ] & 0xff ) << 8 ) | ( data[ pos ] & 0xff );
  }

  public static long readULong( final byte[] data, final int pos ) {
    final int c1 = ( data[ pos ] & 0xff );
    final int c2 = ( data[ pos + 1 ] & 0xff );
    final int c3 = ( data[ pos + 2 ] & 0xff );
    final int c4 = ( data[ pos + 3 ] & 0xff );

    long retval = ( (long) c4 << 24 );
    retval |= (long) c3 << 16;
    retval |= (long) c2 << 8;
    retval |= (long) c1;
    return retval;
  }

  public static long readLongDateTime( final byte[] data, final int pos ) {
    final int c1 = ( data[ pos ] & 0xff );
    final int c2 = ( data[ pos + 1 ] & 0xff );
    final int c3 = ( data[ pos + 2 ] & 0xff );
    final int c4 = ( data[ pos + 3 ] & 0xff );
    final int c5 = ( data[ pos + 4 ] & 0xff );
    final int c6 = ( data[ pos + 5 ] & 0xff );
    final int c7 = ( data[ pos + 6 ] & 0xff );
    final int c8 = ( data[ pos + 7 ] & 0xff );

    long retval = ( (long) c8 << 56 );
    retval |= (long) c7 << 48;
    retval |= (long) c6 << 40;
    retval |= (long) c5 << 32;
    retval |= (long) c4 << 24;
    retval |= (long) c3 << 16;
    retval |= (long) c2 << 8;
    retval |= (long) c1;
    return retval;
  }

  public static short readShort( final byte[] data, final int pos ) {
    return (short) ( ( data[ pos + 1 ] & 0xff ) << 8 | ( data[ pos ] & 0xff ) );
  }

  public static int readLong( final byte[] data, final int pos ) {
    int retval = 0;
    retval |= (long) ( data[ pos + 3 ] & 0xff ) << 24;
    retval |= (long) ( data[ pos + 2 ] & 0xff ) << 16;
    retval |= (long) ( data[ pos + 1 ] & 0xff ) << 8;
    retval |= (long) ( data[ pos ] & 0xff );
    return retval;
  }

}
