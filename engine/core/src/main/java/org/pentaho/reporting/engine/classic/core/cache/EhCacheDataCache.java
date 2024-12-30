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


package org.pentaho.reporting.engine.classic.core.cache;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;

import javax.swing.table.TableModel;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * The simplest of all caches systems. A plain map holding all elements.
 *
 * @author Thomas Morgner.
 */
public class EhCacheDataCache implements DataCache {
  private class GlobalCacheManager implements DataCacheManager {
    private GlobalCacheManager() {
    }

    public void clearAll() {
      synchronized ( EhCacheDataCache.this ) {
        if ( cache != null ) {
          cache.removeAll();
        }
      }
    }

    public void shutdown() {
      synchronized ( EhCacheDataCache.this ) {
        if ( cache != null ) {
          cache.removeAll();
        }
        if ( ClassicEngineBoot.getInstance().getExtendedConfig().getBoolProperty(
            "org.pentaho.reporting.engine.classic.core.cache.EhCacheDataCache.UseGlobalCacheManager" ) ) {
          if ( manager != null ) {
            manager.close();
          }
        }
        cache = null;
        manager = null;

        DataCacheFactory.notifyCacheShutdown( EhCacheDataCache.this );
      }
    }
  }

  private static final String CACHE_NAME = "report-dataset-cache";
  private DataCacheManager cacheManager;
  private int maximumRows;
  private volatile CacheManager manager;
  private volatile Cache cache;

  public EhCacheDataCache() throws URISyntaxException {
    this( ClassicEngineBoot.getInstance().getExtendedConfig().getIntProperty(
        "org.pentaho.reporting.engine.classic.core.cache.EhCacheDataCache.CachableRowLimit" ) );
  }

  public EhCacheDataCache( final int maximumRows ) throws URISyntaxException {
    this.maximumRows = maximumRows;
    this.cacheManager = new GlobalCacheManager();

    initializeCacheManager();
    initialize();
  }

  private void initializeCacheManager() throws URISyntaxException {
    if ( ClassicEngineBoot.getInstance().getExtendedConfig().getBoolProperty(
        "org.pentaho.reporting.engine.classic.core.cache.EhCacheDataCache.UseGlobalCacheManager" ) ) {
      manager = Caching.getCachingProvider().getCacheManager();
    } else if ( manager == null ) {
      CachingProvider cachingProvider = Caching.getCachingProvider();
      manager = cachingProvider.getCacheManager( getClass().getResource( "/ehcache.xml" ).toURI(), getClass().getClassLoader() );
    }
  }

  private synchronized void initialize() throws URISyntaxException {
    if ( manager != null ) {
      if ( manager.isClosed() ) {
        initializeCacheManager();
      }
    }

    if ( cache != null ) {
      if ( !cache.isClosed()) {
        return;
      }
    }

    Cache<Object,Object> cache = manager.getCache( CACHE_NAME );
    if ( cache == null ) {
      MutableConfiguration<Object,Object> configuration = new MutableConfiguration<>().setStoreByValue( false );
      manager.createCache( CACHE_NAME, configuration );
    }
    else {
      cache = manager.getCache( CACHE_NAME );
    }

  }

  public int getMaximumRows() {
    return maximumRows;
  }

  public TableModel get( final DataCacheKey key ) {
    final Cache cache = this.cache;
    synchronized ( this ) {
      if ( cache == null ) {
        return null;
      }

      if ( cache.isClosed() ) {
        this.cache = null;
        return null;
      }
    }

    final Object element = cache.get( key );
    if ( element == null ) {
      return null;
    }
    return (TableModel) element;
  }

  public TableModel put( final DataCacheKey key, final TableModel model ) {
    if ( model.getRowCount() > maximumRows ) {
      return model;
    }

    // Only copy if safe to do so. Check for whitelist of good column types ..
    if ( CachableTableModel.isSafeToCache( model ) == false ) {
      return model;
    }

    final Cache cache;
    synchronized ( this ) {
        try {
            initialize();
        } catch ( URISyntaxException e ) {
            throw new RuntimeException( e );
        }
        cache = this.cache;
    }

    final TableModel cacheModel = new CachableTableModel( model );
    cache.put( key, cacheModel );
    return cacheModel;
  }

  public DataCacheManager getCacheManager() {
    return cacheManager;
  }
}
