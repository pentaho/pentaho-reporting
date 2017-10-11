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
