/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
