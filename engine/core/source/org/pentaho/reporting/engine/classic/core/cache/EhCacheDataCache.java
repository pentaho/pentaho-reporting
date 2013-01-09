package org.pentaho.reporting.engine.classic.core.cache;

import javax.swing.table.TableModel;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;

/**
 * The simplest of all caches systems. A plain map holding all elements.
 *
 * @author Thomas Morgner.
 */
public class EhCacheDataCache implements DataCache
{
  private static final String CACHE_NAME = "report-dataset-cache";

  private class GlobalCacheManager implements DataCacheManager
  {
    private GlobalCacheManager()
    {
    }

    public void clearAll()
    {
      cache.removeAll();
    }

    public void shutdown()
    {
      cache.removeAll();
      if (ClassicEngineBoot.getInstance().getExtendedConfig().getBoolProperty
          ("org.pentaho.reporting.engine.classic.core.cache.EhCacheDataCache.UseGlobalCacheManager"))
      {
        manager.shutdown();
      }
    }
  }


  private DataCacheManager cacheManager;
  private int maximumRows;
  private CacheManager manager;
  private Cache cache;

  public EhCacheDataCache()
  {
    this(ClassicEngineBoot.getInstance().getExtendedConfig().getIntProperty
        ("org.pentaho.reporting.engine.classic.core.cache.EhCacheDataCache.CachableRowLimit"));
  }

  public EhCacheDataCache(final int maximumRows)
  {
    this.maximumRows = maximumRows;

    if (ClassicEngineBoot.getInstance().getExtendedConfig().getBoolProperty
        ("org.pentaho.reporting.engine.classic.core.cache.EhCacheDataCache.UseGlobalCacheManager"))
    {
      manager = CacheManager.getInstance();
    }
    else
    {
      manager = new CacheManager();
    }

    cacheManager = new GlobalCacheManager();
    if (manager.cacheExists(CACHE_NAME) == false)
    {
      cache = new Cache(CACHE_NAME,  // cache name
                        500,         // maxElementsInMemory
                        false,       // overflowToDisk
                        false,       // eternal
                        600,         // timeToLiveSeconds
                        600,         // timeToIdleSeconds
                        false,       // diskPersistent
                        120);        // diskExpiryThreadIntervalSeconds
      manager.addCache(cache);
    }
    else
    {
      cache = manager.getCache(CACHE_NAME);
    }
  }

  public int getMaximumRows()
  {
    return maximumRows;
  }

  public TableModel get(final DataCacheKey key)
  {
    final Element element = cache.get(key);
    if (element == null)
    {
      return null;
    }
    return (TableModel) element.getObjectValue();
  }

  public TableModel put(final DataCacheKey key, final TableModel model)
  {
    if (model.getRowCount() > maximumRows)
    {
      return model;
    }

    // Only copy if safe to do so. Check for whitelist of good column types ..
    if (CachableTableModel.isSafeToCache(model) == false)
    {
      return model;
    }

    final TableModel cacheModel = new CachableTableModel(model);
    cache.put(new Element(key, cacheModel));
    return cacheModel;
  }

  public DataCacheManager getCacheManager()
  {
    return cacheManager;
  }
}
