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


package org.pentaho.reporting.libraries.repository.zipwriter;

import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.DefaultMimeRegistry;
import org.pentaho.reporting.libraries.repository.MimeRegistry;
import org.pentaho.reporting.libraries.repository.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Creation-Date: 01.12.2006, 21:12:39
 *
 * @author Thomas Morgner
 */
public class ZipRepository implements Repository {
  private ZipOutputStream zipOutputStream;
  private MimeRegistry mimeRegistry;
  private ZipContentLocation root;

  public ZipRepository( final OutputStream out,
                        final int level,
                        final MimeRegistry mimeRegistry ) {
    if ( out == null ) {
      throw new NullPointerException();
    }
    if ( mimeRegistry == null ) {
      throw new NullPointerException();
    }

    this.mimeRegistry = mimeRegistry;
    this.zipOutputStream = new ZipOutputStream( out );
    this.zipOutputStream.setLevel( level );
    this.root = new ZipContentLocation( this, null, "" );
  }

  public ZipRepository( final OutputStream out,
                        final int level ) {
    this( out, level, new DefaultMimeRegistry() );
  }

  public ZipRepository( final OutputStream out ) {
    this( out, Deflater.DEFAULT_COMPRESSION, new DefaultMimeRegistry() );
  }

  public ContentLocation getRoot() throws ContentIOException {
    return root;
  }

  public MimeRegistry getMimeRegistry() {
    return mimeRegistry;
  }

  public void close() throws IOException {
    zipOutputStream.finish();
    zipOutputStream.flush();
  }

  public void writeDirectory( final ZipEntry entry ) throws IOException {
    zipOutputStream.putNextEntry( entry );
  }

  public void writeContent( final ZipEntry entry,
                            final InputStream in,
                            final int method,
                            final int compression ) throws IOException {


    zipOutputStream.setMethod( method );
    zipOutputStream.setLevel( compression );
    zipOutputStream.putNextEntry( entry );
    IOUtils.getInstance().copyStreams( in, zipOutputStream );
    zipOutputStream.closeEntry();
  }
}
