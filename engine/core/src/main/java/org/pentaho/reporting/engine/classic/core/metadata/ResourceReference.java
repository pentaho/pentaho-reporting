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


package org.pentaho.reporting.engine.classic.core.metadata;

import org.pentaho.reporting.libraries.resourceloader.ResourceKey;

/**
 * Represents a static resource reference to a file contained in the bundle or in a location that is accessible via the
 * report's resource-manager.
 *
 * @author Thomas Morgner
 */
public class ResourceReference {
  private ResourceKey path;
  private boolean linked;

  public ResourceReference( final ResourceKey path, final boolean linked ) {
    if ( path == null ) {
      throw new NullPointerException();
    }
    this.path = path;
    this.linked = linked;
  }

  public ResourceKey getPath() {
    return path;
  }

  public boolean isLinked() {
    return linked;
  }

  public String toString() {
    return "org.pentaho.reporting.engine.classic.core.metadata.ResourceReference{" + "path=" + path + ", linked="
        + linked + '}';
  }
}
