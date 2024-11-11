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


package org.pentaho.reporting.engine.classic.core.modules.parser.base;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.resourceloader.CompoundResource;
import org.pentaho.reporting.libraries.resourceloader.DependencyCollector;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;

/**
 * A resource implementation that tries to clone the provided parse-result so that the cached object is shielded from
 * later modifications.
 *
 * @author Thomas Morgner
 */
public class ReportResource extends CompoundResource {
  private boolean cloneable;

  public ReportResource( final ResourceKey source, final DependencyCollector dependencies, final Object product,
      final Class targetType, final boolean clone ) {
    super( source, dependencies, product, targetType );
    if ( product instanceof Cloneable ) {
      cloneable = clone;
    }

  }

  public boolean isTemporaryResult() {
    return cloneable == false;
  }

  public Object getResource() throws ResourceException {
    try {
      final Object resource = super.getResource();
      if ( cloneable ) {
        return ObjectUtilities.clone( resource );
      }
      return resource;
    } catch ( CloneNotSupportedException e ) {

      throw new ResourceException( "Unable to retrieve the resource.", e );
    }
  }
}
