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

package org.pentaho.reporting.libraries.repository.stream;

import org.pentaho.reporting.libraries.repository.ContentCreationException;
import org.pentaho.reporting.libraries.repository.ContentEntity;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.repository.ContentItem;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.Repository;

/**
 * A content location that wraps around a single stream. The location will reject any attempts to create new entities or
 * to access entities other than the single entity.
 *
 * @author Thomas Morgner
 */
public class StreamContentLocation implements ContentLocation {
  private ContentItem contentItem;
  private StreamRepository repository;
  private static final ContentEntity[] EMPTY_CONTENT_ENTITY = new ContentEntity[ 0 ];

  /**
   * Creates a new stream-location. There can be only one location per stream-repository.
   *
   * @param repository the repository for which a location is created.
   */
  public StreamContentLocation( final StreamRepository repository ) {
    if ( repository == null ) {
      throw new NullPointerException();
    }
    this.repository = repository;
  }

  /**
   * Returns all content entities stored in this content-location. This returns a array that has at most one entry. If
   * the repository is a write-only repository and no item has been created yet, the method returns an empty array.
   *
   * @return the content entities for this location.
   * @throws ContentIOException if an repository error occured.
   */
  public ContentEntity[] listContents() throws ContentIOException {
    final WrappedInputStream in = repository.getInputStream();
    if ( in != null && contentItem == null ) {
      this.contentItem = new StreamContentItem
        ( repository.getContentName(), this, in, repository.getOutputStream() );
    }

    if ( contentItem == null ) {
      return EMPTY_CONTENT_ENTITY;
    } else {
      return new ContentEntity[] { contentItem };
    }
  }

  /**
   * Returns the content entity with the given name. If the entity does not exist, an Exception will be raised.
   *
   * @param name the name of the entity to be retrieved.
   * @return the content entity for this name, never null.
   * @throws ContentIOException if an repository error occured.
   */
  public ContentEntity getEntry( final String name ) throws ContentIOException {
    final WrappedInputStream in = repository.getInputStream();
    if ( in != null && contentItem == null ) {
      this.contentItem = new StreamContentItem
        ( repository.getContentName(), this, in, repository.getOutputStream() );
    }

    if ( contentItem == null ) {
      throw new ContentIOException( "No such item" );
    }
    if ( contentItem.getName().equals( name ) ) {
      return contentItem;
    }
    throw new ContentIOException( "No such item" );
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
    final WrappedInputStream in = repository.getInputStream();
    final WrappedOutputStream outputStream = repository.getOutputStream();
    if ( in != null && contentItem == null ) {
      this.contentItem = new StreamContentItem( repository.getContentName(), this, in, outputStream );
    }

    if ( contentItem == null && outputStream != null ) {
      contentItem = new StreamContentItem( name, this, in, outputStream );
      return contentItem;
    }
    throw new ContentCreationException
      ( "Failed to create the item. Item already exists or the repository is read-only" );
  }

  /**
   * This method always throws an exception, as stream-repositories cannot create sub-locations.
   *
   * @param name the name.
   * @return nothing.
   * @throws ContentCreationException always, as stream-repositories cannot create sub-locations.
   */
  public ContentLocation createLocation( final String name )
    throws ContentCreationException {
    throw new ContentCreationException( "A stream repository never creates sub-locations" );
  }

  /**
   * Checks, whether an content entity with the given name exists in this content location.
   *
   * @param name the name of the new entity.
   * @return true, if an entity exists with this name, false otherwise.
   */
  public boolean exists( final String name ) {
    if ( contentItem != null ) {
      return contentItem.getName().equals( name );
    }

    final WrappedInputStream in = repository.getInputStream();
    if ( in != null ) {
      // if we are in input mode, the content name must not be null.
      return repository.getContentName().equals( name );
    }

    return false;
  }

  /**
   * Returns the generic name of this location.
   *
   * @return the name.
   */
  public String getName() {
    return "root";
  }

  /**
   * Returns a unique identifier. This can be canonical filename or a database key. It must be guaranteed that within
   * the same repository the key will be unique.
   *
   * @return the unique content ID.
   */
  public Object getContentId() {
    return getName();
  }

  /**
   * Stream-Repositories do not support attributes.
   *
   * @param domain the attribute domain.
   * @param key    the name of the attribute.
   * @return always null.
   */
  public Object getAttribute( final String domain, final String key ) {
    return null;
  }

  /**
   * Stream-Repositories do not support attributes.
   *
   * @param domain the attribute domain.
   * @param key    the attribute name
   * @param value  the new attribute value.
   * @return always false.
   */
  public boolean setAttribute( final String domain, final String key, final Object value ) {
    return false;
  }

  /**
   * Returns a reference to the parent location. If this entity represents the root directory, this method will return
   * null.
   *
   * @return the parent or null, if this is the root-directory.
   */
  public ContentLocation getParent() {
    return null;
  }

  /**
   * Returns the current repository, to which tis entity belongs.
   *
   * @return the repository.
   */
  public Repository getRepository() {
    return repository;
  }

  /**
   * Stream-repositories do not support the deletion of entries.
   *
   * @return always false.
   */
  public boolean delete() {
    return false;
  }
}
