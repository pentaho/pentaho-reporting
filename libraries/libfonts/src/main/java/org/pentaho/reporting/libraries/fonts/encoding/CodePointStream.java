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

/**
 * This is a wrapper around a byte buffer to allows streaming operations. This preserves my sanity, as managing arrays
 * with irregular encodings is hell.
 *
 * @author Thomas Morgner
 */
public class CodePointStream {
  private CodePointBuffer buffer;
  private int[] data;
  private int cursor;
  private int lastWritePos;
  private int increment;

  public CodePointStream( final CodePointBuffer buffer, final int increment ) {
    if ( buffer == null ) {
      throw new NullPointerException();
    }
    if ( increment < 1 ) {
      throw new IllegalArgumentException();
    }
    this.buffer = buffer;
    this.data = buffer.getData();
    this.cursor = buffer.getCursor();
    this.lastWritePos = data.length - 1;
    this.increment = increment;
  }

  public void put( final int b ) {
    if ( cursor >= lastWritePos ) {
      this.buffer.ensureSize( cursor + increment );
      this.data = buffer.getData();
      this.lastWritePos = data.length - 1;
    }

    data[ cursor ] = b;
    cursor += 1;
  }

  public void put( final int[] b ) {
    if ( cursor >= lastWritePos ) {
      this.buffer.ensureSize( cursor + Math.max( increment, b.length ) );
      this.data = buffer.getData();
      this.lastWritePos = data.length - 1;
    }

    System.arraycopy( b, 0, data, cursor, b.length );
    cursor += b.length;
  }

  public void close() {
    buffer.setCursor( cursor );
  }
}
