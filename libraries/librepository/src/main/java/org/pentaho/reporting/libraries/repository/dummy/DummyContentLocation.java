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

package org.pentaho.reporting.libraries.repository.dummy;

import org.pentaho.reporting.libraries.repository.ContentCreationException;
import org.pentaho.reporting.libraries.repository.ContentEntity;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.repository.ContentItem;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.Repository;
import org.pentaho.reporting.libraries.repository.RepositoryUtilities;

import java.io.Serializable;

/**
 * A dummy content location holds references to all dummy items. It does allow to create any items, but always reports
 * itself as empty location. This implementation only serves as data-sink in case the generated content is not needed
 * anywhere.
 *
 * @author Thomas Morgner
 */
public class DummyContentLocation implements ContentLocation, Serializable {
  private String name;
  private ContentLocation parent;
  private Repository repository;
  private static final ContentEntity[] EMPTY_CONTENT_ENTITY = new ContentEntity[ 0 ];

  /**
   * Creates a new DummyContentLocation with the given parent and name. The location will inherit the repository from
   * its parent.
   *
   * @param parent the parent location.
   * @param name   the name of this location.
   */
  public DummyContentLocation( final ContentLocation parent, final String name ) {
    if ( parent == null ) {
      throw new NullPointerException( "Parent must not be null" );
    }
    this.repository = parent.getRepository();
    this.parent = parent;
    this.name = name;
  }

  /**
   * Creates a new root DummyContentLocation with the given repository and name.
   *
   * @param repository the repository.
   * @param name       the name of this location.
   */
  public DummyContentLocation( final Repository repository, final String name ) {
    this.repository = repository;
    this.name = name;
  }

  /**
   * Returns all content entities stored in this content-location. This always returns an empty array.
   *
   * @return the content entities for this location, an empty array.
   * @throws ContentIOException if an repository error occured.
   */
  public ContentEntity[] listContents() throws ContentIOException {
    return EMPTY_CONTENT_ENTITY;
  }

  /**
   * Returns the content entity with the given name. This always throws the ContentIOException, as this implementation
   * claims to not know any of its childs.
   *
   * @param name the name of the entity to be retrieved.
   * @return the content entity for this name, never null.
   * @throws ContentIOException if an repository error occured.
   */
  public ContentEntity getEntry( final String name ) throws ContentIOException {
    throw new ContentIOException();
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
    return new DummyContentItem( this, name );
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
    return new DummyContentLocation( this, name );
  }

  /**
   * A dummy location does not have children, therefore this method always returns false.
   *
   * @param name the name of the item.
   * @return false.
   */
  public boolean exists( final String name ) {
    return false;
  }

  /**
   * Returns the name of the entry.
   *
   * @return the name, never null.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the full pathname of the location.
   *
   * @return the full pathname.
   */
  public Object getContentId() {
    return RepositoryUtilities.buildName( this, "/" );
  }

  /**
   * Dummy locations do not have attributes, therefore this method always returns null.
   *
   * @param domain the attribute domain.
   * @param key    the name of the attribute.
   * @return the value or null, if the content-entity does not have a value for this attribute.
   */
  public Object getAttribute( final String domain, final String key ) {
    return null;
  }

  /**
   * Dummy locations do not allow to set attributes, therefore this method always returns false.
   *
   * @param domain the attribute domain.
   * @param key    the attribute name
   * @param value  the new attribute value.
   * @return false.
   */
  public boolean setAttribute( final String domain, final String key, final Object value ) {
    return false;
  }

  /**
   * Returns the parent, if there is any.
   *
   * @return the parent.
   */
  public ContentLocation getParent() {
    return parent;
  }

  /**
   * Returns the parent repository for this location.
   *
   * @return the repository.
   */
  public Repository getRepository() {
    return repository;
  }

  /**
   * A dummy location does not have content and therefore does not support the delete command.
   *
   * @return always false.
   */
  public boolean delete() {
    return false;
  }
}
