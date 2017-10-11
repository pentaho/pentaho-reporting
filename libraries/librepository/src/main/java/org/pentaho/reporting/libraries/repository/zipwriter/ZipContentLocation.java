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

package org.pentaho.reporting.libraries.repository.zipwriter;

import org.pentaho.reporting.libraries.repository.ContentCreationException;
import org.pentaho.reporting.libraries.repository.ContentEntity;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.repository.ContentItem;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.LibRepositoryBoot;
import org.pentaho.reporting.libraries.repository.Repository;
import org.pentaho.reporting.libraries.repository.RepositoryUtilities;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.zip.ZipEntry;

/**
 * Creation-Date: 01.12.2006, 21:13:24
 *
 * @author Thomas Morgner
 */
public class ZipContentLocation implements ContentLocation {
  private HashMap entries;
  private String name;
  private String contentId;
  private ContentLocation parent;
  private ZipRepository repository;
  private String comment;
  private long time;

  public ZipContentLocation( final ZipRepository repository,
                             final ContentLocation parent,
                             final String name ) {
    if ( repository == null ) {
      throw new NullPointerException();
    }
    if ( name == null ) {
      throw new NullPointerException();
    }
    this.repository = repository;
    this.parent = parent;
    this.name = name;
    this.entries = new HashMap();
    this.contentId = RepositoryUtilities.buildName( this, "/" ) + '/';
  }

  public ContentEntity[] listContents() throws ContentIOException {
    return (ContentEntity[]) entries.values().toArray
      ( new ContentEntity[ entries.size() ] );
  }

  public ContentEntity getEntry( final String name ) throws ContentIOException {
    final ContentEntity contentEntity = (ContentEntity) entries.get( name );
    if ( contentEntity == null ) {
      throw new ContentIOException( "Not found:" + name );
    }
    return contentEntity;
  }

  /**
   * Creates a new data item in the current location. This method must never return null.
   *
   * @param name
   * @return
   * @throws org.pentaho.reporting.libraries.repository.ContentCreationException if the item could not be created.
   */
  public ContentItem createItem( final String name ) throws ContentCreationException {
    if ( entries.containsKey( name ) ) {
      throw new ContentCreationException( "Entry already exists" );
    }

    if ( RepositoryUtilities.isInvalidPathName( name ) ) {
      throw new ContentCreationException( "Entry-Name is not valid" );
    }
    final ZipContentItem item = new ZipContentItem( name, repository, this );
    entries.put( name, item );
    return item;
  }

  public ContentLocation createLocation( final String name )
    throws ContentCreationException {
    if ( entries.containsKey( name ) ) {
      throw new ContentCreationException( "Entry already exists" );
    }
    if ( RepositoryUtilities.isInvalidPathName( name ) ) {
      throw new ContentCreationException( "Entry-Name is not valid" );
    }

    final ZipContentLocation item = new ZipContentLocation( repository, this, name );
    entries.put( name, item );
    if ( "/".equals( this.contentId ) == false ) {
      try {
        final ZipEntry entry = new ZipEntry( contentId + name + '/' );
        repository.writeDirectory( entry );
      } catch ( IOException e ) {
        throw new ContentCreationException( "Failed to create directory.", e );
      }
    }
    return item;
  }

  public boolean exists( final String name ) {
    return entries.containsKey( name );
  }

  public String getName() {
    return name;
  }

  public Object getContentId() {
    return contentId;
  }

  public Object getAttribute( final String domain, final String key ) {
    if ( LibRepositoryBoot.REPOSITORY_DOMAIN.equals( domain ) ) {
      if ( LibRepositoryBoot.VERSION_ATTRIBUTE.equals( key ) ) {
        return new Date( time );
      }
    } else if ( LibRepositoryBoot.ZIP_DOMAIN.equals( domain ) ) {
      if ( LibRepositoryBoot.ZIP_COMMENT_ATTRIBUTE.equals( key ) ) {
        return comment;
      }
    }
    return null;
  }

  public boolean setAttribute( final String domain, final String key, final Object value ) {
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
    return false;
  }
}
