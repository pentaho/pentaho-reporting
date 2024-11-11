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

import java.util.Locale;

/**
 * This is a lucky case, as ISO-8859-1 can be transformed directly. There is no lookup step needed.
 *
 * @author Thomas Morgner
 */
public final class Iso8859_1 implements Encoding {
  public Iso8859_1() {
  }

  public String getName() {
    return "ISO-8859-1";
  }

  public String getName( final Locale locale ) {
    return getName();
  }

  public boolean isUnicodeCharacterSupported( final int c ) {
    return ( c & 0xFFFFFF00 ) == 0;
  }

  /**
   * Encode, but ignore errors.
   *
   * @param text
   * @param buffer
   * @return
   */
  public ByteBuffer encode( final CodePointBuffer text, ByteBuffer buffer ) {
    final int textLength = text.getLength();
    if ( buffer == null ) {
      buffer = new ByteBuffer( textLength );
    } else if ( buffer.getLength() < textLength ) {
      buffer.ensureSize( textLength );
    }

    final byte[] targetArray = buffer.getData();
    final int[] sourceArray = text.getData();

    int targetIdx = buffer.getOffset();
    final int endPos = text.getCursor();
    for ( int i = text.getOffset(); i < endPos; i++ ) {
      final int sourceItem = sourceArray[ i ];
      if ( isUnicodeCharacterSupported( sourceItem ) ) {
        targetArray[ targetIdx ] = (byte) ( sourceItem & 0xff );
        targetIdx += 1;
      }
    }

    buffer.setCursor( targetIdx );
    return buffer;
  }

  public CodePointBuffer decode( final ByteBuffer text, CodePointBuffer buffer ) {
    final int textLength = text.getLength();
    if ( buffer == null ) {
      buffer = new CodePointBuffer( textLength );
    } else if ( buffer.getLength() < textLength ) {
      buffer.ensureSize( textLength );
    }

    final int[] targetArray = buffer.getData();
    final byte[] sourceArray = text.getData();

    int targetIdx = buffer.getOffset();
    final int endPos = text.getCursor();
    for ( int i = text.getOffset(); i < endPos; i++ ) {
      targetArray[ targetIdx ] = ( sourceArray[ i ] & 0xff );
      targetIdx += 1;
    }

    buffer.setCursor( targetIdx );
    return buffer;
  }

  public ByteBuffer encode( final CodePointBuffer text,
                            ByteBuffer buffer,
                            final EncodingErrorType errorHandling )
    throws EncodingException {
    final int textLength = text.getLength();
    if ( buffer == null ) {
      buffer = new ByteBuffer( textLength );
    } else if ( buffer.getLength() < textLength ) {
      buffer.ensureSize( textLength );
    }

    final byte[] targetArray = buffer.getData();
    final int[] sourceArray = text.getData();

    int targetIdx = buffer.getOffset();
    final int endPos = text.getCursor();
    for ( int i = text.getOffset(); i < endPos; i++ ) {
      final int sourceItem = sourceArray[ i ];
      if ( isUnicodeCharacterSupported( sourceItem ) ) {
        targetArray[ targetIdx ] = (byte) ( sourceItem & 0xff );
        targetIdx += 1;
      } else {
        if ( EncodingErrorType.REPLACE.equals( errorHandling ) ) {
          targetArray[ targetIdx ] = (byte) ( '?' & 0xff );
          targetIdx += 1;
        } else if ( EncodingErrorType.FAIL.equals( errorHandling ) ) {
          throw new EncodingException();
        }
      }
    }

    buffer.setCursor( targetIdx );
    return buffer;
  }

  public CodePointBuffer decode( final ByteBuffer text,
                                 CodePointBuffer buffer,
                                 final EncodingErrorType errorHandling )
    throws EncodingException {
    final int textLength = text.getLength();
    if ( buffer == null ) {
      buffer = new CodePointBuffer( textLength );
    } else if ( buffer.getLength() < textLength ) {
      buffer.ensureSize( textLength );
    }

    final int[] targetArray = buffer.getData();
    final byte[] sourceArray = text.getData();

    int targetIdx = buffer.getOffset();
    final int endPos = text.getCursor();
    for ( int i = text.getOffset(); i < endPos; i++ ) {
      targetArray[ targetIdx ] = ( sourceArray[ i ] & 0xff );
      targetIdx += 1;
    }

    buffer.setCursor( targetIdx );
    return buffer;
  }

}
