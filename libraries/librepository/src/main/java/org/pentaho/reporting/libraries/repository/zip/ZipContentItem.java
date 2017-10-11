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

package org.pentaho.reporting.libraries.repository.zip;

import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.repository.ContentItem;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.LibRepositoryBoot;
import org.pentaho.reporting.libraries.repository.Repository;
import org.pentaho.reporting.libraries.repository.RepositoryUtilities;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.zip.Deflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;

public class ZipContentItem implements ContentItem {
  private static final byte[] EMPTY_BYTES = new byte[ 0 ];

  private String comment;
  private String name;
  private long size;
  private long time;
  private ZipRepository repository;
  private byte[] rawData;
  private ZipContentLocation parent;
  private String entryName;
  private Integer method;
  private int compression;
  private long crc32;

  public ZipContentItem( final ZipRepository repository,
                         final ZipContentLocation parent,
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
    if ( parent == null ) {
      throw new NullPointerException();
    }

    this.parent = parent;
    this.repository = repository;
    this.comment = zipEntry.getComment();
    this.name = RepositoryUtilities.buildName( this, "/" );
    this.entryName = IOUtils.getInstance().getFileName( name );
    this.size = zipEntry.getSize();
    this.crc32 = zipEntry.getCrc();
    this.time = zipEntry.getTime();
    this.rawData = bytes;
    final int method = zipEntry.getMethod();
    if ( method == ZipEntry.STORED || method == ZipEntry.DEFLATED ) {
      this.method = new Integer( method );
    } else {
      this.method = new Integer( ZipEntry.DEFLATED );
    }
    this.compression = Deflater.DEFAULT_COMPRESSION;
  }

  public ZipContentItem( final ZipRepository repository, final ZipContentLocation parent, final String name ) {
    if ( repository == null ) {
      throw new NullPointerException();
    }
    if ( parent == null ) {
      throw new NullPointerException();
    }
    if ( name == null ) {
      throw new NullPointerException();
    }
    this.repository = repository;
    this.parent = parent;
    this.entryName = name;
    this.name = RepositoryUtilities.buildName( this, "/" );
    this.time = System.currentTimeMillis();
    this.comment = null;
    this.size = 0;
    this.rawData = EMPTY_BYTES;
    this.method = LibRepositoryBoot.ZIP_METHOD_DEFLATED;
    this.compression = Deflater.DEFAULT_COMPRESSION;
  }

  /**
   * This method is a internal method. The raw-data array must be a valid Deflater-output or the content-item will not
   * be able to read the data.
   *
   * @param rawData
   * @param size
   * @param crc32
   */
  public void setRawData( final byte[] rawData, long size, long crc32 ) {
    if ( rawData == null ) {
      throw new NullPointerException();
    }
    this.rawData = rawData;
    this.size = size;
    this.crc32 = crc32;
    this.time = System.currentTimeMillis();
  }

  public String getMimeType() throws ContentIOException {
    return repository.getMimeRegistry().getMimeType( this );
  }

  public OutputStream getOutputStream() throws ContentIOException, IOException {
    return new ZipEntryOutputStream( this );
  }

  public InputStream getInputStream() throws ContentIOException, IOException {
    return new InflaterInputStream( new ByteArrayInputStream( rawData ) );
  }

  public boolean isReadable() {
    return true;
  }

  public boolean isWriteable() {
    return true;
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
      if ( LibRepositoryBoot.ZIP_CRC32_ATTRIBUTE.equals( key ) ) {
        return new Long( crc32 );
      } else if ( LibRepositoryBoot.ZIP_METHOD_ATTRIBUTE.equals( key ) ) {
        return method;
      } else if ( LibRepositoryBoot.ZIP_COMPRESSION_ATTRIBUTE.equals( key ) ) {
        return new Integer( compression );
      }
    }
    return null;
  }

  public boolean setAttribute( String domain, String key, Object value ) {
    if ( LibRepositoryBoot.REPOSITORY_DOMAIN.equals( domain ) ) {
      if ( LibRepositoryBoot.VERSION_ATTRIBUTE.equals( key ) ) {
        if ( value instanceof Date ) {
          final Date n = (Date) value;
          time = n.getTime();
          return true;
        } else if ( value instanceof Number ) {
          final Number n = (Number) value;
          time = n.longValue();
          return true;
        }
      }
    } else if ( LibRepositoryBoot.ZIP_DOMAIN.equals( domain ) ) {
      if ( LibRepositoryBoot.ZIP_COMMENT_ATTRIBUTE.equals( key ) ) {
        if ( value != null ) {
          comment = String.valueOf( value );
          return true;
        } else {
          comment = null;
          return true;
        }
      }
      if ( LibRepositoryBoot.ZIP_METHOD_ATTRIBUTE.equals( key ) ) {
        if ( LibRepositoryBoot.ZIP_METHOD_STORED.equals( value ) ) {
          method = LibRepositoryBoot.ZIP_METHOD_STORED;
          return true;
        } else if ( LibRepositoryBoot.ZIP_METHOD_DEFLATED.equals( value ) ) {
          method = LibRepositoryBoot.ZIP_METHOD_DEFLATED;
          return true;
        }
      }
      if ( LibRepositoryBoot.ZIP_COMPRESSION_ATTRIBUTE.equals( key ) ) {
        if ( value instanceof Integer ) {
          final Integer valueInt = (Integer) value;
          final int compression = valueInt.intValue();
          if ( compression >= 0 && compression <= 9 ) {
            this.compression = compression;
            return true;
          }
        }
      }
    }
    return false;
  }

  public ContentLocation getParent() {
    return parent;
  }

  public Repository getRepository() {
    return repository;
  }

  public boolean delete() {
    return parent.removeEntity( this );
  }

}
