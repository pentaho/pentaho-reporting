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

package org.pentaho.reporting.libraries.fonts.encoding.manual;

import org.pentaho.reporting.libraries.fonts.encoding.ByteBuffer;
import org.pentaho.reporting.libraries.fonts.encoding.CodePointBuffer;
import org.pentaho.reporting.libraries.fonts.encoding.Encoding;
import org.pentaho.reporting.libraries.fonts.encoding.EncodingErrorType;
import org.pentaho.reporting.libraries.fonts.encoding.EncodingException;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class BuiltInJavaEncoding implements Encoding {
  private String name;
  private boolean fastMode;

  public BuiltInJavaEncoding( final String name, final boolean fastMode ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    this.name = name;
    this.fastMode = fastMode;
  }

  public CodePointBuffer decode( final ByteBuffer text, final CodePointBuffer buffer )
    throws EncodingException {
    try {
      final byte[] rawData = text.getData();
      final String decoded =
        new String( rawData, text.getOffset(), text.getLength(), name );
      return Utf16LE.getInstance().decodeString( decoded, buffer );
    } catch ( UnsupportedEncodingException e ) {
      // this should not happen, as the encodings should have been checked by
      // the system already ...
      throw new EncodingException( "Failed to encode the string: " + e.getMessage() );
    }
  }

  public CodePointBuffer decode( final ByteBuffer text, final CodePointBuffer buffer,
                                 final EncodingErrorType errorHandling )
    throws EncodingException {
    return decode( text, buffer );
  }

  /**
   * Encode, but ignore errors.
   *
   * @param text
   * @param buffer
   * @return
   */
  public ByteBuffer encode( final CodePointBuffer text, ByteBuffer buffer )
    throws EncodingException {
    final String javaText =
      Utf16LE.getInstance().encodeString( text );
    try {
      final byte[] data = javaText.getBytes( name );

      final int textLength = text.getLength();
      if ( buffer == null ) {
        buffer = new ByteBuffer( textLength * 2 );
      } else if ( ( buffer.getLength() * 2 ) < textLength ) {
        buffer.ensureSize( textLength * 2 );
      }

      System.arraycopy( data, 0, buffer.getData(), buffer.getOffset(), data.length );
    } catch ( UnsupportedEncodingException e ) {
      // this should not happen, as the encodings should have been checked by
      // the system already ...
      throw new EncodingException( "Failed to encode the string: " + e.getMessage() );
    }
    return buffer;
  }

  public ByteBuffer encode( final CodePointBuffer text, final ByteBuffer buffer,
                            final EncodingErrorType errorHandling )
    throws EncodingException {
    return encode( text, buffer );
  }

  public String getName() {
    return name;
  }

  public String getName( final Locale locale ) {
    return name;
  }

  public boolean isUnicodeCharacterSupported( final int c ) {
    // Damn, either fast or dead slow
    if ( fastMode ) {
      return true;
    }

    // cant test that one ...
    if ( c == 0x3f ) {
      return true;
    }

    final String testEncoding = String.valueOf( (char) c );
    try {
      final byte[] bytes = testEncoding.getBytes( name );
      if ( bytes.length != 1 ) {
        // Assume that everything went well, as if it didn't,
        // it should have created a single-byte sequence
        return true;
      }

      return ( 0x3f != bytes[ 0 ] );
    } catch ( UnsupportedEncodingException e ) {
      // this should not happen, as the encodings should have been checked by
      // the system already ...
      throw new IllegalStateException( "Failed to encode the string: " + e.getMessage() );
    }
  }
}
