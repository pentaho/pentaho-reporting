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
