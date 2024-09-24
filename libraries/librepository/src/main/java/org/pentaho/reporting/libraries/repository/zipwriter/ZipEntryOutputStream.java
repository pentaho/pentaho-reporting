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

package org.pentaho.reporting.libraries.repository.zipwriter;

import org.pentaho.reporting.libraries.repository.LibRepositoryBoot;
import org.pentaho.reporting.libraries.repository.RepositoryUtilities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;

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
  private Deflater deflater;

  public ZipEntryOutputStream( final ZipContentItem item ) {
    if ( item == null ) {
      throw new NullPointerException();
    }
    this.item = item;
    this.outputStream = new ByteArrayOutputStream();
    this.deflater = new Deflater( RepositoryUtilities.getZipLevel( item ) );
    this.deflaterOutputStream = new DeflaterOutputStream( outputStream, deflater );
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
    deflater.finish();
    deflater.end();

    final byte[] data = outputStream.toByteArray();
    final ByteArrayInputStream bin = new ByteArrayInputStream( data );
    final InflaterInputStream infi = new InflaterInputStream( bin );

    final ZipRepository repository = (ZipRepository) item.getRepository();

    final String contentId = (String) item.getContentId();
    final ZipEntry zipEntry = new ZipEntry( contentId );

    final Object comment = item.getAttribute( LibRepositoryBoot.ZIP_DOMAIN, LibRepositoryBoot.ZIP_COMMENT_ATTRIBUTE );
    if ( comment != null ) {
      zipEntry.setComment( String.valueOf( comment ) );
    }
    final Object version =
      item.getAttribute( LibRepositoryBoot.REPOSITORY_DOMAIN, LibRepositoryBoot.VERSION_ATTRIBUTE );
    if ( version instanceof Date ) {
      final Date date = (Date) version;
      zipEntry.setTime( date.getTime() );
    }

    final int zipMethod = RepositoryUtilities.getZipMethod( item );
    zipEntry.setCrc( crc32.getValue() );
    if ( zipMethod == Deflater.NO_COMPRESSION ) {
      zipEntry.setCompressedSize( size );
      zipEntry.setSize( size );
    } else {
      zipEntry.setSize( size );
    }
    repository.writeContent( zipEntry, infi, zipMethod, RepositoryUtilities.getZipLevel( item ) );
    infi.close();

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
}
