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
