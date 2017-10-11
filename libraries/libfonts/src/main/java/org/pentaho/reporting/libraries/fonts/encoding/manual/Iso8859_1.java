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
