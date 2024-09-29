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


package org.pentaho.reporting.libraries.pixie.wmf;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * A block of raw mmeory. This is used to store various metafile objects as they are read in from file.
 */
public class Buffer {
  /**
   * The memory itself.
   */
  private byte[] bytes;

  /**
   * The current length of the memory.
   */
  private int length;

  /**
   * Default Constructor. Defines a buffer without an initial size.
   */
  protected Buffer() {
  }

  /**
   * Defines a new buffer with the given initial size in bytes.
   *
   * @param length the length of the buffer in bytes.
   */
  protected Buffer( final int length ) {
    setCapacity( length );
  }

  /**
   * The size of the stored data in the memory.
   */
  public final int getLength() {
    return length;
  }

  /**
   * Extends the length to the given new size.
   *
   * @param len the new length.
   * @throws IllegalArgumentException if the length is shorter than the used storage in memory.
   */
  protected void setLength( final int len ) {
    if ( len > bytes.length ) {
      throw new IllegalArgumentException();
    }

    this.length = len;
  }

  /**
   * Ensures that the buffer has enough space for the given number of bytes.
   *
   * @param capacity the new capacity that should be ensured.
   * @throws IllegalArgumentException if the capacity is smaller than the buffers length.
   */
  protected void setCapacity( final int capacity ) {
    if ( capacity < getLength() ) {
      throw new IllegalArgumentException();
    }

    if ( bytes == null || bytes.length == 0 ) {
      bytes = new byte[ capacity ];
    } else if ( capacity != bytes.length ) {
      final byte[] old = bytes;
      bytes = new byte[ capacity ];
      System.arraycopy( old, 0, bytes, 0, Math.min( old.length, capacity ) );
    }
  }

  /**
   * Read <code>len</code> bytes into the memory from a stream and stores the read bytes at the given offset.
   *
   * @param in     the input stream that should be used
   * @param offset the offset
   * @param len    the number bytes that should be read.
   */
  public void read( final InputStream in, int offset, int len )
    throws IOException {
    // make sure, that all bytes can be read and create the buffer if needed.
    if ( bytes == null || offset + len > bytes.length ) {
      setCapacity( offset + len );
    }

    //in.readFully( bytes, offset, len );
    while ( len > 0 ) {
      final int blockSize = in.read( bytes, offset, len );
      if ( blockSize <= 0 ) {
        throw new EOFException();
      }
      offset += blockSize;
      len -= blockSize;
      setLength( offset );
    }
  }

  /**
   * Moves the buffer contents from the source offset to the target offset, the areas should not overlap.
   *
   * @param sourceoffset
   * @param length
   * @param targetoffset
   */
  protected void move( final int sourceoffset, final int length, final int targetoffset ) {
    System.arraycopy( bytes, sourceoffset, bytes, targetoffset, length );
  }

  /**
   * Set the int value as big-endian.
   *
   * @param offset the offset where to set the int value.
   * @param value  the integer value that should be set.
   */
  public void setInt( final int offset, final int value ) {
    if ( offset > ( getLength() - 4 ) ) {
      throw new IndexOutOfBoundsException();
    }

    setShort( offset, value & 0x0ffff );
    setShort( offset + 2, value >> 16 );
  }

  /**
   * Return the 32-bit int at the given byte offset.
   *
   * @param offset the offset where the integer value is stored in the memory
   * @return the integer.
   */
  public int getInt( final int offset ) {
    if ( offset > ( getLength() - 4 ) ) {
      throw new IndexOutOfBoundsException();
    }

    return ( getShort( offset ) & 0x0ffff ) | ( getShort( offset + 2 ) << 16 );
  }

  /**
   * Stores the given short as BigEndian value.
   *
   * @param offset   the offset.
   * @param shortval the shortvalue.
   */
  public void setShort( final int offset, final int shortval ) {
    if ( offset > ( getLength() - 2 ) ) {
      throw new IndexOutOfBoundsException();
    }

    bytes[ offset ] = (byte) ( shortval & 0x0ff );
    bytes[ offset + 1 ] = (byte) ( shortval >> 8 );
  }

  /**
   * Return the 16-bit int at the given byte offset.
   *
   * @param offset the offset from where to read the short.
   * @return the short.
   */
  public int getShort( final int offset ) {
    if ( offset > ( getLength() - 2 ) ) {
      throw new IndexOutOfBoundsException
        ( "Offset " + offset + " is out of limit. " +
          "Max length is " + ( getLength() - 2 ) );
    }

    return ( bytes[ offset ] & 0x0ff ) | ( bytes[ offset + 1 ] << 8 );
  }

  /**
   * Sets the byte at the given offset.
   *
   * @param offset the offset.
   * @param value  the byte that should be set.
   */
  public void setByte( final int offset, final int value ) {
    if ( offset > ( getLength() - 1 ) ) {
      throw new IndexOutOfBoundsException();
    }

    bytes[ offset ] = (byte) ( value & 0x0ff );
  }

  /**
   * Return the 8-bit int at the given byte offset.
   *
   * @param offset the offset from where to read the byte
   * @return the byte read.
   */
  public int getByte( final int offset ) {
    if ( offset > ( getLength() - 1 ) ) {
      throw new IndexOutOfBoundsException();
    }

    return bytes[ offset ] & 0x0ff;
  }

  /**
   * Writes the given string as byte stream using the plattforms default encoding.
   *
   * @param offset the offset, where to store the string.
   * @param str    the string that should be stored in the Wmf.
   */
  public void setString( final int offset, final String str ) {
    if ( ( offset + str.length() ) > ( getLength() - 1 ) ) {
      throw new IndexOutOfBoundsException();
    }

    final byte[] b = str.getBytes();

    final int len = getLength() - offset;

    for ( int i = 0; i < len; i++ ) {
      bytes[ offset + i ] = b[ offset ];
    }
    if ( ( offset + len ) < getLength() ) {
      bytes[ offset + len ] = 0;
    }
  }

  /**
   * Return the null-terminated string at the given byte offset with the given maximum length.
   *
   * @param offset the offset where the string starts
   * @param len    the maximum length of the string
   * @return the null-terminated string read.
   */
  public String getString( final int offset, final int len ) {
    int i;
    for ( i = 0; i < len; i++ ) {
      if ( bytes[ offset + i ] == 0 ) {
        break;
      }
    }
    return new String( bytes, offset, i );
  }

  /**
   * Gets an input stream to read from the memory buffer.
   *
   * @param offset the offse, from where to read.
   * @return the InputStream.
   */
  public InputStream getInputStream( final int offset ) {
    return new ByteArrayInputStream( bytes, offset, bytes.length - offset );
  }
}
