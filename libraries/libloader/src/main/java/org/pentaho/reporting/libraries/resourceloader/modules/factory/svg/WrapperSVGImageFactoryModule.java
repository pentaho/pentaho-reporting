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
