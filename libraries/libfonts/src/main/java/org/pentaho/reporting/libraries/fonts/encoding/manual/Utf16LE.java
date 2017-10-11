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
import org.pentaho.reporting.libraries.fonts.encoding.ByteStream;
import org.pentaho.reporting.libraries.fonts.encoding.CodePointBuffer;
import org.pentaho.reporting.libraries.fonts.encoding.CodePointStream;
import org.pentaho.reporting.libraries.fonts.encoding.ComplexEncoding;
import org.pentaho.reporting.libraries.fonts.encoding.EncodingErrorType;
import org.pentaho.reporting.libraries.fonts.encoding.EncodingException;

import java.util.Locale;

public class Utf16LE implements ComplexEncoding {
  public static final int MAX_CHAR = 0x10FFFD;
  private static Utf16LE instance;

  public static synchronized Utf16LE getInstance() {
    if ( instance == null ) {
      instance = new Utf16LE();
    }
    return instance;
  }

  public Utf16LE() {
  }

  public String getName() {
    return "UTF-16LE";
  }

  public String getName( final Locale locale ) {
    return "UTF-16LE";
  }

  public boolean isUnicodeCharacterSupported( final int c ) {
    return ( c > 0 ) && ( c < MAX_CHAR ) && // this is the maximum number of characters defined.
      ( c & 0xFFFFF800 ) == 0xD800; // this is the replacement zone.
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
      buffer = new ByteBuffer( textLength * 2 );
    } else if ( ( buffer.getLength() * 2 ) < textLength ) {
      buffer.ensureSize( textLength * 2 );
    }

    final ByteStream target = new ByteStream( buffer, textLength );
    final int[] sourceArray = text.getData();
    final int endPos = text.getCursor();
    for ( int i = text.getOffset(); i < endPos; i++ ) {
      final int sourceItem = sourceArray[ i ];
      if ( sourceItem < 0 || sourceItem > MAX_CHAR ) {
        continue;
      }

      if ( sourceItem <= 0xFFFF ) {
        if ( sourceItem >= 0xD800 && sourceItem <= 0xDFFF ) {
          // this is an error condition. We ignore it for now ..
          continue;
        }

        target.put( (byte) ( ( sourceItem & 0xff00 ) >> 8 ) );
        target.put( (byte) ( sourceItem & 0xff ) );
      } else {
        // compute the weird replacement mode chars ..
        final int derivedSourceItem = sourceItem - 0x10000;
        final int highWord = 0xD800 | ( ( derivedSourceItem & 0xFFC00 ) >> 10 );
        target.put( (byte) ( ( highWord & 0xff00 ) >> 8 ) );
        target.put( (byte) ( highWord & 0xff ) );

        final int lowWord = 0xDC00 | ( derivedSourceItem & 0x3FF );
        target.put( (byte) ( ( lowWord & 0xff00 ) >> 8 ) );
        target.put( (byte) ( lowWord & 0xff ) );
      }
    }

