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

package org.pentaho.reporting.engine.classic.core;

import junit.framework.TestCase;
import net.sf.ehcache.CacheManager;
import org.pentaho.reporting.engine.classic.core.cache.DataCacheKey;
import org.pentaho.reporting.engine.classic.core.cache.EhCacheDataCache;

import javax.swing.table.DefaultTableModel;

public class CacheKillingIT extends TestCase {
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
