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

package org.pentaho.reporting.libraries.resourceloader.cache;

import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * Creation-Date: 06.04.2006, 09:30:28
 *
 * @author Thomas Morgner
 */
public interface ResourceDataCache {
  /**
   * Retrieves the given data from the cache.
   *
   * @param key the resource key for the data.
   */
  public ResourceDataCacheEntry get( ResourceKey key );

  /**
   * Stores the given data on the cache. The data is registered by its primary key. The cache has to store the current
   * version of the data.
   *
   * @param data the data to be stored in the cache
   * @return the resource data object, possibly wrapped by a cache-specific implementation.
   */
  public ResourceData put( ResourceManager caller,
                           ResourceData data ) throws ResourceLoadingException;

  public void remove( ResourceData data );

  /**
   * Remove all cached entries. This should be called after the cache has become invalid or after it has been removed
   * from a resource manager.
   */
  public void clear();

  public void shutdown();
}
