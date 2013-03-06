package org.pentaho.reporting.engine.classic.core.cache;

import javax.swing.table.TableModel;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;

/**
 * The simplest of all caches systems. A plain map holding all elements.
 *
 * @author Thomas Morgner.
 */
public class EhCacheDataCache implements DataCache
{
  private class GlobalCacheManager implements DataCacheManager
  {
    private GlobalCacheManager()
    {
    }

    public void clearAll()
    {
      synchronized (EhCacheDataCache.this)
      {
        if (cache != null)
        {
          cache.removeAll();
        }
      }
    }

    public void shutdown()
    {
      synchronized (EhCacheDataCache.this)
      {
        if (cache != null)
        {
          cache.removeAll();
        }
        if (ClassicEngineBoot.getInstance().getExtendedConfig().getBoolProperty
            ("org.pentaho.reporting.engine.classic.core.cache.EhCacheDataCache.UseGlobalCacheManager"))
        {
          if (manager != null)
          {
            manager.shutdown();
          }
        }
        cache = null;
        manager = null;

        DataCacheFactory.notifyCacheShutdown(EhCacheDataCache.this);
      }
    }
  }

  private static final String CACHE_NAME = "report-dataset-cache";
  private DataCacheManager cacheManager;
  private int maximumRows;
  private volatile CacheManager manager;
  private volatile Cache cache;

  public EhCacheDataCache()
  {
    this(ClassicEngineBoot.getInstance().getExtendedConfig().getIntProperty
        ("org.pentaho.reporting.engine.classic.core.cache.EhCacheDataCache.CachableRowLimit"));
  }

  public EhCacheDataCache(final int maximumRows)
  {
    this.maximumRows = maximumRows;
    this.cacheManager = new GlobalCacheManager();

    initializeCacheManager();
    initialize();
  }

  private void initializeCacheManager()
  {
    if (ClassicEngineBoot.getInstance().getExtendedConfig().getBoolProperty
        ("org.pentaho.reporting.engine.classic.core.cache.EhCacheDataCache.UseGlobalCacheManager"))
    {
      manager = CacheManager.getInstance();
    }
    else if (manager == null)
    {
      manager = createCacheManager();
    }
  }

  private synchronized void initialize()
  {
    if (manager != null)
    {
      if (manager.getStatus() != Status.STATUS_ALIVE)
      {
        initializeCacheManager();
      }
    }

    if (cache != null)
    {
      if (cache.getStatus() == Status.STATUS_ALIVE)
      {
        return;
      }
    }

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

  protected CacheManager createCacheManager()
  {
    return new CacheManager();
  }

  public int getMaximumRows()
  {
    return maximumRows;
  }

  public TableModel get(final DataCacheKey key)
  {
    final Cache cache = this.cache;
    synchronized (this)
    {
      if (cache == null)
      {
        return null;
      }

      if (cache.getStatus() != Status.STATUS_ALIVE)
      {
        this.cache = null;
        return null;
      }
    }

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

    final Cache cache;
    synchronized (this)
    {
      initialize();
      cache = this.cache;
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
