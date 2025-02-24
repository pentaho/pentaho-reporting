/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.fonts.encoding;

import java.io.Serializable;

/**
 * A simple byte buffer. The length specifies the fill level of the data-array.
 *
 * @author Thomas Morgner
 */
public class ByteBuffer implements Serializable {
  private byte[] data;
  private int offset;
  private int cursor;

  public ByteBuffer( final int length ) {
    if ( length < 0 ) {
      throw new IllegalArgumentException();
    }
    this.data = new byte[ length ];
    this.offset = 0;
    this.cursor = 0;
  }

  public ByteBuffer( final byte[] data ) {
    if ( data == null ) {
      throw new NullPointerException();
    }
    this.data = data;
    this.offset = 0;
    this.cursor = data.length;
  }

  public ByteBuffer( final byte[] data, final int offset, final int length ) {
    if ( length < 0 ) {
      throw new IndexOutOfBoundsException();
    }
    if ( offset < 0 ) {
      throw new IndexOutOfBoundsException();
    }
    if ( data == null ) {
      throw new NullPointerException();
    }
    if ( ( length + offset ) > data.length ) {
      throw new IndexOutOfBoundsException();
    }
    this.data = data;
    this.offset = offset;
    this.cursor = offset + length;
  }

  public byte[] getData() {
    return data;
  }

  public void setData( final byte[] data, final int length, final int offset ) {
    if ( data == null ) {
      throw new IllegalArgumentException();
    }
    if ( length < 0 ) {
      throw new IndexOutOfBoundsException( "Length < 0" );
    }
    if ( offset < 0 ) {
      throw new IllegalArgumentException( "Offset < 0" );
    }
    if ( length + offset >= data.length ) {
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
      final byte[] newdata = new byte[ offset + length ];
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
