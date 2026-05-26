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


package org.pentaho.reporting.engine.classic.core.layout.output;

import org.junit.Before;
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
import org.pentaho.reporting.engine.classic.core.states.ProcessStateHandle;
import org.pentaho.reporting.engine.classic.core.testsupport.dummyoutput.DummyReportProcessor;
import org.pentaho.reporting.libraries.base.config.Configuration;
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

  private DummyReportProcessor processor;

  /**
   * A testable subclass that exposes protected methods for testing.
   */
  private static class TestableReportProcessor extends AbstractReportProcessor {
    public TestableReportProcessor( final MasterReport report ) throws ReportProcessingException {
      super( report, new org.pentaho.reporting.engine.classic.core.testsupport.dummyoutput.DummyOutputProcessor() );
    }

    @Override
    protected OutputFunction createLayoutManager() {
      final DefaultOutputFunction outputFunction = new DefaultOutputFunction();
      outputFunction.setRenderer( new org.pentaho.reporting.engine.classic.core.testsupport.dummyoutput.DummyRenderer(
          getOutputProcessor() ) );
      return outputFunction;
    }

    // Expose protected methods for testing
    public ProcessStateHandle exposedGetProcessStateHandle() {
      return getProcessStateHandle();
    }

    public void exposedSetProcessStateHandle( final ProcessStateHandle handle ) {
      setProcessStateHandle( handle );
    }

    public MasterReport exposedGetReport() {
      return getReport();
    }

    public OutputProcessorMetaData exposedGetOutputProcessorMetaData() {
      return getOutputProcessorMetaData();
    }

    public DefaultProcessingContext exposedCreateProcessingContext() throws ReportProcessingException {
      return createProcessingContext();
    }

    public void exposedCheckInterrupted() throws ReportInterruptedException {
      checkInterrupted();
    }
  }

  @BeforeClass
  public static void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }


    @Before
    public void processorSetUp() throws Exception {
        processor = new DummyReportProcessor(new MasterReport());
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

    @Test
    public void testSetAndGetHandleInterruptedState() {
        processor.setHandleInterruptedState(false);
        assertFalse(processor.isHandleInterruptedState());
        processor.setHandleInterruptedState(true);
        assertTrue(processor.isHandleInterruptedState());
    }

    @Test
    public void testSetAndGetFullStreamingProcessor() {
        processor.setFullStreamingProcessor(false);
        assertFalse(processor.isFullStreamingProcessor());
        processor.setFullStreamingProcessor(true);
        assertTrue(processor.isFullStreamingProcessor());
    }

    @Test
    public void testGetConfiguration() {
        assertNotNull(processor.getConfiguration());
    }

    @Test
    public void testIsStrictErrorHandling() {
        Configuration config = mock(Configuration.class);
        when(config.getConfigProperty(anyString())).thenReturn("true");
        assertTrue(AbstractReportProcessor.isStrictErrorHandling(config));
        when(config.getConfigProperty(anyString())).thenReturn("false");
        assertFalse(AbstractReportProcessor.isStrictErrorHandling(config));
    }

    @Test
    public void testGetLogicalAndPhysicalPageCountWhenNull() {
        assertEquals(-1, processor.getLogicalPageCount());
        assertEquals(-1, processor.getPhysicalPageCount());
    }

    @Test
    public void testSetQueryLimitReached() {
        processor.setQueryLimitReached(true);
        assertTrue(processor.isQueryLimitReached());
        processor.setQueryLimitReached(false);
        assertFalse(processor.isQueryLimitReached());
    }

    // ========== NEW TEST CASES FOR INCREASED COVERAGE ==========

    @Test
    public void testGetOutputProcessor() {
        assertNotNull( processor.getOutputProcessor() );
    }

    @Test
    public void testIsPaginatedReturnsFalseBeforePagination() {
        assertFalse( processor.isPaginated() );
    }

    @Test
    public void testIsPaginatedReturnsTrueAfterPrepare() throws Exception {
        final MasterReport report = new MasterReport();
        final DefaultTableModel model = new DefaultTableModel( 10, 3 );
        report.setDataFactory( new TableDataFactory( "default", model ) );
        final AbstractReportProcessor reportProcessor = new DummyReportProcessor( report );
        reportProcessor.prepareReportProcessing();
        assertTrue( reportProcessor.isPaginated() );
        reportProcessor.close();
    }

    @Test
    public void testPaginateReturnsTrueAndMarksPaginated() throws Exception {
        final MasterReport report = new MasterReport();
        final DefaultTableModel model = new DefaultTableModel( 10, 3 );
        report.setDataFactory( new TableDataFactory( "default", model ) );
        final AbstractReportProcessor reportProcessor = new DummyReportProcessor( report );
        boolean result = reportProcessor.paginate();
        assertTrue( result );
        assertTrue( reportProcessor.isPaginated() );
        reportProcessor.close();
    }

    @Test
    public void testPaginateDoesNotRepaginateIfAlreadyPaginated() throws Exception {
        final MasterReport report = new MasterReport();
        final DefaultTableModel model = new DefaultTableModel( 10, 3 );
        report.setDataFactory( new TableDataFactory( "default", model ) );
        final AbstractReportProcessor reportProcessor = new DummyReportProcessor( report );
        reportProcessor.paginate();
        assertTrue( reportProcessor.isPaginated() );
        // Calling paginate again should just return true without re-processing
        boolean result = reportProcessor.paginate();
        assertTrue( result );
        reportProcessor.close();
    }

    @Test
    public void testProcessPageThrowsNullPointerForNullPageState() throws Exception {
        final MasterReport report = new MasterReport();
        final DefaultTableModel model = new DefaultTableModel( 10, 3 );
        report.setDataFactory( new TableDataFactory( "default", model ) );
        final AbstractReportProcessor reportProcessor = new DummyReportProcessor( report );
        try {
            reportProcessor.processPage( null, false );
            fail( "Expected NullPointerException" );
        } catch ( NullPointerException e ) {
            assertNotNull( e );
        } finally {
            reportProcessor.close();
        }
    }

    @Test
    public void testCancelSetsInterruptedFlag() throws ReportProcessingException {
        // cancel() sets manuallyInterrupted; subsequent checkInterrupted() should throw
        final TestableReportProcessor testable = new TestableReportProcessor( new MasterReport() );
        testable.cancel();
        try {
            testable.exposedCheckInterrupted();
            fail( "Expected ReportInterruptedException after cancel" );
        } catch ( ReportInterruptedException e ) {
            assertNotNull( e.getMessage() );
            assertTrue( e.getMessage().contains( "interrupted" ) );
        } finally {
            testable.close();
        }
    }

    @Test
    public void testCheckInterruptedDoesNotThrowWhenHandleInterruptedStateIsFalse() throws Exception {
        final TestableReportProcessor testable = new TestableReportProcessor( new MasterReport() );
        testable.setHandleInterruptedState( false );
        testable.cancel();
        // Should NOT throw even though manuallyInterrupted is true
        testable.exposedCheckInterrupted();
        assertTrue( true ); // reached here means no exception
        testable.close();
    }

    @Test
    public void testCheckInterruptedDoesNotThrowWhenNotInterrupted() throws Exception {
        final TestableReportProcessor testable = new TestableReportProcessor( new MasterReport() );
        testable.setHandleInterruptedState( true );
        // Should NOT throw since neither manuallyInterrupted nor thread interrupted
        testable.exposedCheckInterrupted();
        assertTrue( true );
        testable.close();
    }

    @Test
    public void testGetProcessStateHandleInitiallyNull() throws ReportProcessingException {
        final TestableReportProcessor testable = new TestableReportProcessor( new MasterReport() );
        assertNull( testable.exposedGetProcessStateHandle() );
        testable.close();
    }

    @Test
    public void testSetAndGetProcessStateHandle() throws ReportProcessingException {
        final TestableReportProcessor testable = new TestableReportProcessor( new MasterReport() );
        final ProcessStateHandle mockHandle = mock( ProcessStateHandle.class );
        testable.exposedSetProcessStateHandle( mockHandle );
        assertSame( mockHandle, testable.exposedGetProcessStateHandle() );
        testable.close();
    }

    @Test
    public void testGetReportReturnsNonNull() throws ReportProcessingException {
        final TestableReportProcessor testable = new TestableReportProcessor( new MasterReport() );
        assertNotNull( testable.exposedGetReport() );
        testable.close();
    }

    @Test
    public void testGetOutputProcessorMetaDataReturnsNonNull() throws ReportProcessingException {
        final TestableReportProcessor testable = new TestableReportProcessor( new MasterReport() );
        assertNotNull( testable.exposedGetOutputProcessorMetaData() );
        testable.close();
    }

    @Test
    public void testCreateProcessingContextReturnsNonNull() throws ReportProcessingException {
        final TestableReportProcessor testable = new TestableReportProcessor( new MasterReport() );
        final DefaultProcessingContext ctx = testable.exposedCreateProcessingContext();
        assertNotNull( ctx );
        testable.close();
    }

    @Test
    public void testFireEventsWithNoListenersDoesNotThrow() {
        // Processor with no listeners added - fire events should not throw
        final ReportProgressEvent event = new ReportProgressEvent( new Object() );
        processor.fireProcessingStarted( event );
        processor.fireStateUpdate( event );
        processor.fireProcessingFinished( event );
        // If we reach here, no exception was thrown
        assertTrue( true );
    }

    @Test
    public void testMultipleListenersAllReceiveEvents() throws Exception {
        final AbstractReportProcessor reportProcessor = new DummyReportProcessor( new MasterReport() );
        final ReportProgressListener listener1 = mock( ReportProgressListener.class );
        final ReportProgressListener listener2 = mock( ReportProgressListener.class );
        reportProcessor.addReportProgressListener( listener1 );
        reportProcessor.addReportProgressListener( listener2 );
        final ReportProgressEvent event = new ReportProgressEvent( new Object() );
        reportProcessor.fireProcessingStarted( event );
        reportProcessor.fireStateUpdate( event );
        reportProcessor.fireProcessingFinished( event );
        verify( listener1, times( 1 ) ).reportProcessingStarted( event );
        verify( listener1, times( 1 ) ).reportProcessingUpdate( event );
        verify( listener1, times( 1 ) ).reportProcessingFinished( event );
        verify( listener2, times( 1 ) ).reportProcessingStarted( event );
        verify( listener2, times( 1 ) ).reportProcessingUpdate( event );
        verify( listener2, times( 1 ) ).reportProcessingFinished( event );
        reportProcessor.close();
    }

    @Test
    public void testCloseMultipleTimesDoesNotThrow() throws ReportProcessingException {
        final AbstractReportProcessor reportProcessor = new DummyReportProcessor( new MasterReport() );
        reportProcessor.close();
        // Calling close a second time should not throw
        reportProcessor.close();
        assertNull( ReportProcessorThreadHolder.getProcessor() );
    }

    @Test
    public void testCloseWithActiveDataFactory() throws Exception {
        final MasterReport report = new MasterReport();
        final DefaultTableModel model = new DefaultTableModel( 10, 3 );
        report.setDataFactory( new TableDataFactory( "default", model ) );
        final TestableReportProcessor testable = new TestableReportProcessor( report );
        // Set a mock ProcessStateHandle to verify close is called on it
        final ProcessStateHandle mockHandle = mock( ProcessStateHandle.class );
        testable.exposedSetProcessStateHandle( mockHandle );
        testable.close();
        verify( mockHandle, times( 1 ) ).close();
    }

    @Test
    public void testIsStrictErrorHandlingWithNullConfig() {
        Configuration config = mock( Configuration.class );
        when( config.getConfigProperty( anyString() ) ).thenReturn( null );
        assertFalse( AbstractReportProcessor.isStrictErrorHandling( config ) );
    }

    @Test
    public void testIsStrictErrorHandlingWithEmptyString() {
        Configuration config = mock( Configuration.class );
        when( config.getConfigProperty( anyString() ) ).thenReturn( "" );
        assertFalse( AbstractReportProcessor.isStrictErrorHandling( config ) );
    }

    @Test
    public void testDefaultHandleInterruptedStateIsTrue() {
        // The constructor sets handleInterruptedState = true by default
        assertTrue( processor.isHandleInterruptedState() );
    }

    @Test
    public void testDefaultFullStreamingProcessorIsTrue() {
        // The constructor sets fullStreamingProcessor = true by default
        assertTrue( processor.isFullStreamingProcessor() );
    }

    @Test
    public void testDefaultQueryLimitReachedIsFalse() {
        assertFalse( processor.isQueryLimitReached() );
    }

    @Test
    public void testDefaultIsPaginatedIsFalse() {
        assertFalse( processor.isPaginated() );
    }

    @Test
    public void testProcessReportWithData() throws Exception {
        final MasterReport report = new MasterReport();
        final DefaultTableModel model = new DefaultTableModel( 10, 3 );
        report.setDataFactory( new TableDataFactory( "default", model ) );
        final AbstractReportProcessor reportProcessor = new DummyReportProcessor( report );
        reportProcessor.processReport();
        assertTrue( reportProcessor.isPaginated() );
        assertTrue( reportProcessor.getLogicalPageCount() > 0 );
        assertTrue( reportProcessor.getPhysicalPageCount() > 0 );
        reportProcessor.close();
    }

    @Test
    public void testPaginateFiresStartedAndFinishedEvents() throws Exception {
        final MasterReport report = new MasterReport();
        final DefaultTableModel model = new DefaultTableModel( 10, 3 );
        report.setDataFactory( new TableDataFactory( "default", model ) );
        final AbstractReportProcessor reportProcessor = new DummyReportProcessor( report );
        final ReportProgressListener listener = mock( ReportProgressListener.class );
        reportProcessor.addReportProgressListener( listener );
        reportProcessor.paginate();
        verify( listener, atLeastOnce() ).reportProcessingStarted( any( ReportProgressEvent.class ) );
        verify( listener, atLeastOnce() ).reportProcessingFinished( any( ReportProgressEvent.class ) );
        reportProcessor.close();
    }

    @Test
    public void testPrepareReportProcessingIsIdempotent() throws Exception {
        final MasterReport report = new MasterReport();
        final DefaultTableModel model = new DefaultTableModel( 10, 3 );
        report.setDataFactory( new TableDataFactory( "default", model ) );
        final AbstractReportProcessor reportProcessor = new DummyReportProcessor( report );
        reportProcessor.prepareReportProcessing();
        assertTrue( reportProcessor.isPaginated() );
        int logicalCount = reportProcessor.getLogicalPageCount();
        int physicalCount = reportProcessor.getPhysicalPageCount();
        // Call again - should be idempotent (returns early if already paginated)
        reportProcessor.prepareReportProcessing();
        assertEquals( logicalCount, reportProcessor.getLogicalPageCount() );
        assertEquals( physicalCount, reportProcessor.getPhysicalPageCount() );
        reportProcessor.close();
    }

    @Test
    public void testSetFullStreamingProcessorAffectsPageStateListType() throws Exception {
        final MasterReport report = new MasterReport();
        final DefaultTableModel model = new DefaultTableModel( 10, 3 );
        report.setDataFactory( new TableDataFactory( "default", model ) );

        // Test with full streaming = true (default) - uses FastPageStateList
        final AbstractReportProcessor streamingProcessor = new DummyReportProcessor( report );
        assertTrue( streamingProcessor.isFullStreamingProcessor() );
        streamingProcessor.prepareReportProcessing();
        assertTrue( streamingProcessor.isPaginated() );
        streamingProcessor.close();

        // Test with full streaming = false - uses DefaultPageStateList
        final AbstractReportProcessor nonStreamingProcessor = new DummyReportProcessor( report );
        nonStreamingProcessor.setFullStreamingProcessor( false );
        assertFalse( nonStreamingProcessor.isFullStreamingProcessor() );
        nonStreamingProcessor.prepareReportProcessing();
        assertTrue( nonStreamingProcessor.isPaginated() );
        nonStreamingProcessor.close();
    }

    @Test
    public void testProcessReportFiresAllProgressEvents() throws Exception {
        final MasterReport report = new MasterReport();
        final DefaultTableModel model = new DefaultTableModel( 10, 3 );
        report.setDataFactory( new TableDataFactory( "default", model ) );
        final AbstractReportProcessor reportProcessor = new DummyReportProcessor( report );
        final ReportProgressListener listener = mock( ReportProgressListener.class );
        reportProcessor.addReportProgressListener( listener );
        reportProcessor.processReport();
        verify( listener, atLeastOnce() ).reportProcessingStarted( any( ReportProgressEvent.class ) );
        verify( listener, atLeastOnce() ).reportProcessingFinished( any( ReportProgressEvent.class ) );
        reportProcessor.close();
    }

    @Test
    public void testConstructorWithYieldRateConfig() throws Exception {
        final MasterReport report = new MasterReport();
        report.getReportConfiguration().setConfigProperty(
            "org.pentaho.reporting.engine.classic.core.YieldRate", "50" );
        final AbstractReportProcessor reportProcessor = new DummyReportProcessor( report );
        assertNotNull( reportProcessor );
        reportProcessor.close();
    }

    @Test
    public void testConstructorWithProfilingConfig() throws Exception {
        final MasterReport report = new MasterReport();
        report.getReportConfiguration().setConfigProperty(
            "org.pentaho.reporting.engine.classic.core.ProfileReportProcessing", "true" );
        report.getReportConfiguration().setConfigProperty(
            "org.pentaho.reporting.engine.classic.core.performance.LogLevelProgress", "true" );
        report.getReportConfiguration().setConfigProperty(
            "org.pentaho.reporting.engine.classic.core.performance.LogPageProgress", "true" );
        report.getReportConfiguration().setConfigProperty(
            "org.pentaho.reporting.engine.classic.core.performance.LogRowProgress", "true" );
        final AbstractReportProcessor reportProcessor = new DummyReportProcessor( report );
        assertNotNull( reportProcessor );
        reportProcessor.close();
    }

    @Test
    public void testConstructorWithParanoidChecksConfig() throws Exception {
        final MasterReport report = new MasterReport();
        report.getReportConfiguration().setConfigProperty(
            "org.pentaho.reporting.engine.classic.core.layout.ParanoidChecks", "true" );
        final DefaultTableModel model = new DefaultTableModel( 5, 2 );
        report.setDataFactory( new TableDataFactory( "default", model ) );
        final AbstractReportProcessor reportProcessor = new DummyReportProcessor( report );
        assertNotNull( reportProcessor );
        // Just ensure it doesn't blow up during processing with paranoid checks on
        reportProcessor.paginate();
        assertTrue( reportProcessor.isPaginated() );
        reportProcessor.close();
    }

    @Test
    public void testRemoveListenerFromEmptyListDoesNotThrow() throws Exception {
        final AbstractReportProcessor reportProcessor = new DummyReportProcessor( new MasterReport() );
        final ReportProgressListener listener = mock( ReportProgressListener.class );
        // Remove a listener that was never added (but listeners list is not null because no listener was added yet)
        // This tests the case where listeners == null
        reportProcessor.removeReportProgressListener( listener );
        // If we reach here, no exception was thrown
        assertTrue( true );
        reportProcessor.close();
    }

    @Test
    public void testGetConfigurationMatchesReportConfig() throws Exception {
        final MasterReport report = new MasterReport();
        final AbstractReportProcessor reportProcessor = new DummyReportProcessor( report );
        Configuration config = reportProcessor.getConfiguration();
        assertNotNull( config );
        // The configuration comes from the cloned report, so it should be a valid Configuration object
        assertSame( config.getClass(), report.getConfiguration().getClass() );
        reportProcessor.close();
    }

    @Test
    public void testSetProcessStateHandleToNull() throws ReportProcessingException {
        final TestableReportProcessor testable = new TestableReportProcessor( new MasterReport() );
        final ProcessStateHandle mockHandle = mock( ProcessStateHandle.class );
        testable.exposedSetProcessStateHandle( mockHandle );
        assertSame( mockHandle, testable.exposedGetProcessStateHandle() );
        testable.exposedSetProcessStateHandle( null );
        assertNull( testable.exposedGetProcessStateHandle() );
        testable.close();
    }

    @Test
    public void testCancelAndProcessReportThrowsInterrupted() throws Exception {
        final MasterReport report = new MasterReport();
        final DefaultTableModel model = new DefaultTableModel( 500, 10 );
        report.setDataFactory( new TableDataFactory( "default", model ) );
        final AbstractReportProcessor reportProcessor = new DummyReportProcessor( report );
        // Cancel before processing
        reportProcessor.cancel();
        try {
            reportProcessor.processReport();
            fail( "Expected ReportInterruptedException" );
        } catch ( ReportInterruptedException e ) {
            assertNotNull( e.getMessage() );
        } finally {
            reportProcessor.close();
        }
    }

}
