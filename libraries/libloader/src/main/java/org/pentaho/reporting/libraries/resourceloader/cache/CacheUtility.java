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

package org.pentaho.reporting.libraries.resourceloader.cache;

import org.pentaho.reporting.libraries.resourceloader.ResourceKey;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Creation-Date: Feb 22, 2007, 7:36:19 PM
 *
 * @author Thomas Morgner
 */
public class CacheUtility {
  public static String externalizeKey( final ResourceKey key ) {
    try {
      final ByteArrayOutputStream bout = new ByteArrayOutputStream();
      final ObjectOutputStream oout = new ObjectOutputStream( bout );
      oout.writeObject( key );
      oout.close();
      final byte[] serializedKeyData = bout.toByteArray();
      return convertToString( serializedKeyData );
    } catch ( IOException ioe ) {
      return null;
    }
  }

  private static String convertToString( final byte[] serializedKeyData ) {
    final int capacity = ( serializedKeyData.length / 2 ) + 2;
    final StringBuffer buffer = new StringBuffer( capacity );
    char data = 0;
    if ( ( serializedKeyData.length & 1 ) == 1 ) {
      buffer.append( '*' );
    } else {
      buffer.append( '#' );
    }

    for ( int i = 0; i < serializedKeyData.length; i += 1 ) {
      data <<= 8;
      data |= serializedKeyData[ i ];
      if ( ( i & 1 ) == 1 ) {
        buffer.append( data );
      }
    }
    if ( ( serializedKeyData.length & 1 ) == 1 ) {
      data <<= 8;
      buffer.append( data );
    }
    return buffer.toString();
  }

  private CacheUtility() {
  }
}
