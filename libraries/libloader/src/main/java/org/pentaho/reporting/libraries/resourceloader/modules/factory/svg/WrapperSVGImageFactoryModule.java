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

package org.pentaho.reporting.libraries.resourceloader.modules.factory.svg;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.factory.FactoryModule;

public class WrapperSVGImageFactoryModule implements FactoryModule {
  private FactoryModule parent;

  public WrapperSVGImageFactoryModule() {
    parent = ObjectUtilities.loadAndInstantiate
      ( "org.pentaho.reporting.libraries.resourceloader.modules.factory.svg.SVGImageFactoryModule",
        WrapperSVGImageFactoryModule.class, FactoryModule.class );
  }

  public int canHandleResource( final ResourceManager caller, final ResourceData data )
    throws ResourceCreationException, ResourceLoadingException {
    if ( parent == null ) {
      return FactoryModule.REJECTED;
    }
    return parent.canHandleResource( caller, data );
  }

  public int getHeaderFingerprintSize() {
    if ( parent == null ) {
      return 0;
    }
    return parent.getHeaderFingerprintSize();
  }

  public Resource create( final ResourceManager caller, final ResourceData data, final ResourceKey context )
    throws ResourceCreationException, ResourceLoadingException {
    if ( parent == null ) {
      throw new ResourceCreationException( "Cannot create resource: Batik libraries are not available." );
    }
    return parent.create( caller, data, context );
  }
}
