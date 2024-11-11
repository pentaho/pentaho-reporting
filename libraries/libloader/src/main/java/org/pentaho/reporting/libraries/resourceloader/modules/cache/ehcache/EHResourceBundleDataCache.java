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


package org.pentaho.reporting.libraries.resourceloader.modules.cache.ehcache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.resourceloader.ResourceBundleData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.cache.CachingResourceBundleData;
import org.pentaho.reporting.libraries.resourceloader.cache.DefaultResourceBundleDataCacheEntry;
import org.pentaho.reporting.libraries.resourceloader.cache.ResourceBundleDataCache;
import org.pentaho.reporting.libraries.resourceloader.cache.ResourceBundleDataCacheEntry;

public class EHResourceBundleDataCache implements ResourceBundleDataCache {
  private static final Log logger = LogFactory.getLog( EHResourceDataCache.class );
  private Cache dataCache;

  public EHResourceBundleDataCache( final Cache dataCache ) {
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
  public ResourceBundleDataCacheEntry get( final ResourceKey key ) {
    if ( key == null ) {
      throw new NullPointerException();
    }

    try {
      final Element element = dataCache.get( (Object) key );
      if ( element != null ) {
        if ( EHCacheModule.CACHE_MONITOR.isDebugEnabled() ) {
          EHCacheModule.CACHE_MONITOR.debug( "Bund Cache Hit  " + key );
        }
        return (ResourceBundleDataCacheEntry) element.getObjectValue();
      } else {
        if ( EHCacheModule.CACHE_MONITOR.isDebugEnabled() ) {
          EHCacheModule.CACHE_MONITOR.debug( "Bund Cache Miss " + key );
        }
        return null;
      }
    } catch ( CacheException e ) {
      logger.debug( "Failed to query cache", e );
      return null;
    }
  }

  public ResourceBundleData put( final ResourceManager caller, final ResourceBundleData data )
    throws ResourceLoadingException {
    if ( caller == null ) {
      throw new NullPointerException();
    }
    if ( data == null ) {
      throw new NullPointerException();
    }

    final ResourceBundleData cdata = CachingResourceBundleData.createCached( data );
    final Object keyObject = data.getBundleKey();
    final Object valueObject = new DefaultResourceBundleDataCacheEntry( cdata, caller );
    final Element element = new Element( keyObject, valueObject );
    if ( EHCacheModule.CACHE_MONITOR.isDebugEnabled() ) {
      EHCacheModule.CACHE_MONITOR.debug( "Storing Bundle " + keyObject );
    }
    dataCache.put( element );
    return cdata;
  }

  public void remove( final ResourceBundleData data ) {
    if ( data == null ) {
      throw new NullPointerException();
    }

    dataCache.remove( (Object) data.getBundleKey() );
  }

  /**
   * Remove all cached entries. This should be called after the cache has become invalid or after it has been removed
   * from a resource manager.
   */
  public void clear() {
    try {
      dataCache.removeAll();
    } catch ( Exception e ) {
      logger.debug( "Clearing cache failed", e );
      // ignore it ..
    }
  }

  public void shutdown() {
    try {
      dataCache.getCacheManager().shutdown();
    } catch ( Exception e ) {
      logger.debug( "Failed to shut-down cache", e );
      // ignore it ..
    }
  }
}
