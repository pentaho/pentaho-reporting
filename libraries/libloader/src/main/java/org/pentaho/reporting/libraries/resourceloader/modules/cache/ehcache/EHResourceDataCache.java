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

package org.pentaho.reporting.libraries.resourceloader.modules.cache.ehcache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;
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
      final Element element = dataCache.get( (Object) key );
      if ( element != null ) {
        if ( EHCacheModule.CACHE_MONITOR.isDebugEnabled() ) {
          EHCacheModule.CACHE_MONITOR.debug( "Data Cache Hit  " + key );
        }
        return (ResourceDataCacheEntry) element.getObjectValue();
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
    dataCache.put( new Element( keyObject, dataCacheEntry ) );
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
      dataCache.getCacheManager().shutdown();
    } catch ( Exception e ) {
      logger.debug( "Failed to shut-down cache", e );
      // ignore it ..
    }
  }
}
