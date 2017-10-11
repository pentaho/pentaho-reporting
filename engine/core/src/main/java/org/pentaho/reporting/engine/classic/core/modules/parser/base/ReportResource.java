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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

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
