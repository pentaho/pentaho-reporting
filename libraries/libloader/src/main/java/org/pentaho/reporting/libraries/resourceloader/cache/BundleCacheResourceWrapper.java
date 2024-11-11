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


package org.pentaho.reporting.libraries.resourceloader.cache;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;

public class BundleCacheResourceWrapper implements Resource {
  private Resource parent;
  private ResourceKey outsideKey;

  public BundleCacheResourceWrapper( final Resource parent,
                                     final ResourceKey outsideKey ) {
    this.parent = parent;
    this.outsideKey = outsideKey;
  }

  public Object getResource()
    throws ResourceException {
    return parent.getResource();
  }

  public Class getTargetType() {
    return parent.getTargetType();
  }

  public long getVersion( final ResourceKey key ) {
    if ( ObjectUtilities.equal( key, outsideKey ) ) {
      return parent.getVersion( parent.getSource() );
    }
    return parent.getVersion( key );
  }

  public ResourceKey[] getDependencies() {
    final ResourceKey[] resourceKeys = parent.getDependencies();
    final ResourceKey[] target = new ResourceKey[ resourceKeys.length + 1 ];
    target[ 0 ] = parent.getSource();
    System.arraycopy( resourceKeys, 0, target, 1, resourceKeys.length );
    return target;
  }

  public ResourceKey getSource() {
    return outsideKey;
  }

  public boolean isTemporaryResult() {
    return parent.isTemporaryResult();
  }
}
