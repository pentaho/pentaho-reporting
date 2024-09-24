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
