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


package org.pentaho.reporting.libraries.repository;

/**
 * This represents a container in the repository. If the repository is a filesystem, this will be a directory.
 *
 * @author Thomas Morgner
 */
public interface ContentLocation extends ContentEntity {
  /**
   * Returns all content entities stored in this content-location.
   *
   * @return the content entities for this location.
   * @throws ContentIOException if an repository error occured.
   */
  public ContentEntity[] listContents() throws ContentIOException;

  /**
   * Returns the content entity with the given name. If the entity does not exist, an Exception will be raised.
   *
   * @param name the name of the entity to be retrieved.
   * @return the content entity for this name, never null.
   * @throws ContentIOException if an repository error occured.
   */
  public ContentEntity getEntry( String name ) throws ContentIOException;

  /**
   * Creates a new data item in the current location. This method must never return null. This method will fail if an
   * entity with the same name exists in this location.
   *
   * @param name the name of the new entity.
   * @return the newly created entity, never null.
   * @throws ContentCreationException if the item could not be created.
   */
  public ContentItem createItem( String name ) throws ContentCreationException;

  /**
   * Creates a new content location in the current location. This method must never return null. This method will fail
   * if an entity with the same name exists in this location.
   *
   * @param name the name of the new entity.
   * @return the newly created entity, never null.
   * @throws ContentCreationException if the item could not be created.
   */
  public ContentLocation createLocation( String name ) throws ContentCreationException;

  /**
   * Checks, whether an content entity with the given name exists in this content location.
   *
   * @param name the name of the new entity.
   * @return true, if an entity exists with this name, false otherwise.
   */
  public boolean exists( final String name );
}
