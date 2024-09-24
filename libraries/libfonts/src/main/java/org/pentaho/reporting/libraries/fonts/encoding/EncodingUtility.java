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

package org.pentaho.reporting.libraries.fonts.encoding;

import org.pentaho.reporting.libraries.fonts.encoding.manual.Utf16LE;

/**
 * Creation-Date: 21.07.2007, 19:44:31
 *
 * @author Thomas Morgner
 */
public class EncodingUtility {
  private EncodingUtility() {
  }

  public static String encode( final byte[] data, final String encoding ) throws EncodingException {
    final Encoding enc;
    if ( "UTF-16".equals( encoding ) ) {
      enc = EncodingRegistry.getInstance().getEncoding( "UTF-16LE" );
    } else {
      enc = EncodingRegistry.getInstance().getEncoding( encoding );
    }

    final ByteBuffer byteBuffer = new ByteBuffer( data );
    final CodePointBuffer cp = enc.decode( byteBuffer, null );
    return Utf16LE.getInstance().encodeString( cp );
  }
}
