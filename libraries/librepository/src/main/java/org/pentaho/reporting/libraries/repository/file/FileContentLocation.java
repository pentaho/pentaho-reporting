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

package org.pentaho.reporting.libraries.repository.file;

import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.repository.ContentCreationException;
import org.pentaho.reporting.libraries.repository.ContentEntity;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.repository.ContentItem;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.Repository;
import org.pentaho.reporting.libraries.repository.RepositoryUtilities;

import java.io.File;
import java.io.IOException;

/**
 * A content-location that uses a directory as backend.
 *
 * @author Thomas Morgner
 */
public class FileContentLocation extends FileContentEntity implements ContentLocation {
  private static final long serialVersionUID = -5452372293937107734L;

  /**
   * Creates a new location for the given parent and directory.
   *
   * @param parent  the parent location.
   * @param backend the backend.
   * @throws ContentIOException if an error occured or the file did not point to a directory.
   */
  public FileContentLocation( final ContentLocation parent, final File backend ) throws ContentIOException {
    super( parent, backend );
    if ( backend.exists() == false || backend.isDirectory() == false ) {
      throw new ContentIOException( "The given backend-file is not a directory." );
    }
  }

  /**
   * Creates a new root-location for the given repository and directory.
   *
   * @param repository the repository for which a location should be created.
   * @param backend    the backend.
   * @throws ContentIOException if an error occured or the file did not point to a directory.
   */
  public FileContentLocation( final Repository repository, final File backend ) throws ContentIOException {
    super( repository, backend );
    if ( backend.exists() == false || backend.isDirectory() == false ) {
      throw new ContentIOException( "The given backend-file is not a directory." );
    }
  }

  /**
   * Lists all content entities stored in this content-location. This method filters out all files that have an invalid
   * name (according to the repository rules).
   *
   * @return the content entities for this location.
   * @throws ContentIOException if an repository error occured.
   */
  public ContentEntity[] listContents() throws ContentIOException {
    final File file = getBackend();
    final File[] files = file.listFiles();
    final ContentEntity[] entities = new ContentEntity[ files.length ];
    for ( int i = 0; i < files.length; i++ ) {
      final File child = files[ i ];
      if ( RepositoryUtilities.isInvalidPathName( child.getName() ) ) {
        continue;
      }

      if ( child.isDirectory() ) {
        entities[ i ] = new FileContentLocation( this, child );
      } else if ( child.isFile() ) {
        entities[ i ] = new FileContentLocation( this, child );
      }
    }
    return entities;
  }

  /**
   * Returns the content entity with the given name. If the entity does not exist, an Exception will be raised.
   *
   * @param name the name of the entity to be retrieved.
   * @return the content entity for this name, never null.
   * @throws ContentIOException if an repository error occured.
   */
  public ContentEntity getEntry( final String name ) throws ContentIOException {
    if ( RepositoryUtilities.isInvalidPathName( name ) ) {
      throw new IllegalArgumentException( "The name given is not valid." );
    }

    final File file = getBackend();
    final File child = new File( file, name );
    if ( child.exists() == false ) {
      throw new ContentIOException( "Not found:" + child );
    }
    try {
      if ( IOUtils.getInstance().isSubDirectory( file, child ) == false ) {
        throw new ContentIOException( "The given entry does not point to a sub-directory of this content-location" );
      }
    } catch ( IOException e ) {
      throw new ContentIOException( "IO Error.", e );
    }

    if ( child.isDirectory() ) {
      return new FileContentLocation( this, child );
    } else if ( child.isFile() ) {
      return new FileContentItem( this, child );
    } else {
      throw new ContentIOException( "Not File nor directory." );
    }
  }

  /**
   * Creates a new data item in the current location. This method must never return null. This method will fail if an
   * entity with the same name exists in this location.
   *
   * @param name the name of the new entity.
   * @return the newly created entity, never null.
   * @throws ContentCreationException if the item could not be created.
   */
  public ContentItem createItem( final String name ) throws ContentCreationException {
    if ( RepositoryUtilities.isInvalidPathName( name ) ) {
      throw new IllegalArgumentException( "The name given is not valid." );
    }

    final File file = getBackend();
    final File child = new File( file, name );
    if ( child.exists() ) {
      if ( child.length() == 0 ) {
        // probably one of the temp files created by the pentaho-system
        return new FileContentItem( this, child );
      }
      throw new ContentCreationException( "File already exists: " + child );
    }
    try {
      if ( child.createNewFile() == false ) {
        throw new ContentCreationException( "Unable to create the file." );
      }
      return new FileContentItem( this, child );
    } catch ( IOException e ) {
      throw new ContentCreationException( "IOError while create", e );
    }
  }

  /**
   * Creates a new content location in the current location. This method must never return null. This method will fail
   * if an entity with the same name exists in this location.
   *
   * @param name the name of the new entity.
   * @return the newly created entity, never null.
   * @throws ContentCreationException if the item could not be created.
   */
  public ContentLocation createLocation( final String name )
    throws ContentCreationException {
    if ( RepositoryUtilities.isInvalidPathName( name ) ) {
      throw new IllegalArgumentException( "The name given is not valid." );
    }

    final File file = getBackend();
    final File child = new File( file, name );
    if ( child.exists() ) {
      throw new ContentCreationException( "File already exists." );
    }
    if ( child.mkdir() == false ) {
      throw new ContentCreationException( "Unable to create the directory" );
    }
    try {
      return new FileContentLocation( this, child );
    } catch ( ContentIOException e ) {
      throw new ContentCreationException( "Failed to create the content-location", e );
    }
  }

  /**
   * Checks, whether an content entity with the given name exists in this content location. This method will report
   * invalid filenames as non-existent.
   *
   * @param name the name of the new entity.
   * @return true, if an entity exists with this name, false otherwise.
   */
  public boolean exists( final String name ) {
    if ( RepositoryUtilities.isInvalidPathName( name ) ) {
      return false;
    }

    final File file = getBackend();
    final File child = new File( file, name );
    return ( child.exists() );
  }
}
