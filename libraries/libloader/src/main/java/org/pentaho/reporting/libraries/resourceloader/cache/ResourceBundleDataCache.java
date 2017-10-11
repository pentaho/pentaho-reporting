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
 * Creation-Date: 06.04.2006, 09:30:28
 *
 * @author Thomas Morgner
 */
public interface ResourceBundleDataCache {
  /**
   * Retrieves the given data from the cache.
   *
   * @param key the resource key for the data.
   * @return the cached entry or null, if the entry is no longer in the cache.
   */
  public ResourceBundleDataCacheEntry get( ResourceKey key );

  /**
   * Stores the given data on the cache. The data is registered by its primary key. The cache has to store the current
   * version of the data.
   *
   * @param caller the calling resource manager.
   * @param data   the data to be stored in the cache
   * @return the resource data object, possibly wrapped by a cache-specific implementation.
   * @throws ResourceLoadingException if an error prevents the data from being cached.
   */
  public ResourceBundleData put( ResourceManager caller,
                                 ResourceBundleData data ) throws ResourceLoadingException;

  public void remove( ResourceBundleData data );

  /**
   * Remove all cached entries. This should be called after the cache has become invalid or after it has been removed
   * from a resource manager.
   */
  public void clear();

  public void shutdown();
}
