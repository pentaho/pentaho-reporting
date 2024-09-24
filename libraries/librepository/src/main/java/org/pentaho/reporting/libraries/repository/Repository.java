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
 * A repository represents a abstract view on a filesystem. It always has a single root-entry and grants access to a
 * repository-specific mime-registry.
 *
 * @author Thomas Morgner
 */
public interface Repository {
  /**
   * Returns the repositories root directory entry.
   *
   * @return the root directory.
   * @throws ContentIOException if an error occurs.
   */
  public ContentLocation getRoot() throws ContentIOException;

  /**
   * Returns the repositories MimeRegistry, which is used return basic content-type information about the items stored
   * in this repository.
   *
   * @return the mime registry.
   * @see MimeRegistry
   */
  public MimeRegistry getMimeRegistry();
}
