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
import org.pentaho.reporting.libraries.repository.ContentCreationException;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.repository.ContentItem;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.LibRepositoryBoot;
import org.pentaho.reporting.libraries.repository.Repository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;

/**
 * Creation-Date: 17.12.2007, 12:19:20
 *
 * @author Thomas Morgner
 */
public class ZipReadContentItem implements ContentItem {
  private String comment;
  private String name;
  private long size;
  private long time;
  private ZipReadRepository repository;
  private byte[] rawData;
  private ZipReadContentLocation parent;
  private String entryName;

  public ZipReadContentItem( final ZipReadRepository repository,
                             final ZipReadContentLocation parent,
                             final ZipEntry zipEntry,
                             final byte[] bytes ) {
    if ( repository == null ) {
      throw new NullPointerException();
    }
    if ( zipEntry == null ) {
      throw new NullPointerException();
    }
    if ( bytes == null ) {
      throw new NullPointerException();
    }

    this.parent = parent;
    this.repository = repository;
    this.comment = zipEntry.getComment();
    this.name = zipEntry.getName();
    this.entryName = IOUtils.getInstance().getFileName( name );
    this.size = zipEntry.getSize();
    this.time = zipEntry.getTime();
    this.rawData = bytes;
  }

  public String getMimeType() throws ContentIOException {
    return repository.getMimeRegistry().getMimeType( this );
  }

  public OutputStream getOutputStream() throws ContentIOException, IOException {
    throw new ContentCreationException( "This repository is read-only" );
  }

  public InputStream getInputStream() throws ContentIOException, IOException {
    return new InflaterInputStream( new ByteArrayInputStream( rawData ) );
  }

  public boolean isReadable() {
    return true;
  }

  public boolean isWriteable() {
    return false;
  }

  public String getName() {
    return entryName;
  }

  public Object getContentId() {
    return name;
  }

  public Object getAttribute( String domain, String key ) {
    if ( LibRepositoryBoot.REPOSITORY_DOMAIN.equals( domain ) ) {
      if ( LibRepositoryBoot.SIZE_ATTRIBUTE.equals( key ) ) {
        return new Long( size );
      } else if ( LibRepositoryBoot.VERSION_ATTRIBUTE.equals( key ) ) {
        return new Date( time );
      }
    } else if ( LibRepositoryBoot.ZIP_DOMAIN.equals( domain ) ) {
      if ( LibRepositoryBoot.ZIP_COMMENT_ATTRIBUTE.equals( key ) ) {
        return comment;
      }
    }
    return null;
  }

  public boolean setAttribute( String domain, String key, Object value ) {
    return false;
  }

  public ContentLocation getParent() {
    return parent;
  }

  public Repository getRepository() {
    return repository;
  }

  public boolean delete() {
    return false;
  }
}
