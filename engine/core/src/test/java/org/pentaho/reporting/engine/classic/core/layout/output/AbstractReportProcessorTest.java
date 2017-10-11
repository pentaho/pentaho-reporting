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

package org.pentaho.reporting.engine.classic.core.layout.output;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportInterruptedException;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressListener;
import org.pentaho.reporting.engine.classic.core.function.OutputFunction;
import org.pentaho.reporting.engine.classic.core.testsupport.dummyoutput.DummyReportProcessor;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.swing.table.DefaultTableModel;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AbstractReportProcessorTest {

  @BeforeClass
  public static void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testConsrtruction() throws Exception {
    boolean thrown1 = false;
    try {
      new DummyReportProcessor( null );
    } catch ( final NullPointerException e ) {
      thrown1 = true;
    }
    assertEquals( true, thrown1 );
    boolean thrown2 = false;
    try {
      new AbstractReportProcessor( new MasterReport(), null ) {

        @Override protected OutputFunction createLayoutManager() {
          return null;
        }
      };
    } catch ( final NullPointerException e ) {
      thrown2 = true;
    }
    assertEquals( true, thrown2 );


  }

  @Test
  public void addReportProgressListener() throws Exception {
    final AbstractReportProcessor reportProcessor = new DummyReportProcessor( new MasterReport() );
    boolean thrown1 = false;
    try {
      reportProcessor.addReportProgressListener( null );
    } catch ( final NullPointerException e ) {
      thrown1 = true;
    }
    assertEquals( true, thrown1 );
  }

  @Test
  public void addRemoveCallListener() throws Exception {
    final AbstractReportProcessor reportProcessor = new DummyReportProcessor( new MasterReport() );
    boolean thrown1 = false;
    try {
      reportProcessor.removeReportProgressListener( null );
    } catch ( final NullPointerException e ) {
      thrown1 = true;
    }
    assertEquals( true, thrown1 );
    final ReportProgressListener mock = mock( ReportProgressListener.class );
    reportProcessor.removeReportProgressListener( mock );
    reportProcessor.addReportProgressListener( mock );
    final ReportProgressEvent state = new ReportProgressEvent( new Object() );
    reportProcessor.fireProcessingStarted( state );
    reportProcessor.fireStateUpdate( state );
    reportProcessor.fireProcessingFinished( state );
    verify( mock, times( 1 ) ).reportProcessingStarted( state );
    verify( mock, times( 1 ) ).reportProcessingUpdate( state );
    verify( mock, times( 1 ) ).reportProcessingFinished( state );
    reportProcessor.removeReportProgressListener( mock );
    reportProcessor.fireProcessingStarted( state );
    reportProcessor.fireStateUpdate( state );
    reportProcessor.fireProcessingFinished( state );
    verify( mock, times( 1 ) ).reportProcessingStarted( state );
    verify( mock, times( 1 ) ).reportProcessingUpdate( state );
    verify( mock, times( 1 ) ).reportProcessingFinished( state );
  }

  @Test
  public void testIsQueryLimitNotReachedForUnlimitedQueryLimit() throws Exception {
    final MasterReport report = new MasterReport();
    final DefaultTableModel model = new DefaultTableModel( 500, 10 );
    report.setDataFactory( new TableDataFactory( "default", model ) );
    report.setQueryLimit( -1 );
    final AbstractReportProcessor reportProcessor = new DummyReportProcessor( report );
    reportProcessor.prepareReportProcessing();
    assertEquals( reportProcessor.isQueryLimitReached(), false );
  }

  @Test
  public void testIsQueryLimitNotReachedForUnsetQueryLimit() throws Exception {
    final MasterReport report = new MasterReport();
    final DefaultTableModel model = new DefaultTableModel( 500, 10 );
    report.setDataFactory( new TableDataFactory( "default", model ) );
    final AbstractReportProcessor reportProcessor = new DummyReportProcessor( report );
    reportProcessor.prepareReportProcessing();
    assertEquals( reportProcessor.isQueryLimitReached(), false );
  }

  @Test
  public void testIsQueryLimitNotReachedForZeroQueryLimit() throws Exception {
    final MasterReport report = new MasterReport();
    final DefaultTableModel model = new DefaultTableModel( 500, 10 );
    report.setDataFactory( new TableDataFactory( "default", model ) );
    report.setQueryLimit( 0 );
    final AbstractReportProcessor reportProcessor = new DummyReportProcessor( report );
    reportProcessor.prepareReportProcessing();
    assertEquals( reportProcessor.isQueryLimitReached(), false );
  }

  @Test
  public void testIsQueryLimitReachedForNumberOfRowsGreaterQueryLimit() throws Exception {
    final MasterReport report = new MasterReport();
    final DefaultTableModel model = new DefaultTableModel( 501, 10 );
    report.setDataFactory( new TableDataFactory( "default", model ) );
    report.setQueryLimit( 500 );
    final AbstractReportProcessor reportProcessor = new DummyReportProcessor( report );
    reportProcessor.prepareReportProcessing();
    assertEquals( reportProcessor.isQueryLimitReached(), true );
  }

  @Test
  public void testIsQueryLimitNotReachedForNumberOfRowsEqualQueryLimit() throws Exception {
    final MasterReport report = new MasterReport();
    final DefaultTableModel model = new DefaultTableModel( 500, 10 );
    report.setDataFactory( new TableDataFactory( "default", model ) );
    report.setQueryLimit( 500 );
    final AbstractReportProcessor reportProcessor = new DummyReportProcessor( report );
    reportProcessor.prepareReportProcessing();
    assertEquals( reportProcessor.isQueryLimitReached(), false );
  }

  @Test
  public void testIsQueryLimitNotReachedForNumberOfRowsLessQueryLimit() throws Exception {
    final MasterReport report = new MasterReport();
    final DefaultTableModel model = new DefaultTableModel( 499, 10 );
    report.setDataFactory( new TableDataFactory( "default", model ) );
    report.setQueryLimit( 500 );
    final AbstractReportProcessor reportProcessor = new DummyReportProcessor( report );
    reportProcessor.prepareReportProcessing();
    assertEquals( reportProcessor.isQueryLimitReached(), false );
  }

  @Test
  public void testIsLimitReachedForNumberOfRowsGreaterQueryLimit() throws Exception {
    //When data source enforce limit itself
    // report with 148 rows
    final URL url = getClass().getResource( "report1.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    report.setQueryLimit( 147 );
    final AbstractReportProcessor reportProcessor = new DummyReportProcessor( report );
    reportProcessor.prepareReportProcessing();
    assertEquals( reportProcessor.isQueryLimitReached(), true );
  }

  @Test
  public void testIsLimitNotReachedForNumberOfRowsEqualQueryLimit() throws Exception {
    //When data source enforce limit itself
    // report with 148 rows
    final URL url = getClass().getResource( "report1.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    report.setQueryLimit( 148 );
    final AbstractReportProcessor reportProcessor = new DummyReportProcessor( report );
    reportProcessor.prepareReportProcessing();
    assertEquals( reportProcessor.isQueryLimitReached(), false );
  }

  @Test
  public void testIsLimitNotReachedForNumberOfRowsLessQueryLimit() throws Exception {
    //When data source enforce limit itself
    // report with 148 rows
    final URL url = getClass().getResource( "report1.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    report.setQueryLimit( 149 );
    final AbstractReportProcessor reportProcessor = new DummyReportProcessor( report );
    reportProcessor.prepareReportProcessing();
    assertEquals( reportProcessor.isQueryLimitReached(), false );
  }

  @Test
  public void testConstructionAndClose() throws ReportProcessingException {
    final AbstractReportProcessor reportProcessor = new DummyReportProcessor( new MasterReport() );
    assertEquals( reportProcessor, ReportProcessorThreadHolder.getProcessor() );
    reportProcessor.close();
    assertNull( ReportProcessorThreadHolder.getProcessor() );
  }

  @Test
  public void testInterruptedOld() throws Exception {
    final boolean[] fired = { false };
    try {

      final MasterReport report = new MasterReport();
      final DefaultTableModel model = new DefaultTableModel( 500, 10 );
      report.setDataFactory( new TableDataFactory( "default", model ) );
      final AbstractReportProcessor reportProcessor = new DummyReportProcessor( report );

      final ReportProgressListener reportProgressListener = mock( ReportProgressListener.class );
      doAnswer( new Answer() {
        @Override public Object answer( final InvocationOnMock invocation ) throws Throwable {
          Thread.currentThread().interrupt();
          return null;
        }
      } ).when( reportProgressListener ).reportProcessingUpdate( any( ReportProgressEvent.class ) );
      reportProcessor.addReportProgressListener( reportProgressListener );

      final ExecutorService executorService = Executors.newSingleThreadExecutor();

      final CountDownLatch latch = new CountDownLatch( 1 );
      executorService.execute( new Runnable() {
        @Override public void run() {
          try {
            reportProcessor.processReport();
          } catch ( final ReportProcessingException e ) {
            fired[ 0 ] = true;
          }
          latch.countDown();
        }
      } );

      latch.await();
    } finally {
      assertTrue( fired[ 0 ] );
    }
  }


  @Test( expected = ReportInterruptedException.class )
  public void testInterruptedNew() throws Exception {

    final MasterReport report = new MasterReport();
    final DefaultTableModel model = new DefaultTableModel( 500, 10 );
    report.setDataFactory( new TableDataFactory( "default", model ) );
    final AbstractReportProcessor reportProcessor = new DummyReportProcessor( report );

    final ReportProgressListener reportProgressListener = mock( ReportProgressListener.class );
    doAnswer( new Answer() {
      @Override public Object answer( final InvocationOnMock invocation ) throws Throwable {
        reportProcessor.cancel();
        return null;
      }
    } ).when( reportProgressListener ).reportProcessingUpdate( any( ReportProgressEvent.class ) );
    reportProcessor.addReportProgressListener( reportProgressListener );

    reportProcessor.processReport();
  }

}
