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


package org.pentaho.reporting.libraries.resourceloader.modules.cache.ehcache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.cache.CachingResourceData;
import org.pentaho.reporting.libraries.resourceloader.cache.DefaultResourceDataCacheEntry;
import org.pentaho.reporting.libraries.resourceloader.cache.ResourceDataCache;
import org.pentaho.reporting.libraries.resourceloader.cache.ResourceDataCacheEntry;
import javax.cache.Cache;
import javax.cache.CacheException;

public class EHResourceDataCache implements ResourceDataCache {
  private Cache dataCache;
  private static final Log logger = LogFactory.getLog( EHResourceDataCache.class );

  public EHResourceDataCache( final Cache dataCache ) {
    if ( dataCache == null ) {
      throw new NullPointerException();
    }
    this.dataCache = dataCache;
  }

  /**
   * Retrieves the given data from the cache.
   *
   * @param key the resource key for the data.
   */
  public ResourceDataCacheEntry get( final ResourceKey key ) {
    if ( key == null ) {
      throw new NullPointerException();
    }

    try {
      final Object element = dataCache.get( key );
      if ( element != null ) {
        if ( EHCacheModule.CACHE_MONITOR.isDebugEnabled() ) {
          EHCacheModule.CACHE_MONITOR.debug( "Data Cache Hit  " + key );
        }
        return (ResourceDataCacheEntry) element;
      }
      if ( EHCacheModule.CACHE_MONITOR.isDebugEnabled() ) {
        EHCacheModule.CACHE_MONITOR.debug( "Data Cache Miss " + key );
      }
      return null;
    } catch ( CacheException e ) {
      logger.debug( "Failed to query cache", e );
      return null;
    }
  }

  /**
   * Stores the given data on the cache. The data is registered by its primary key. The cache has to store the current
   * version of the data.
   *
   * @param data the data to be stored in the cache
   * @return the resource data object, possibly wrapped by a cache-specific implementation.
   */
  public ResourceData put( final ResourceManager caller, final ResourceData data ) throws ResourceLoadingException {
    if ( data == null ) {
      throw new NullPointerException();
    }
    if ( caller == null ) {
      throw new NullPointerException();
    }

    final ResourceData cdata = CachingResourceData.createCached( data );
    final Object keyObject = data.getKey();
    final Object dataCacheEntry = new DefaultResourceDataCacheEntry( cdata, caller );
    dataCache.put( keyObject, dataCacheEntry );
    return cdata;
  }

  public void remove( final ResourceData data ) {
    if ( data == null ) {
      throw new NullPointerException();
    }

    dataCache.remove( (Object) data.getKey() );
  }

  /**
   * Remove all cached entries. This should be called after the cache has become invalid or after it has been removed
   * from a resource manager.
   */
  public void clear() {
    try {
      dataCache.removeAll();
    } catch ( Exception e ) {
      // ignore it ..
      logger.debug( "Clearing cache failed", e );
    }
  }

  public void shutdown() {
    try {
      dataCache.getCacheManager().close();
    } catch ( Exception e ) {
      logger.debug( "Failed to shut-down cache", e );
      // ignore it ..
    }
  }
}
