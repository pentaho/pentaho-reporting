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

package org.pentaho.reporting.libraries.repository;

/**
 * A content entity is the base interface for both data items and directory items.
 *
 * @author Thomas Morgner
 */
public interface ContentEntity {
  /**
   * Returns the name of the entry.
   *
   * @return the name, never null.
   */
  public String getName();

  /**
   * Returns a unique identifier. This can be canonical filename or a database key. It must be guaranteed that within
   * the same repository the key will be unique.
   *
   * @return the unique content ID.
   */
  public Object getContentId();

  /**
   * Returns a attribute value for the given domain (namespace) and attribute-name. Some generic attribute domains and
   * names are defined as constants in the {@link LibRepositoryBoot} class.
   *
   * @param domain the attribute domain.
   * @param key    the name of the attribute.
   * @return the value or null, if the content-entity does not have a value for this attribute.
   */
  public Object getAttribute( String domain, String key );

  /**
   * Updates the attribute value for the given attribute domain and name. If the element is not writable or the
   * attribute could not be updated for any other reason, the method will return false. This method only returns true,
   * if the attribute has been updated successfully.
   *
   * @param domain the attribute domain.
   * @param key    the attribute name
   * @param value  the new attribute value.
   * @return true, if the update was successful, false otherwise.
   */
  public boolean setAttribute( String domain, String key, Object value );

  /**
   * Returns a reference to the parent location. If this entity represents the root directory, this method will return
   * null.
   *
   * @return the parent or null, if this is the root-directory.
   */
  public ContentLocation getParent();

  /**
   * Returns the current repository, to which tis entity belongs.
   *
   * @return the repository.
   */
  public Repository getRepository();

  /**
   * Attempts to delete the entity. After an entity has been deleted, any call to any of the methods of the entity may
   * produce undefined results.
   *
   * @return true, if the entity was deleted and detached from the repository, false otherwise.
   */
  public boolean delete();
}
