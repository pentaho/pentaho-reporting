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

import java.io.Serializable;

/**
 * Creation-Date: 20.04.2006, 16:46:50
 *
 * @author Thomas Morgner
 */
public class CodePointBuffer implements Serializable {
  private int[] data;
  private int offset;
  private int cursor;

  public CodePointBuffer( final int[] data ) {
    if ( data == null ) {
      throw new NullPointerException();
    }
    this.data = data;
    this.offset = 0;
    this.cursor = 0;
  }

  public CodePointBuffer( final int length ) {
    this.data = new int[ length ];
    this.offset = 0;
    this.cursor = 0;
  }

  public int[] getBuffer( final int[] buffer ) {
    final int length = getLength();
    if ( length < buffer.length ) {
      System.arraycopy( data, offset, buffer, 0, length );
      return buffer;
    }

    final int[] retval = new int[ length ];
    System.arraycopy( data, offset, retval, 0, length );
    return retval;
  }


  public int[] getBuffer() {
    final int length = getLength();
    final int[] retval = new int[ length ];
    System.arraycopy( data, offset, retval, 0, length );
    return retval;
  }

  public int[] getData() {
    return data;
  }

  public void setData( final int[] data, final int length, final int offset ) {
    if ( data == null ) {
      throw new NullPointerException();
    }
    if ( length < 0 ) {
      throw new IndexOutOfBoundsException( "Length < 0" );
    }
    if ( offset < 0 ) {
      throw new IllegalArgumentException( "Offset < 0" );
    }
    if ( length + offset > data.length ) {
      throw new IllegalArgumentException( "Length + Offset" );
    }
    this.data = data;
    this.cursor = length + offset;
    this.offset = offset;
  }


  public int getLength() {
    return cursor - offset;
  }

  public int getOffset() {
    return offset;
  }

  public int getCursor() {
    return cursor;
  }

  public void ensureSize( final int length ) {
    if ( data.length < ( offset + length ) ) {
      final int[] newdata = new int[ offset + length ];
      System.arraycopy( data, 0, newdata, 0, data.length );
      data = newdata;
    }
  }

  public void setCursor( final int cursor ) {
    if ( cursor < offset ) {
      throw new IndexOutOfBoundsException();
    }
    if ( cursor > data.length ) {
      throw new IndexOutOfBoundsException();
    }
    this.cursor = cursor;
  }
}