    target.close();
    return buffer;
  }

  public CodePointBuffer decode( final ByteBuffer text, CodePointBuffer buffer ) {
    final int textLength = text.getLength();
    if ( buffer == null ) {
      buffer = new CodePointBuffer( textLength / 2 );
    } else if ( ( buffer.getLength() / 2 ) < textLength ) {
      buffer.ensureSize( textLength / 2 );
    }


    final int[] targetData = buffer.getData();
    final ByteStream sourceBuffer = new ByteStream( text, 10 );

    // this construct gives us an even number ...
    int position = buffer.getOffset();
    while ( sourceBuffer.getReadSize() >= 2 ) {
      final int highByte = ( sourceBuffer.get() & 0xff );
      final int lowByte = ( sourceBuffer.get() & 0xff );

      if ( ( highByte & 0xFC ) == 0xD8 ) {
        if ( sourceBuffer.getReadSize() < 2 ) {
          // we reached the end of the parsable stream ...
          // this is an error condition
          // Log.debug("Reached the end ..");
          break;
        }

        final int highByteL = ( sourceBuffer.get() & 0xff );
        final int lowByteL = ( sourceBuffer.get() & 0xff );


        if ( ( highByteL & 0xFC ) == 0xDC ) {
          // decode the extended CodePoint ...
          int result = lowByteL;
          result |= ( highByteL & 0x03 ) << 8;
          result |= lowByte << 10;
          result |= ( highByte & 0x03 ) << 18;
          targetData[ position ] = result + 0x10000;
          position += 1;
        } else {
          // this is an error condition.
          // Log.debug("error 1..");
        }
      } else if ( ( highByte & 0xFC ) == 0xDC ) {
        // this is an error condition ..
        // skip this word ..
        // Log.debug("error 2..");
      } else {
        // decode the simple mode ...
        targetData[ position ] = ( highByte << 8 ) | lowByte;
        position += 1;
      }
    }
    buffer.setCursor( position );
    return buffer;
  }

  public ByteBuffer encode( final CodePointBuffer text,
                            final ByteBuffer buffer,
                            final EncodingErrorType errorHandling )
    throws EncodingException {
    return encode( text, buffer );
  }

  public CodePointBuffer decode( final ByteBuffer text,
                                 final CodePointBuffer buffer,
                                 final EncodingErrorType errorHandling )
    throws EncodingException {
    return decode( text, buffer );
  }

  /**
   * Checks, whether this implementation supports encoding of character data.
   *
   * @return
   */
  public boolean isEncodingSupported() {
    return true;
  }

  public CodePointBuffer decodeString( final String text, final CodePointBuffer buffer ) {
    final char[] chars = text.toCharArray();
    final int textLength = chars.length;
    return decode( chars, 0, textLength, buffer );
  }

  public CodePointBuffer decode( final char[] chars, final int offset, final int length, CodePointBuffer buffer ) {
    if ( buffer == null ) {
      buffer = new CodePointBuffer( length );
    } else if ( ( buffer.getLength() ) < length ) {
      buffer.ensureSize( length );
    }

    final CodePointStream cps = new CodePointStream( buffer, 10 );
    final int maxPos = offset + length;
    for ( int i = offset; i < maxPos; i++ ) {
      final char c = chars[ i ];
      if ( ( c & 0xFC00 ) == 0xD800 ) {
        i += 1;
        if ( i < maxPos ) {
          final char c2 = chars[ i ];
          if ( ( c2 & 0xFC00 ) == 0xDC00 ) {
            final int codePoint = 0x10000 +
              ( ( c2 & 0x3FF ) | ( ( c & 0x3FF ) << 10 ) );
            cps.put( codePoint );
          } else {
            // Should not happen ..
          }
        } else {
          // illegal char .. ignore it ..
          // of course: This should not happen, as this produced by JDK code
          break;
        }
      } else {
        cps.put( c );
      }
    }
    cps.close();
    return buffer;
  }

  public String encodeString( final CodePointBuffer buffer ) {
    final StringBuffer stringBuffer = new StringBuffer( buffer.getLength() * 3 / 2 );
    final int[] data = buffer.getData();
    final int endPos = buffer.getCursor();

    for ( int i = buffer.getOffset(); i < endPos; i++ ) {
      final int codePoint = data[ i ];
      if ( codePoint < 0x10000 ) {
        stringBuffer.append( (char) codePoint );
      } else {
        // oh, no, we have to decode ...
        // compute the weird replacement mode chars ..
        final int derivedSourceItem = codePoint - 0x10000;
        final int highWord = 0xD800 | ( ( derivedSourceItem & 0xFFC00 ) >> 10 );
        final int lowWord = 0xDC00 | ( derivedSourceItem & 0x3FF );
        stringBuffer.append( (char) highWord );
        stringBuffer.append( (char) lowWord );
      }
    }
    //    Log.debug ("Encoded:" + stringBuffer + " (" + buffer.getOffset() + ", " + endPos + ")");
    return stringBuffer.toString();
  }

  //  public static void main (String[] args)
  //          throws UnsupportedEncodingException
  //  {
  //    Utf16LE utf = new Utf16LE();
  //    final String text = "The lazy fox jumps over the lemon tree";
  //    byte[] bytes = text.getBytes("UTF16");
  //    CodePointBuffer cp = utf.decode
  //            (new ByteBuffer(bytes), new CodePointBuffer(text.length()));
  //    int[] cps = cp.getData();
  //
  //    final int length = cp.getLength();
  //    for (int i = 1; i < length; i++)
  //    {
  //      int cp1 = cps[i];
  //      if (cp1 != text.charAt(i - 1))
  //      {
  //        throw new IllegalStateException("Error at " + i + ": " +
  //                Integer.toHexString(cp1) + " vs " +
  //                Integer.toHexString(text.charAt(i - 1)));
  //      }
  //    }
  //  }
}
