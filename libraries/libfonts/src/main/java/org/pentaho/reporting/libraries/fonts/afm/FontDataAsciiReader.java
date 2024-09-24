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

package org.pentaho.reporting.libraries.fonts.afm;

import org.pentaho.reporting.libraries.fonts.io.FontDataInputSource;

import java.io.IOException;

/**
 * Creation-Date: 22.07.2007, 14:03:40
 *
 * @author Thomas Morgner
 */
public class FontDataAsciiReader {
  private byte[] buffer;
  //private int bufferFill;
  private int cursor;

  private FontDataInputSource inputSource;
  private long readPosition;
  private boolean eol;

  public FontDataAsciiReader( final FontDataInputSource inputSource ) {
    this( inputSource, 4096 );
  }

  public FontDataAsciiReader( final FontDataInputSource inputSource, final int bufferSize ) {
    if ( bufferSize < 1 ) {
      throw new IllegalArgumentException();
    }
    if ( inputSource == null ) {
      throw new NullPointerException();
    }
    this.buffer = new byte[ bufferSize ];
    this.cursor = bufferSize;
    this.inputSource = inputSource;
    this.readPosition = 0;
    // this.bufferFill = 0;
  }

  private int read() throws IOException {
    if ( cursor >= inputSource.getLength() ) {
      return -1;
    }

    if ( cursor >= buffer.length ) {
      final int readableLength = Math.min( buffer.length,
        (int) Math.min( 0x7fffffff, inputSource.getLength() - readPosition ) );
      if ( readableLength == 0 ) {
        return -1;
      }

      inputSource.readFullyAt( readPosition, buffer, readableLength );
      if ( readableLength == 0 ) {
        return -1;
      }
      readPosition += readableLength;
      cursor = 0;
    }

    final int retval = ( 0xff & buffer[ cursor ] );
    cursor += 1;
    return retval;
  }

  public String readLine() throws IOException {
    int data = read();
    if ( data == -1 ) {
      return null;
    }

    final StringBuffer retval = new StringBuffer( 150 );
    while ( true ) {
      if ( data == -1 ) {
        return retval.toString();
      } else if ( data == '\n' ) {
        // the next time we will skip the \r
        eol = true;
        return retval.toString();
      } else if ( data == '\r' ) {
        if ( eol == false ) {
          return retval.toString();
        }
        eol = false;
      } else {
        eol = false;
        if ( data > 0x7f ) {
          retval.append( '?' );
        } else {
          retval.append( (char) data );
        }
      }
      data = read();
    }
  }
}
