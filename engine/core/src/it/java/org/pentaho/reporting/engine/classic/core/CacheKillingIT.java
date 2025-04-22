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

import junit.framework.TestCase;
import net.sf.ehcache.CacheManager;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.cache.DataCacheKey;
import org.pentaho.reporting.engine.classic.core.cache.EhCacheDataCache;

import javax.swing.table.DefaultTableModel;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class CacheKillingIT {
  @Test
  public void testKillCacheAndSurvive() {
    EhCacheDataCache dataCache = new EhCacheDataCache();
    DataCacheKey key = new DataCacheKey();
    key.addAttribute( "Test", "test" );
    dataCache.put( key, new DefaultTableModel() );

    final CacheManager cacheManager = CacheManager.getInstance();
    // Note: EHCacheProvider will dynamically create these
    // caches if they don't exist.
    cacheManager.clearAll();
    cacheManager.removalAll();

    assertFalse( cacheManager.cacheExists( "libloader-bundles" ) );
    assertFalse( cacheManager.cacheExists( "libloader-data" ) );
    assertFalse( cacheManager.cacheExists( "libloader-factory" ) );
    assertFalse( cacheManager.cacheExists( "report-dataset-cache" ) );

    cacheManager.shutdown();

    assertNull( dataCache.get( key ) );
    dataCache.put( key, new DefaultTableModel() );
    assertNotNull( dataCache.get( key ) );

  }

  @Test
  public void testKillCacheWithoutShutdownAndSurvive() {
    EhCacheDataCache dataCache = new EhCacheDataCache();
    DataCacheKey key = new DataCacheKey();
    key.addAttribute( "Test", "test" );
    dataCache.put( key, new DefaultTableModel() );

    final CacheManager cacheManager = CacheManager.getInstance();
    // Note: EHCacheProvider will dynamically create these
    // caches if they don't exist.
    cacheManager.clearAll();
    cacheManager.removalAll();

    assertFalse( cacheManager.cacheExists( "libloader-bundles" ) );
    assertFalse( cacheManager.cacheExists( "libloader-data" ) );
    assertFalse( cacheManager.cacheExists( "libloader-factory" ) );
    assertFalse( cacheManager.cacheExists( "report-dataset-cache" ) );

    assertNull( dataCache.get( key ) );
    dataCache.put( key, new DefaultTableModel() );
    assertNotNull( dataCache.get( key ) );

  }
}
