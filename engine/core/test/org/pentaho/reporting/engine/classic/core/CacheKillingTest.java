package org.pentaho.reporting.engine.classic.core;

import javax.swing.table.DefaultTableModel;

import junit.framework.TestCase;
import net.sf.ehcache.CacheManager;
import org.pentaho.reporting.engine.classic.core.cache.DataCacheKey;
import org.pentaho.reporting.engine.classic.core.cache.EhCacheDataCache;

public class CacheKillingTest extends TestCase
{
  public void testKillCacheAndSurvive()
  {
    EhCacheDataCache dataCache = new EhCacheDataCache();
    DataCacheKey key = new DataCacheKey();
    key.addAttribute("Test", "test");
    dataCache.put(key, new DefaultTableModel());

    final CacheManager cacheManager = CacheManager.getInstance();
    // Note: EHCacheProvider will dynamically create these
    // caches if they don't exist.
    cacheManager.clearAll();
    cacheManager.removalAll();

    assertFalse(cacheManager.cacheExists("libloader-bundles"));
    assertFalse(cacheManager.cacheExists("libloader-data"));
    assertFalse(cacheManager.cacheExists("libloader-factory"));
    assertFalse(cacheManager.cacheExists("report-dataset-cache"));

    cacheManager.shutdown();

    assertNull(dataCache.get(key));
    dataCache.put(key, new DefaultTableModel());
    assertNotNull(dataCache.get(key));

  }

  public void testKillCacheWithoutShutdownAndSurvive()
  {
    EhCacheDataCache dataCache = new EhCacheDataCache();
    DataCacheKey key = new DataCacheKey();
    key.addAttribute("Test", "test");
    dataCache.put(key, new DefaultTableModel());

    final CacheManager cacheManager = CacheManager.getInstance();
    // Note: EHCacheProvider will dynamically create these
    // caches if they don't exist.
    cacheManager.clearAll();
    cacheManager.removalAll();

    assertFalse(cacheManager.cacheExists("libloader-bundles"));
    assertFalse(cacheManager.cacheExists("libloader-data"));
    assertFalse(cacheManager.cacheExists("libloader-factory"));
    assertFalse(cacheManager.cacheExists("report-dataset-cache"));

    assertNull(dataCache.get(key));
    dataCache.put(key, new DefaultTableModel());
    assertNotNull(dataCache.get(key));

  }
}
