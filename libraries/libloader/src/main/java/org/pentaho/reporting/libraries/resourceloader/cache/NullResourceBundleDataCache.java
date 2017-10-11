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

package org.pentaho.reporting.libraries.resourceloader.cache;

import org.pentaho.reporting.libraries.resourceloader.ResourceBundleData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * Disables caching. It simply returns null on every request and ignores the put requests. You certainly want to use one
 * of the other cache providers in real world applications.
 *
 * @author Thomas Morgner
 */
public class NullResourceBundleDataCache implements ResourceBundleDataCache {
  private Object lastEntry;

  public NullResourceBundleDataCache() {
  }

  public ResourceBundleData put( final ResourceManager caller, final ResourceBundleData data )
    throws ResourceLoadingException {
    final ResourceBundleData retval = CachingResourceBundleData.createCached( data );
    lastEntry = new DefaultResourceBundleDataCacheEntry( retval, caller );
    return retval;
  }

  public ResourceBundleDataCacheEntry get( final ResourceKey key ) {
    if ( lastEntry != null ) {
      final ResourceBundleDataCacheEntry entry = (ResourceBundleDataCacheEntry) lastEntry;
      if ( key.equals( entry.getData().getBundleKey() ) ) {
        return entry;
      }
      lastEntry = null;
    }
    return null;
  }

  public void remove( final ResourceBundleData data ) {
  }

  public void clear() {
    lastEntry = null;
  }

  public void shutdown() {
    lastEntry = null;
  }
}
