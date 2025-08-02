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


package org.pentaho.reporting.engine.classic.core;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.cache.DataCacheKey;
import org.pentaho.reporting.engine.classic.core.cache.EhCacheDataCache;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.swing.table.DefaultTableModel;
import java.net.URISyntaxException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class CacheKillingIT {
  @Test
  public void testKillCacheAndSurvive() throws URISyntaxException {
    EhCacheDataCache dataCache = new EhCacheDataCache();
    DataCacheKey key = new DataCacheKey();
    key.addAttribute( "Test", "test" );
    dataCache.put( key, new DefaultTableModel() );

    final CacheManager cacheManager = Caching.getCachingProvider().getCacheManager();
    // Note: EHCacheProvider will dynamically create these
    // caches if they don't exist.
    for ( String cacheName : cacheManager.getCacheNames() ) {
      Cache<?, ?> cache = cacheManager.getCache( cacheName );
      if ( cache != null ) {
        cache.clear();
        cacheManager.destroyCache( cacheName );
      }
    }

    assertNull( cacheManager.getCache( "libloader-bundles" ) );
    assertNull( cacheManager.getCache( "libloader-data" ) );
    assertNull( cacheManager.getCache( "libloader-factory" ) );
    assertNull( cacheManager.getCache( "report-dataset-cache" ) );

    cacheManager.close();

    assertNull( dataCache.get( key ) );
    dataCache.put( key, new DefaultTableModel() );
    assertNotNull( dataCache.get( key ) );

  }

  @Test
  public void testKillCacheWithoutShutdownAndSurvive() throws URISyntaxException {
    EhCacheDataCache dataCache = new EhCacheDataCache();
    DataCacheKey key = new DataCacheKey();
    key.addAttribute( "Test", "test" );
    dataCache.put( key, new DefaultTableModel() );

    final CacheManager cacheManager = Caching.getCachingProvider().getCacheManager();
    // Note: EHCacheProvider will dynamically create these
    // caches if they don't exist.
    for ( String cacheName : cacheManager.getCacheNames() ) {
      Cache<?, ?> cache = cacheManager.getCache( cacheName );
      if ( cache != null ) {
        cache.clear();
        cacheManager.destroyCache( cacheName );
      }
    }

    assertNull( cacheManager.getCache( "libloader-bundles" ) );
    assertNull( cacheManager.getCache( "libloader-data" ) );
    assertNull( cacheManager.getCache( "libloader-factory" ) );
    assertNull( cacheManager.getCache( "report-dataset-cache" ) );

    assertNull( dataCache.get( key ) );
    dataCache.put( key, new DefaultTableModel() );
    assertNotNull( dataCache.get( key ) );

  }
}
