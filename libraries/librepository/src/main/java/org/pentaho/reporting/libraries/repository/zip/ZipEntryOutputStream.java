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


package org.pentaho.reporting.libraries.repository.zip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.util.zip.DeflaterOutputStream;

/**
 * A fully buffered stream.
 *
 * @author Thomas Morgner
 */
public class ZipEntryOutputStream extends OutputStream {
  private ByteArrayOutputStream outputStream;
  private DeflaterOutputStream deflaterOutputStream;
  private boolean closed;
  private ZipContentItem item;
  private CRC32 crc32;
  private long size;

  public ZipEntryOutputStream( final ZipContentItem item ) {
    if ( item == null ) {
      throw new NullPointerException();
    }

    this.item = item;
    this.outputStream = new ByteArrayOutputStream();
    this.deflaterOutputStream = new DeflaterOutputStream( outputStream );
    this.crc32 = new CRC32();
    this.size = 0;
  }

  public void write( final int b )
    throws IOException {
    if ( closed ) {
      throw new IOException( "Already closed" );
    }
    deflaterOutputStream.write( b );
    crc32.update( b );
    size += 1;
  }

  public void write( final byte[] b, final int off, final int len )
    throws IOException {
    if ( closed ) {
      throw new IOException( "Already closed" );
    }
    deflaterOutputStream.write( b, off, len );
    crc32.update( b, off, len );
    size += len;
  }

  public void close()
    throws IOException {
    if ( closed ) {
      // A duplicate close is just a NO-OP as with all other output streams.
      return;
    }

    deflaterOutputStream.close();
    item.setRawData( outputStream.toByteArray(), size, crc32.getValue() );

    closed = true;
    outputStream = null;
    deflaterOutputStream = null;
  }

  public void write( final byte[] b )
    throws IOException {
    if ( closed ) {
      throw new IOException( "Already closed" );
    }
    deflaterOutputStream.write( b );
    crc32.update( b );
    size += b.length;
  }

  public void flush()
    throws IOException {
    if ( closed ) {
      throw new IOException( "Already closed" );
    }
    deflaterOutputStream.flush();
  }

  public long getSize() {
    return size;
  }

  public long getCrc() {
    return crc32.getValue();
  }
}
