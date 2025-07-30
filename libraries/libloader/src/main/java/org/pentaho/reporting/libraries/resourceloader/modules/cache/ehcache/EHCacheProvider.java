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
import org.pentaho.reporting.libraries.resourceloader.cache.ResourceBundleDataCache;
import org.pentaho.reporting.libraries.resourceloader.cache.ResourceBundleDataCacheProvider;
import org.pentaho.reporting.libraries.resourceloader.cache.ResourceDataCache;
import org.pentaho.reporting.libraries.resourceloader.cache.ResourceDataCacheProvider;
import org.pentaho.reporting.libraries.resourceloader.cache.ResourceFactoryCache;
import org.pentaho.reporting.libraries.resourceloader.cache.ResourceFactoryCacheProvider;
import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;

public class EHCacheProvider implements ResourceDataCacheProvider,
  ResourceFactoryCacheProvider, ResourceBundleDataCacheProvider {
  private static CacheManager cacheManager;

  private static final String DATA_CACHE_NAME = "libloader-data";
  private static final String BUNDLES_CACHE_NAME = "libloader-bundles";
  private static final String FACTORY_CACHE_NAME = "libloader-factory";

  private static final Log logger = LogFactory.getLog( EHCacheProvider.class );

  public static synchronized CacheManager getCacheManager() throws CacheException {
    if ( cacheManager == null ) {
      cacheManager = Caching.getCachingProvider().getCacheManager();
    }
    return cacheManager;
  }

  public EHCacheProvider() {
  }

  public ResourceDataCache createDataCache() {
    try {
      final CacheManager manager = getCacheManager();
      synchronized( manager ) {
        Cache<Object,Object> libloaderCache = manager.getCache( DATA_CACHE_NAME );
        if ( libloaderCache == null ) {
          MutableConfiguration<Object,Object> configuration = new MutableConfiguration<>().setStoreByValue( false );
          libloaderCache = manager.createCache( DATA_CACHE_NAME, configuration );
          return new EHResourceDataCache( libloaderCache );
        } else {
          return new EHResourceDataCache( manager.getCache( DATA_CACHE_NAME ) );
        }
      }
    } catch ( CacheException e ) {
      logger.debug( "Failed to create EHCache libloader-data cache", e );
      return null;
    }
  }

  public ResourceBundleDataCache createBundleDataCache() {
    try {
      final CacheManager manager = getCacheManager();
      synchronized( manager ) {
        Cache<Object,Object> libloaderCache = manager.getCache( BUNDLES_CACHE_NAME );
        if ( libloaderCache == null ) {
          MutableConfiguration<Object,Object> configuration = new MutableConfiguration<>().setStoreByValue( false );
          libloaderCache = manager.createCache( BUNDLES_CACHE_NAME, configuration );
          return new EHResourceBundleDataCache( libloaderCache );
        } else {
          return new EHResourceBundleDataCache( manager.getCache( BUNDLES_CACHE_NAME ) );
        }
      }
    } catch ( CacheException e ) {
      logger.debug( "Failed to create EHCache libloader-bundles cache", e );
      return null;
    }
  }

  public ResourceFactoryCache createFactoryCache() {
    try {
      final CacheManager manager = getCacheManager();
      synchronized( manager ) {
        Cache<Object,Object> libloaderCache = manager.getCache( FACTORY_CACHE_NAME );
        if ( libloaderCache == null ) {
          MutableConfiguration<Object,Object> configuration = new MutableConfiguration<>().setStoreByValue( false );
          libloaderCache = manager.createCache( FACTORY_CACHE_NAME, configuration );
          return new EHResourceFactoryCache( libloaderCache );
        } else {
          return new EHResourceFactoryCache( manager.getCache( FACTORY_CACHE_NAME ) );
        }
      }
    } catch ( CacheException e ) {
      logger.debug( "Failed to create EHCache libloader-factory cache", e );
      return null;
    }
  }
}
