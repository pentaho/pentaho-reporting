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

import org.pentaho.reporting.libraries.fonts.encoding.ByteBuffer;
import org.pentaho.reporting.libraries.fonts.encoding.CodePointBuffer;
import org.pentaho.reporting.libraries.fonts.encoding.Encoding;
import org.pentaho.reporting.libraries.fonts.encoding.EncodingException;
import org.pentaho.reporting.libraries.fonts.encoding.EncodingRegistry;
import org.pentaho.reporting.libraries.fonts.encoding.manual.Utf16LE;

/**
 * Reads byte-data using a Big-Endian access schema. Big-Endian is used for TrueType fonts.
 *
 * @author Thomas Morgner
 */
public class ByteAccessUtilities {
  private ByteAccessUtilities() {
  }

  public static int readUShort( final byte[] data, final int pos ) {
    return ( ( data[ pos ] & 0xff ) << 8 ) | ( data[ pos + 1 ] & 0xff );
  }

  public static long readULong( final byte[] data, final int pos ) {
    final int c1 = ( data[ pos ] & 0xff );
    final int c2 = ( data[ pos + 1 ] & 0xff );
    final int c3 = ( data[ pos + 2 ] & 0xff );
    final int c4 = ( data[ pos + 3 ] & 0xff );

    long retval = ( (long) c1 << 24 );
    retval |= (long) c2 << 16;
    retval |= (long) c3 << 8;
    retval |= (long) c4;
    return retval;
  }

  public static float readFixed( final byte[] data, final int pos ) {
    final short mantissa = readShort( data, pos );
    final int fraction = readUShort( data, pos + 2 );
    if ( fraction == 0 || mantissa == 0 ) {
      return 0;
    }
    return (float) mantissa / ( fraction / 16384.0f );
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

    long retval = ( (long) c1 << 56 );
    retval |= (long) c2 << 48;
    retval |= (long) c3 << 40;
    retval |= (long) c4 << 32;
    retval |= (long) c5 << 24;
    retval |= (long) c6 << 16;
    retval |= (long) c7 << 8;
    retval |= (long) c8;
    return retval;
  }

  public static byte[] readBytes( final byte[] data,
                                  final int pos, final int length ) {
    final byte[] retval = new byte[ length ];
    System.arraycopy( data, pos, retval, 0, length );
    return retval;
  }

  public static short readShort( final byte[] data, final int pos ) {
    return (short) ( ( data[ pos ] & 0xff ) << 8 | ( data[ pos + 1 ] & 0xff ) );
  }

  public static int readLong( final byte[] data, final int pos ) {
    int retval = 0;
    retval |= (long) ( data[ pos ] & 0xff ) << 24;
    retval |= (long) ( data[ pos + 1 ] & 0xff ) << 16;
    retval |= (long) ( data[ pos + 2 ] & 0xff ) << 8;
    retval |= (long) ( data[ pos + 3 ] & 0xff );
    return retval;
  }

  public static int readZStringOffset( final byte[] data, final int pos, final int maxLength ) {
    final int lastPos = Math.min( pos + maxLength, pos + data.length );
    for ( int i = pos; i < lastPos; i++ ) {
      if ( data[ i ] == 0 ) {
        return i;
      }
    }

    return lastPos;
  }

  public static String readZString( final byte[] data, final int pos, final int maxLength, final String encoding )
    throws EncodingException {
    final int lastPos = Math.min( pos + maxLength, pos + data.length );
    for ( int i = pos; i < lastPos; i++ ) {
      if ( data[ i ] == 0 ) {
        return readString( data, pos, i - pos, encoding );
      }
    }

    return readString( data, pos, lastPos, encoding );
  }

  public static String readString( final byte[] data, final int pos,
                                   final int length, final String encoding )
    throws EncodingException {
    final Encoding enc;
    if ( "UTF-16".equals( encoding ) ) {
      enc = EncodingRegistry.getInstance().getEncoding( "UTF-16LE" );
    } else {
      enc = EncodingRegistry.getInstance().getEncoding( encoding );
    }
    final ByteBuffer byteBuffer = new ByteBuffer( data, pos, length );
    final CodePointBuffer cp = enc.decode( byteBuffer, null );
    return Utf16LE.getInstance().encodeString( cp );
  }
}
