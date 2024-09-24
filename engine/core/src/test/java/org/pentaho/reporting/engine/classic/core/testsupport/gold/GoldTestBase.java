/*
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
 * Copyright (c) 2000 - 2017 Hitachi Vantara and Contributors...
 * All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.testsupport.gold;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Before;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ClassicEngineCoreModule;
import org.pentaho.reporting.engine.classic.core.DefaultReportEnvironment;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.designtime.compat.CompatibilityUpdater;
import org.pentaho.reporting.engine.classic.core.layout.output.ReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml.XmlPageOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml.internal.XmlPageOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.FlowReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.StreamReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xml.XmlTableOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xml.internal.XmlTableOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugJndiContextFactoryBuilder;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.font.LocalFontRegistry;
import org.pentaho.reporting.libraries.base.config.ModifiableConfiguration;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.base.util.FilesystemFilter;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.MemoryByteArrayOutputStream;
import org.pentaho.reporting.libraries.base.util.StopWatch;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.naming.spi.NamingManager;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class GoldTestBase {
  public enum ReportProcessingMode {
    current( "gold" ), legacy( "gold-legacy" ), migration( "gold-migrated" );
    private String target;

    ReportProcessingMode( final String target ) {
      this.target = target;
    }

    public String getGoldDirectoryName() {
      return target;
    }
  }

  protected static class TestThreadFactory implements ThreadFactory {
    final AtomicInteger threadNumber = new AtomicInteger( 1 );

    public TestThreadFactory() {
    }

    public Thread newThread( final Runnable r ) {
      final Thread t = new Thread( r );
      t.setName( "Golden-Sample: " + getClass().getName() + "-" + threadNumber.getAndAdd( 1 ) );
      t.setDaemon( true );
      t.setPriority( 3 );
      return t;
    }
  }

  private class ExecuteReportRunner implements Runnable {
    private File reportFile;
    private File goldTemplate;
    private ReportProcessingMode processingMode;
    private List<Throwable> errors;

    private ExecuteReportRunner( final File reportFile, final File goldTemplate, final List<Throwable> errors,
        final ReportProcessingMode processingMode ) {
      this.reportFile = reportFile;
      this.goldTemplate = goldTemplate;
      this.processingMode = processingMode;
      this.errors = errors;
    }

    public void run() {
      try {
        System.out.printf( "Processing %s in mode=%s%n", reportFile, processingMode );
        GoldTestBase.this.run( reportFile, goldTemplate, processingMode );
        System.out.printf( "Finished   %s in mode=%s%n", reportFile, processingMode );
      } catch ( AssertionError e ) {
        errors.add( e );
      } catch ( Throwable t ) {
        String message = String.format( "Failed to process %s in mode %s", reportFile, processingMode ); // NON-NLS
        errors.add( new AssertionError( message, t ) );
      }
    }
  }

  private LocalFontRegistry localFontRegistry;

  public GoldTestBase() {
  }

  private static File checkMarkerExists( final String filename ) {
    final File file = new File( filename );
    if ( file.canRead() ) {
      return file;
    }
    return null;
  }

  public static File findMarker() {
    final ArrayList<String> positions = new ArrayList<String>();
    positions.add( "target/test-classes/test-gold/marker.properties" );
    for ( final String pos : positions ) {
      final File file = checkMarkerExists( pos );
      if ( file != null ) {
        return file.getAbsoluteFile().getParentFile();
      }
    }
    throw new IllegalStateException( "Cannot find marker, please run from the correct directory" );
  }

  public static MasterReport parseReport( final Object file ) throws ResourceException {
    final ResourceManager manager = new ResourceManager();
    manager.registerDefaults();
    final Resource resource = manager.createDirectly( file, MasterReport.class );
    return (MasterReport) resource.getResource();
  }

  public static File locateGoldenSampleReport( final String name ) {
    final FilesystemFilter filesystemFilter = new FilesystemFilter( name, "Reports" );
    final File marker = findMarker();
    final String[] directories = new String[] { "reports", "reports-4.0" };
    for ( int i = 0; i < directories.length; i++ ) {
      final String directory = directories[i];
      final File reports = new File( marker, directory );
      final File[] files = reports.listFiles( filesystemFilter );
      final HashSet<String> fileSet = new HashSet<String>();
      if ( files != null ) {
        for ( final File file : files ) {
          final String s = file.getName().toLowerCase();
          if ( fileSet.add( s ) == false ) {
            // the toy systems MacOS X and Windows use case-insensitive file systems and completely
            // mess up when there are two files with what they consider the same name.
            throw new IllegalStateException( "There is a golden sample with the same Windows/Mac "
                + "filename in the directory. Make sure your files are unique and lowercase." );
          }
        }

        for ( final File file : files ) {
          if ( file.isDirectory() ) {
            continue;
          }
          return file;
        }
      }

    }

    return null;
  }

  @Before
  public void setUp() throws Exception {
    Locale.setDefault( Locale.US );
    TimeZone.setDefault( TimeZone.getTimeZone( "UTC" ) );
    // enforce binary compatibility for the xml-files so that comparing them can be faster.

    ClassicEngineBoot.getInstance().start();
    if ( NamingManager.hasInitialContextFactoryBuilder() == false ) {
      NamingManager.setInitialContextFactoryBuilder( new DebugJndiContextFactoryBuilder() );
    }

    localFontRegistry = new LocalFontRegistry();
    localFontRegistry.initialize();
  }

  protected MasterReport tuneForTesting( final MasterReport report ) throws Exception {
    final ModifiableConfiguration configuration = report.getReportConfiguration();
    configuration.setConfigProperty( DefaultReportEnvironment.ENVIRONMENT_KEY + "::internal::report.date",
        "2011-04-07T15:00:00.000+0000" );
    configuration.setConfigProperty( DefaultReportEnvironment.ENVIRONMENT_TYPE + "::internal::report.date",
        "java.util.Date" );
    return report;
  }

  protected MasterReport tuneForLegacyMode( final MasterReport report ) {
    report.setCompatibilityLevel( ClassicEngineBoot.computeVersionId( 3, 8, 0 ) );
    return report;
  }

  protected MasterReport tuneForMigrationMode( final MasterReport report ) {
    final CompatibilityUpdater updater = new CompatibilityUpdater();
    updater.performUpdate( report );
    report.setAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.COMAPTIBILITY_LEVEL, null );
    return report;
  }

  protected MasterReport tuneForCurrentMode( final MasterReport report ) {
    report.setAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.COMAPTIBILITY_LEVEL, null );
    return report;
  }

  protected void run( final File file, final File gold, final ReportProcessingMode mode ) throws Exception {
    final MasterReport originalReport = parseReport( file );
    final MasterReport tunedReport = tuneForTesting( originalReport );
    MasterReport report = postProcess( tunedReport, file );
    if ( mode == ReportProcessingMode.legacy ) {
      report = tuneForLegacyMode( report );
    } else if ( mode == ReportProcessingMode.migration ) {
      report = tuneForMigrationMode( report );
    } else {
      report = tuneForCurrentMode( report );
    }

    final String fileName = IOUtils.getInstance().stripFileExtension( file.getName() );
    handleXmlContent( executePageable( report ), new File( gold, fileName + "-page.xml" ) );
    handleXmlContent( executeTableStream( report ), new File( gold, fileName + "-table-stream.xml" ) );
    handleXmlContent( executeTableFlow( report ), new File( gold, fileName + "-table-flow.xml" ) );
    handleXmlContent( executeTablePage( report ), new File( gold, fileName + "-table-page.xml" ) );
  }

  protected MasterReport postProcess( final MasterReport originalReport, final File file ) throws Exception {
    return postProcess( originalReport );
  }

  protected MasterReport postProcess( final MasterReport originalReport ) throws Exception {
    return originalReport;
  }

  protected void handleXmlContent( final byte[] reportOutput, final File goldSample ) throws Exception {
    final byte[] goldData;
    final InputStream goldInput = new BufferedInputStream( new FileInputStream( goldSample ) );
    final MemoryByteArrayOutputStream goldByteStream =
        new MemoryByteArrayOutputStream( Math.min( 1024 * 1024, (int) goldSample.length() ), 1024 * 1024 );

    try {
      IOUtils.getInstance().copyStreams( goldInput, goldByteStream );
      goldData = goldByteStream.toByteArray();
      if ( Arrays.equals( goldData, reportOutput ) ) {
        return;
      }
    } finally {
      goldInput.close();
    }

    final Reader reader = new InputStreamReader( new ByteArrayInputStream( goldData ), "UTF-8" );
    final ByteArrayInputStream inputStream = new ByteArrayInputStream( reportOutput );
    final Reader report = new InputStreamReader( inputStream, "UTF-8" );
    try {
      XMLAssert.assertXMLEqual( "File " + goldSample + " failed", new Diff( reader, report ), true );
    } catch ( AssertionFailedError afe ) {
      debugOutput( reportOutput, goldSample );
      throw afe;
    } finally {
      reader.close();
    }
  }

  private void debugOutput( final byte[] reportOutput, final File goldSample ) throws IOException {
    try {
      File testOutputFile = DebugReportRunner.createTestOutputFile();
      final FileOutputStream w =
          new FileOutputStream( new File( testOutputFile, "gold-failure-" + goldSample.getName() ) );
      try {
        w.write( reportOutput );
      } finally {
        w.close();
      }
    } catch ( IOException ioe ) {
      // ignored ..
      DebugLog.log( "Failed to write debug-output", ioe );
    }
  }

  protected byte[] executeTablePage( final MasterReport report ) throws IOException, ReportProcessingException {
    final MemoryByteArrayOutputStream outputStream = new MemoryByteArrayOutputStream();
    try {
      final XmlTableOutputProcessor outputProcessor =
          new XmlTableOutputProcessor( outputStream, new XmlTableOutputProcessorMetaData(
              XmlTableOutputProcessorMetaData.PAGINATION_FULL, localFontRegistry ) );
      final ReportProcessor streamReportProcessor = new PageableReportProcessor( report, outputProcessor );
      try {
        streamReportProcessor.processReport();
      } finally {
        streamReportProcessor.close();
      }
    } finally {
      outputStream.close();
    }
    return ( outputStream.toByteArray() );
  }

  protected byte[] executeTableFlow( final MasterReport report ) throws IOException, ReportProcessingException {
    final MemoryByteArrayOutputStream outputStream = new MemoryByteArrayOutputStream();
    try {
      final XmlTableOutputProcessor outputProcessor =
          new XmlTableOutputProcessor( outputStream, new XmlTableOutputProcessorMetaData(
              XmlTableOutputProcessorMetaData.PAGINATION_MANUAL, localFontRegistry ) );
      final ReportProcessor streamReportProcessor = new FlowReportProcessor( report, outputProcessor );
      try {
        streamReportProcessor.processReport();
      } finally {
        streamReportProcessor.close();
      }
    } finally {
      outputStream.close();
    }
    return ( outputStream.toByteArray() );
  }

  protected byte[] executePageable( final MasterReport report ) throws IOException, ReportProcessingException {
    final MemoryByteArrayOutputStream outputStream = new MemoryByteArrayOutputStream();
    try {
      final XmlPageOutputProcessor outputProcessor =
          new XmlPageOutputProcessor( outputStream, new XmlPageOutputProcessorMetaData( localFontRegistry ) );
      final PageableReportProcessor streamReportProcessor = new PageableReportProcessor( report, outputProcessor );
      try {
        streamReportProcessor.processReport();
      } finally {
        streamReportProcessor.close();
      }
    } finally {
      outputStream.close();
    }
    return ( outputStream.toByteArray() );
  }

  protected byte[] executeTableStream( final MasterReport report ) throws IOException, ReportProcessingException {
    final MemoryByteArrayOutputStream outputStream = new MemoryByteArrayOutputStream();
    try {
      final XmlTableOutputProcessor outputProcessor =
          new XmlTableOutputProcessor( outputStream, new XmlTableOutputProcessorMetaData(
              XmlTableOutputProcessorMetaData.PAGINATION_NONE, localFontRegistry ) );
      final ReportProcessor streamReportProcessor = new StreamReportProcessor( report, outputProcessor );
      try {
        streamReportProcessor.processReport();
      } finally {
        streamReportProcessor.close();
      }
    } finally {
      outputStream.close();
    }
    return ( outputStream.toByteArray() );
  }

  protected void initializeTestEnvironment() throws Exception {

  }

  protected void runAllGoldReports() throws Exception {
    if ( "true".equals( ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty(
        ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY ) ) ) {
      Assert.fail( "Dont run GoldenSample tests with the new layout system. These tests are not platform independent." );
    }

    final int numThreads =
        Math.max( 1, ClassicEngineBoot.getInstance().getExtendedConfig().getIntProperty(
            "org.pentaho.reporting.engine.classic.core.testsupport.gold.MaxWorkerThreads",
            Math.max( 1, Runtime.getRuntime().availableProcessors() - 1 ) ) );

    StopWatch w = new StopWatch();
    w.start();
    try {
      if ( numThreads == 1 ) {
        runAllGoldReportsSerial();
      } else {
        runAllGoldReportsInParallel( numThreads );
      }
    } finally {
      System.out.println( w.toString() );
    }
  }

  protected void runAllGoldReportsSerial() throws Exception {
    initializeTestEnvironment();

    List<Throwable> errors = Collections.synchronizedList( new ArrayList<Throwable>() );
    List<ExecuteReportRunner> reports = new ArrayList<ExecuteReportRunner>();
    reports.addAll( collectReports( "reports", ReportProcessingMode.legacy, errors ) );
    reports.addAll( collectReports( "reports", ReportProcessingMode.migration, errors ) );
    reports.addAll( collectReports( "reports", ReportProcessingMode.current, errors ) );
    reports.addAll( collectReports( "reports-4.0", ReportProcessingMode.migration, errors ) );
    reports.addAll( collectReports( "reports-4.0", ReportProcessingMode.current, errors ) );

    for ( ExecuteReportRunner report : reports ) {
      report.run();
    }
    if ( errors.isEmpty() == false ) {
      Log log = LogFactory.getLog( GoldTestBase.class );
      for ( Throwable throwable : errors ) {
        log.error( "Failed", throwable );
      }
      Assert.fail();
    }

    System.out.println( findMarker() );
  }

  protected void runAllGoldReportsInParallel( int threads ) throws Exception {
    initializeTestEnvironment();

    final List<Throwable> errors = Collections.synchronizedList( new ArrayList<Throwable>() );

    final ExecutorService threadPool =
        new ThreadPoolExecutor( threads, threads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
            new TestThreadFactory(), new ThreadPoolExecutor.AbortPolicy() );

    List<ExecuteReportRunner> reports = new ArrayList<ExecuteReportRunner>();
    reports.addAll( collectReports( "reports", ReportProcessingMode.legacy, errors ) );
    reports.addAll( collectReports( "reports", ReportProcessingMode.migration, errors ) );
    reports.addAll( collectReports( "reports", ReportProcessingMode.current, errors ) );
    reports.addAll( collectReports( "reports-4.0", ReportProcessingMode.migration, errors ) );
    reports.addAll( collectReports( "reports-4.0", ReportProcessingMode.current, errors ) );

    for ( ExecuteReportRunner report : reports ) {
      threadPool.submit( report );
    }

    threadPool.shutdown();
    while ( threadPool.isTerminated() == false ) {
      threadPool.awaitTermination( 5, TimeUnit.MINUTES );
    }
    if ( errors.isEmpty() == false ) {
      Log log = LogFactory.getLog( GoldTestBase.class );
      for ( Throwable throwable : errors ) {
        log.error( "Failed", throwable );
      }
      Assert.fail();
    }
  }

  private List<ExecuteReportRunner> collectReports( final String sourceDirectoryName, final ReportProcessingMode mode,
      final List<Throwable> errors ) throws Exception {
    final File marker = findMarker();
    final File reports = new File( marker, sourceDirectoryName );
    final File gold = new File( marker, mode.getGoldDirectoryName() );
    final FilenameFilter filter = createReportFilter();
    final File[] files = reports.listFiles( filter );

    if ( files == null ) {
      throw new IOException( "IO-Error while listing files for '" + reports + "'" );
    }

    final HashSet<String> fileSet = new HashSet<String>();
    for ( final File file : files ) {
      final String s = file.getName().toLowerCase();
      if ( fileSet.add( s ) == false ) {
        // the toy systems MacOS X and Windows use case-insensitive file systems and completely
        // mess up when there are two files with what they consider the same name.
        throw new IllegalStateException( "There is a golden sample with the same Windows/Mac "
            + "filename in the directory. Make sure your files are unique and lowercase." );
      }
    }

    List<ExecuteReportRunner> retval = new ArrayList<ExecuteReportRunner>();
    for ( final File file : files ) {
      if ( file.isDirectory() ) {
        continue;
      }

      try {
        retval.add( new ExecuteReportRunner( file, gold, errors, mode ) );
      } catch ( Throwable re ) {
        throw new Exception( "Failed at " + file, re );
      }
    }
    return retval;
  }

  protected void runSingleGoldReport( final String file, final ReportProcessingMode mode ) throws Exception {
    initializeTestEnvironment();

    final File marker = findMarker();
    final File gold = new File( marker, mode.getGoldDirectoryName() );

    try {
      final File reportFile = findReport( file );

      System.out.printf( "Processing %s in mode=%s%n", file, mode );
      run( reportFile, gold, mode );
      System.out.printf( "Finished   %s in mode=%s%n", file, mode );
    } catch ( Throwable re ) {
      throw new Exception( "Failed at " + file, re );
    }

    System.out.println( marker );
  }

  private File findReport( final String file ) throws FileNotFoundException {
    final File marker = findMarker();
    final File reports = new File( marker, "reports" );
    final File reportFile = new File( reports, file );
    if ( reportFile.exists() ) {
      return reportFile;
    }

    final File reports4 = new File( marker, "reports-4.0" );
    final File reportFile4 = new File( reports4, file );
    if ( reportFile4.exists() ) {
      return reportFile4;
    }

    throw new FileNotFoundException( file );
  }

  protected FilesystemFilter createReportFilter() {
    return new FilesystemFilter( new String[] { ".prpt", ".report", ".xml" }, "Reports", false );
  }
}
