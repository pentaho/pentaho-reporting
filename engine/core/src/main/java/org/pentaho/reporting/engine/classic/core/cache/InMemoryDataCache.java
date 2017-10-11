/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.cache;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.util.LFUMap;

import javax.swing.table.TableModel;

/**
 * The simplest of all caches systems. A plain map holding all elements.
 *
 * @author Thomas Morgner.
 */
public class InMemoryDataCache implements DataCache {
  private class InMemoryCacheManager implements DataCacheManager {
    private InMemoryCacheManager() {
    }

    public void clearAll() {
      dataCache.clear();
    }

    public void shutdown() {
      dataCache.clear();
    }
  }

  private InMemoryCacheManager cacheManager;
  private LFUMap<DataCacheKey, TableModel> dataCache;
  private int maximumEntries;
  private int maximumRows;

  public InMemoryDataCache() {
    this( ClassicEngineBoot.getInstance().getExtendedConfig().getIntProperty(
        "org.pentaho.reporting.engine.classic.core.cache.InMemoryCache.MaxEntries" ), ClassicEngineBoot.getInstance()
        .getExtendedConfig().getIntProperty(
            "org.pentaho.reporting.engine.classic.core.cache.InMemoryCache.CachableRowLimit" ) );
  }

  public InMemoryDataCache( final int maximumEntries, final int maximumRows ) {
    this.maximumEntries = maximumEntries;
    this.maximumRows = maximumRows;

    cacheManager = new InMemoryCacheManager();
    dataCache = new LFUMap<DataCacheKey, TableModel>( maximumEntries );
  }

  public int getMaximumEntries() {
    return maximumEntries;
  }

  public int getMaximumRows() {
    return maximumRows;
  }

  public TableModel get( final DataCacheKey key ) {
    return dataCache.get( key );
  }

  public TableModel put( final DataCacheKey key, final TableModel model ) {
    if ( model.getRowCount() > maximumRows ) {
      return model;
    }

    // Only copy if safe to do so. Check for whitelist of good column types ..
    if ( CachableTableModel.isSafeToCache( model ) == false ) {
      return model;
    }

    final TableModel cacheModel = new CachableTableModel( model );
    dataCache.put( key, cacheModel );
    return cacheModel;
  }

  public DataCacheManager getCacheManager() {
    return cacheManager;
  }
}
