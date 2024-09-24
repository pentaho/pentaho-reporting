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
