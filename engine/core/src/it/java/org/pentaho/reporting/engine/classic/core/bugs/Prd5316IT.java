/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.bugs;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.cache.CachableTableModel;
import org.pentaho.reporting.engine.classic.core.cache.DataCache;
import org.pentaho.reporting.engine.classic.core.cache.DataCacheFactory;
import org.pentaho.reporting.engine.classic.core.cache.DataCacheKey;
import org.pentaho.reporting.engine.classic.core.cache.DataCacheManager;
import org.pentaho.reporting.engine.classic.core.cache.EhCacheDataCache;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultParameterContext;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultReportParameterValidator;
import org.pentaho.reporting.engine.classic.core.parameters.ValidationResult;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.swing.table.TableModel;
import java.util.HashMap;

public class Prd5316IT {
  public static class TestCacheBackend implements DataCache, DataCacheManager {
    private HashMap<DataCacheKey, TableModel> cache;
    private int putCount;
    private int getCount;

    public TestCacheBackend() {
      cache = new HashMap<DataCacheKey, TableModel>();
    }

    public TableModel get( final DataCacheKey key ) {
      getCount += 1;
      return cache.get( key );
    }

    public TableModel put( final DataCacheKey key, final TableModel model ) {
      putCount += 1;
      TableModel cachable = new CachableTableModel( model );
      cache.put( key, cachable );
      return cachable;
    }

    public DataCacheManager getCacheManager() {
      return this;
    }

    public HashMap<DataCacheKey, TableModel> getCache() {
      return cache;
    }

    public void clearAll() {
      cache.clear();
      getCount = 0;
      putCount = 0;
    }

    public void shutdown() {

    }
  }

  private static String cache;

  @BeforeClass
  public static void setUp() throws Exception {
    DataCacheFactory.notifyCacheShutdown( DataCacheFactory.getCache() );

    ClassicEngineBoot boot = ClassicEngineBoot.getInstance();
    boot.start();
    cache = boot.getGlobalConfig().getConfigProperty( DataCache.class.getName() );
    boot.getEditableConfig().setConfigProperty( DataCache.class.getName(), TestCacheBackend.class.getName() );
  }

  @AfterClass
  public static void tearDown() throws Exception {
    TestCacheBackend cacheInstance = (TestCacheBackend) DataCacheFactory.getCache();
    cacheInstance.clearAll();

    ClassicEngineBoot boot = ClassicEngineBoot.getInstance();

    boot.getEditableConfig().setConfigProperty( DataCache.class.getName(), cache );

    DataCacheFactory.notifyCacheShutdown( DataCacheFactory.getCache() );
  }

  @Before
  public void clearCacheJustInCase() throws Exception {
    DataCacheFactory.getCache().getCacheManager().clearAll();
  }

  @Test
  public void testParameterCache() throws ResourceException, ReportProcessingException {
    Assert.assertEquals( EhCacheDataCache.class.getName(), cache );
    Assert.assertEquals( TestCacheBackend.class.getName(), ClassicEngineBoot.getInstance().getGlobalConfig()
        .getConfigProperty( DataCache.class.getName() ) );

    MasterReport resource =
        (MasterReport) new ResourceManager().createDirectly( getClass().getResource( "Prd-5316.prpt" ),
            MasterReport.class ).getResource();

    DefaultReportParameterValidator v = new DefaultReportParameterValidator();
    v.validate( new ValidationResult(), resource.getParameterDefinition(), new DefaultParameterContext( resource ) );
    v.validate( new ValidationResult(), resource.getParameterDefinition(), new DefaultParameterContext( resource ) );
    v.validate( new ValidationResult(), resource.getParameterDefinition(), new DefaultParameterContext( resource ) );

    TestCacheBackend cacheInstance = (TestCacheBackend) DataCacheFactory.getCache();
    Assert.assertEquals( 1, cacheInstance.getCache().size() );
    Assert.assertEquals( 1, cacheInstance.putCount );
    Assert.assertEquals( 3, cacheInstance.getCount );
  }

  @Test
  public void testReportRunCache() throws Exception {
    Assert.assertEquals( EhCacheDataCache.class.getName(), cache );
    Assert.assertEquals( TestCacheBackend.class.getName(), ClassicEngineBoot.getInstance().getGlobalConfig()
        .getConfigProperty( DataCache.class.getName() ) );

    MasterReport resource =
        (MasterReport) new ResourceManager().createDirectly( getClass().getResource( "Prd-5316.prpt" ),
            MasterReport.class ).getResource();
    DebugReportRunner.execGraphics2D( resource );
    DebugReportRunner.execGraphics2D( resource );
    DebugReportRunner.execGraphics2D( resource );

    TestCacheBackend cacheInstance = (TestCacheBackend) DataCacheFactory.getCache();
    Assert.assertEquals( 1, cacheInstance.getCache().size() );
    Assert.assertEquals( 1, cacheInstance.putCount );
    Assert.assertEquals( 3, cacheInstance.getCount );
  }
}
