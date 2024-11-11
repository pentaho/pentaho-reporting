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


package org.pentaho.reporting.libraries.repository.dummy;

import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.DefaultMimeRegistry;
import org.pentaho.reporting.libraries.repository.MimeRegistry;
import org.pentaho.reporting.libraries.repository.Repository;

import java.io.Serializable;

/**
 * A dummy repositor is a empty repository that swallows all content fed into it.
 *
 * @author Thomas Morgner
 */
public class DummyRepository implements Repository, Serializable {
  private DummyContentLocation location;
  private MimeRegistry mimeRegistry;

  /**
   * Creates a new dummy repository.
   */
  public DummyRepository() {
    location = new DummyContentLocation( this, "" );
    mimeRegistry = new DefaultMimeRegistry();
  }

  /**
   * Returns the repositories root directory entry.
   *
   * @return the root directory.
   */
  public ContentLocation getRoot() {
    return location;
  }

  /**
   * Returns the repositories MimeRegistry, which is used return basic content-type information about the items stored
   * in this repository.
   *
   * @return the mime registry.
   * @see MimeRegistry
   */
  public MimeRegistry getMimeRegistry() {
    return mimeRegistry;
  }
}
