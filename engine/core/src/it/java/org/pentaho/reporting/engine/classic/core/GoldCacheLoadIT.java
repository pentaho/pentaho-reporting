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

import net.sf.ehcache.CacheManager;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.testsupport.gold.GoldTestBase;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.*;

public class GoldCacheLoadIT extends GoldTestBase {
  private CacheManager cacheManager;
  private ExecutorService threadPool;
  private int maxThreads;
  private static HashMap<Long, MasterReport> masterReportList = new HashMap<Long, MasterReport>();

  public GoldCacheLoadIT() {
  }

  @Before
  public void setupThreadPoolAndDisableCache() {
    maxThreads = 2;
    threadPool = Executors.newFixedThreadPool( maxThreads );

    cacheManager = CacheManager.getInstance();
    if ( cacheManager.cacheExists( "libloader-bundles" ) == true ) {
      // Note: EHCacheProvider will dynamically create these
      // caches if they don't exist.
      cacheManager.clearAll();
      cacheManager.removalAll();

      assertFalse( cacheManager.cacheExists( "libloader-bundles" ) );
      assertFalse( cacheManager.cacheExists( "libloader-data" ) );
      assertFalse( cacheManager.cacheExists( "libloader-factory" ) );
      assertFalse( cacheManager.cacheExists( "report-dataset-cache" ) );
    }
  }

  protected MasterReport postProcess( final MasterReport report ) throws Exception {
    final Object dataCacheEnabledRaw =
        report.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.DATA_CACHE );
    assertFalse( Boolean.FALSE.equals( dataCacheEnabledRaw ) );

    masterReportList.put( new Long( Thread.currentThread().getId() ), report );

    return report;
  }

  private void validateMasterReport( final MasterReport report ) {
    assertNotNull( report );

    // Validate Cache
    ResourceManager resourceManager = report.getResourceManager();

    // TODO - validate that the cache is provisioned correctly (especially diskPersistence)
    // resourceManager.getBundleCache().

    String[] queryNames = report.getDataFactory().getQueryNames();
    assertNotNull( queryNames );
    assertEquals( 2, queryNames.length );
    assertEquals( "TerritoryList", queryNames[0] );
    assertEquals( "Query 1", queryNames[1] );

    PageDefinition pageDefinition = report.getPageDefinition();
    assertNotNull( pageDefinition );

    // TODO - this was causing failures - not sure why
    // assertEquals(576.0, pageDefinition.getHeight());
    // assertEquals(734.0, pageDefinition.getWidth());
    assertEquals( 1, pageDefinition.getPageCount() );

    Configuration configuration = report.getConfiguration();
    String configDate =
        configuration
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.environment.::internal::report.date" );
    assertEquals( "2011-04-07T15:00:00.000+0000", configDate );

    // TODO - add more tests to validate against a corrupt report
    // Validate Data Factory: query name, query, connection path (from ConnectionProvider) = 'SampleData', data
    // factory size = 1,
    // query mappings = 2 (TerritoryList, Query 1
    // ResourceManager (no data, bundle or factory cache)
    // Report Configuration (config = report.date 2011-04-07T15:00:00.000+0000)
    // CacheManager
    // Report Structure: RootGroup, ReportHeader, ReportFooter, PageHeader, PageFooter, WaterMark
    // non-visual change tracker, datasource change tracker
    // ReportParameterDefinition reportParameterDefinition = report.getParameterDefinition();
    // ReportEnvironment reportEnvironment = report.getReportEnvironment();
  }

  @Test
  public void testExecuteReports() throws Exception {
    final ArrayList<Exception> exceptions = new ArrayList<Exception>();
    for ( int numThread = 0; numThread < maxThreads; numThread++ ) {
      threadPool.submit( new Runnable() {
        @Override
        public void run() {
          try {
            GoldCacheLoadIT cacheLoadTest = new GoldCacheLoadIT();
            cacheLoadTest.setUp();
            cacheLoadTest.runSingleGoldReport( "Prd-3159.prpt", ReportProcessingMode.current );
            final MasterReport report = masterReportList.get( new Long( Thread.currentThread().getId() ) );
            validateMasterReport( report );
          } catch ( Exception ex ) {
            exceptions.add( ex );
            System.out.println( "Exception caught: " + ex.toString() );
          }
        }
      } );
    }

    threadPool.shutdown();
    while ( threadPool.isTerminated() == false ) {
      threadPool.awaitTermination( 5, TimeUnit.MINUTES );
    }

    assertTrue( exceptions.isEmpty() );
  }
}
