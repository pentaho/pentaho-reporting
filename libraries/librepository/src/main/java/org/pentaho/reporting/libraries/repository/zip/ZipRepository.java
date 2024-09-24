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

import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.DefaultMimeRegistry;
import org.pentaho.reporting.libraries.repository.MimeRegistry;
import org.pentaho.reporting.libraries.repository.Repository;
import org.pentaho.reporting.libraries.repository.RepositoryUtilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * A read-write repository based on ZIP streams. The repository can be created using a existing zip file as initial
 * content. The repository will be fully buffered, so nothing is written until the whole repository is closed. For a
 * streaming solution use the zipwriter-repository instead.
 *
 * @author Thomas Morgner
 */
public class ZipRepository implements Repository {
  private ZipContentLocation root;
  private MimeRegistry mimeRegistry;

  public ZipRepository() {
    this( new DefaultMimeRegistry() );
  }

  public ZipRepository( final MimeRegistry mimeRegistry ) {
    if ( mimeRegistry == null ) {
      throw new NullPointerException();
    }

    this.mimeRegistry = mimeRegistry;
    this.root = new ZipContentLocation( this, null, "" );
  }

  public ZipRepository( final InputStream in ) throws IOException {
    this( in, new DefaultMimeRegistry() );
  }

  public ZipRepository( final InputStream in, final MimeRegistry mimeRegistry ) throws IOException {
    this( mimeRegistry );

    final ZipInputStream zipIn = new ZipInputStream( in );
    try {
      ZipEntry nextEntry = zipIn.getNextEntry();
      while ( nextEntry != null ) {
        final String[] buildName = RepositoryUtilities.splitPath( nextEntry.getName(), "/" );
        if ( nextEntry.isDirectory() ) {
          root.updateDirectoryEntry( buildName, 0, nextEntry );
        } else {
          final ByteArrayOutputStream bos = new ByteArrayOutputStream();
          final Deflater def = new Deflater( nextEntry.getMethod() );
          try {
            final DeflaterOutputStream dos = new DeflaterOutputStream( bos, def );
            try {
              IOUtils.getInstance().copyStreams( zipIn, dos );
              dos.flush();
            } finally {
              dos.close();
            }
          } finally {
            def.end();
          }

          root.updateEntry( buildName, 0, nextEntry, bos.toByteArray() );
        }

        zipIn.closeEntry();
        nextEntry = zipIn.getNextEntry();
      }
    } finally {
      zipIn.close();
    }
  }

  public ContentLocation getRoot() throws ContentIOException {
    return root;
  }

  public MimeRegistry getMimeRegistry() {
    return mimeRegistry;
  }

  public void write( final OutputStream outputStream ) throws IOException, ContentIOException {
    RepositoryUtilities.writeAsZip( outputStream, this );
  }

  public void writeToZipStream( final ZipOutputStream zipOutputStream,
                                final Repository repository ) throws IOException, ContentIOException {
    RepositoryUtilities.writeToZipStream( zipOutputStream, repository );
  }
}
