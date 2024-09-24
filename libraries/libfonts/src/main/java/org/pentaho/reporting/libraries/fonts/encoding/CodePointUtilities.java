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
 * Creation-Date: 01.06.2006, 18:10:19
 *
 * @author Thomas Morgner
 */
public class CodePointUtilities {
  public static boolean isValidCodePoint( final int cp ) {
    if ( cp > 0x10FFFF ) {
      return false;
    }
    if ( cp > 0xDFFF ) {
      return true;
    }
    if ( cp >= 0xDC00 ) {
      return false;
    }
    if ( cp < 0 ) {
      return false;
    }
    return true;
  }

  /**
   * Converts the given codepoint into the given character array. The return value indicates either success or failure.
   * The conversion will fail, if the given array does not contain enough space for the decoded character. In that case
   * either -1 (one char missing) or -2 (two chars missing) is returned. On success either 1 or 2 is returned to
   * indicate the number of chars added.
   * <p/>
   * If the buffer has space for at least two chars, then the negative index will never be returned.
   *
   * @param cp
   * @param buffer
   * @param offset
   * @return the number of chars added or the number of additional chars required.
   */
  public static int toChars( final int cp, final char[] buffer, final int offset ) {

    if ( cp < 0x10000 ) {
      if ( offset >= buffer.length ) {
        return -1;
      }

      // maybe simple ..
      if ( cp < 0 ) {
        // invalid ..
        buffer[ offset ] = 0xffff;
      } else if ( cp >= 0xdc00 && cp < 0xE000 ) {
        // invalid ..
        buffer[ offset ] = 0xffff;
      } else {
        buffer[ offset ] = (char) ( cp & 0xFFFF );
      }
      return 1;

    } else {
      if ( cp > 0x10FFFF ) {
        if ( offset >= buffer.length ) {
          return -1;
        }

        // invalid ..
        buffer[ offset ] = 0xffff;
        return 1;
      }

      if ( offset + 1 >= buffer.length ) {
        return -2;
      }

      // convert ...
      final int derivedSourceItem = cp - 0x10000;
      final int highWord = 0xD800 | ( ( derivedSourceItem & 0xFFC00 ) >> 10 );
      final int lowWord = 0xDC00 | ( derivedSourceItem & 0x3FF );
      buffer[ offset ] = (char) highWord;
      buffer[ offset + 1 ] = (char) lowWord;
      return 2;
    }
  }

  public static int[] charsToCodepoint( final String text ) {
    final CodePointBuffer buffer = Utf16LE.getInstance().decodeString( text, null );
    return buffer.getBuffer();
  }

  public static String codepointToChars( final int[] text ) {
    final CodePointBuffer buffer = new CodePointBuffer( text );
    buffer.setCursor( text.length );
    return Utf16LE.getInstance().encodeString( buffer );
  }

  private CodePointUtilities() {
  }
}
