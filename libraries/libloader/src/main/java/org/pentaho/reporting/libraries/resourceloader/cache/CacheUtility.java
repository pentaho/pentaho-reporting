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
