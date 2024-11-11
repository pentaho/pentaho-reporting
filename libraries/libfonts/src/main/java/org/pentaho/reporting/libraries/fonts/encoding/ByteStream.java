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

/**
 * This is a wrapper around a byte buffer to allows streaming operations. This preserves my sanity, as managing arrays
 * with irregular encodings is hell.
 *
 * @author Thomas Morgner
 */
public class ByteStream {
  private ByteBuffer buffer;
  private byte[] data;
  private int writeCursor;
  private int lastWritePos;
  private int increment;
  private int readCursor;

  public ByteStream( final ByteBuffer buffer, final int increment ) {
    if ( buffer == null ) {
      throw new NullPointerException();
    }
    if ( increment < 1 ) {
      throw new IllegalArgumentException();
    }
    this.buffer = buffer;
    this.data = buffer.getData();
    this.writeCursor = buffer.getCursor();
    this.lastWritePos = data.length - 1;
    this.increment = increment;
    this.readCursor = buffer.getOffset();
  }

  public void put( final byte b ) {
    if ( writeCursor >= lastWritePos ) {
      this.buffer.ensureSize( writeCursor + increment );
      this.data = buffer.getData();
      this.lastWritePos = data.length - 1;
    }

    data[ writeCursor ] = b;
    writeCursor += 1;
  }

  public void put( final byte[] b ) {
    if ( writeCursor >= lastWritePos ) {
      this.buffer.ensureSize( writeCursor + Math.max( increment, b.length ) );
      this.data = buffer.getData();
      this.lastWritePos = data.length - 1;
    }

    System.arraycopy( b, 0, data, writeCursor, b.length );
    writeCursor += b.length;
  }

  public byte get() {
    if ( readCursor < writeCursor ) {
      final byte retval = data[ readCursor ];
      readCursor += 1;
      return retval;
    } else {
      return 0;
    }
  }

  public void close() {
    buffer.setCursor( writeCursor );
  }

  public int getReadSize() {
    return ( writeCursor - readCursor );
  }
}
