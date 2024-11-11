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


package org.pentaho.reporting.libraries.repository.zipreader;

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
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Provides read-only access to ZIP files. The whole zip-file is cached in memory so this input method will fail badly
 * on huge zuip-files.
 *
 * @author Thomas Morgner
 */
public class ZipReadRepository implements Repository {
  private ZipReadContentLocation root;
  private MimeRegistry mimeRegistry;

  public ZipReadRepository( final InputStream in ) throws IOException {
    this( in, new DefaultMimeRegistry() );
  }

  public ZipReadRepository( final InputStream in, final MimeRegistry mimeRegistry ) throws IOException {
    this.mimeRegistry = mimeRegistry;
    root = new ZipReadContentLocation( this, null, "" );

    final ZipInputStream zipIn = new ZipInputStream( in );
    try {
      ZipEntry nextEntry = zipIn.getNextEntry();
      if ( nextEntry == null ) {
        throw new IOException( "This repository is empty or does not point to a ZIP file" );
      }

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
}
