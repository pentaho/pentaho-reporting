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
 * Copyright (c) 2000 - 2011 Pentaho Corporation and Contributors...  
 * All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.testsupport.gold;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import javax.naming.spi.NamingManager;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Before;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
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

public class GoldTestBase
{
  public enum ReportProcessingMode
  {
    current("gold"), legacy("gold-legacy"), migration("gold-migrated");
    private String target;

    ReportProcessingMode(final String target)
    {
      this.target = target;
    }

    public String getGoldDirectoryName()
    {
      return target;
    }
  }

  protected static class TestThreadFactory implements ThreadFactory
  {
    final AtomicInteger threadNumber = new AtomicInteger(1);

    public TestThreadFactory()
    {
    }

    public Thread newThread(final Runnable r)
    {
      final Thread t = new Thread(r);
      t.setName("Golden-Sample: " + getClass().getName() + "-" + threadNumber.getAndAdd(1));
      t.setDaemon(true);
      t.setPriority(3);
      return t;
    }
  }

  private class ExecuteReportRunner implements Runnable
  {
    private String directory;
    private ReportProcessingMode processingMode;
    private List<Throwable> errors;

    private ExecuteReportRunner(final String directory,
                                final ReportProcessingMode processingMode,
                                final List<Throwable> errors)
    {
      this.directory = directory;
      this.processingMode = processingMode;
      this.errors = errors;
    }

    public void run()
    {
      try
      {
        processReports(directory, processingMode);
      }
      catch (Throwable t)
      {
        errors.add(t);
      }
    }
  }

  private LocalFontRegistry localFontRegistry;

  public GoldTestBase()
  {
  }

  private static File checkMarkerExists(final String filename)
  {
    final File file = new File(filename);
    if (file.canRead())
    {
      return file;
    }
    return null;
  }

  public static File findMarker()
  {
    final ArrayList<String> positions = new ArrayList<String>();
    positions.add("test-gold/marker.properties");
    for (final String pos : positions)
    {
      final File file = checkMarkerExists(pos);
      if (file != null)
      {
        return file.getAbsoluteFile().getParentFile();
      }
    }
    throw new IllegalStateException("Cannot find marker, please run from the correct directory");
  }

  public static MasterReport parseReport(final Object file) throws ResourceException
  {
    final ResourceManager manager = new ResourceManager();
    manager.registerDefaults();
    final Resource resource = manager.createDirectly(file, MasterReport.class);
    return (MasterReport) resource.getResource();
  }

  public static File locateGoldenSampleReport(final String name)
  {
    final FilesystemFilter filesystemFilter = new FilesystemFilter(name, "Reports");
    final File marker = findMarker();
    final String[] directories = new String[]{"reports", "reports-4.0"};
    for (int i = 0; i < directories.length; i++)
    {
      final String directory = directories[i];
      final File reports = new File(marker, directory);
      final File[] files = reports.listFiles(filesystemFilter);
      final HashSet<String> fileSet = new HashSet<String>();
      for (final File file : files)
      {
        final String s = file.getName().toLowerCase();
        if (fileSet.add(s) == false)
        {
          // the toy systems MacOS X and Windows use case-insensitive file systems and completely
          // mess up when there are two files with what they consider the same name.
          throw new IllegalStateException("There is a golden sample with the same Windows/Mac " +
              "filename in the directory. Make sure your files are unique and lowercase.");
        }
      }

      for (final File file : files)
      {
        if (file.isDirectory())
        {
          continue;
        }
        return file;
      }

    }

    return null;
  }

  @Before
  public void setUp() throws Exception
  {
    Locale.setDefault(Locale.US);
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    // enforce binary compatibility for the xml-files so that comparing them can be faster.

    ClassicEngineBoot.getInstance().start();
    if (NamingManager.hasInitialContextFactoryBuilder() == false)
    {
      NamingManager.setInitialContextFactoryBuilder(new DebugJndiContextFactoryBuilder());
    }

    localFontRegistry = new LocalFontRegistry();
    localFontRegistry.initialize();
  }

  protected MasterReport tuneForTesting(final MasterReport report) throws Exception
  {
    final ModifiableConfiguration configuration = report.getReportConfiguration();
    configuration.setConfigProperty
        (DefaultReportEnvironment.ENVIRONMENT_KEY + "::internal::report.date", "2011-04-07T15:00:00.000+0000");
    configuration.setConfigProperty
        (DefaultReportEnvironment.ENVIRONMENT_TYPE + "::internal::report.date", "java.util.Date");
    return report;
  }

  protected MasterReport tuneForLegacyMode(final MasterReport report)
  {
    report.setCompatibilityLevel(ClassicEngineBoot.computeVersionId(3, 8, 0));
    return report;
  }

  protected MasterReport tuneForMigrationMode(final MasterReport report)
  {
    final CompatibilityUpdater updater = new CompatibilityUpdater();
    updater.performUpdate(report);
    report.setAttribute(AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.COMAPTIBILITY_LEVEL, null);
    return report;
  }

  protected MasterReport tuneForCurrentMode(final MasterReport report)
  {
    report.setAttribute(AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.COMAPTIBILITY_LEVEL, null);
    return report;
  }

  protected void run(final File file, final File gold, final ReportProcessingMode mode)
      throws Exception
  {
    final MasterReport originalReport = parseReport(file);
    final MasterReport tunedReport = tuneForTesting(originalReport);
    MasterReport report = postProcess(tunedReport);
    if (mode == ReportProcessingMode.legacy)
    {
      report = tuneForLegacyMode(report);
    }
    else if (mode == ReportProcessingMode.migration)
    {
      report = tuneForMigrationMode(report);
    }
    else
    {
      report = tuneForCurrentMode(report);
    }

    final String fileName = IOUtils.getInstance().stripFileExtension(file.getName());
    handleXmlContent(executeTableStream(report), new File(gold, fileName + "-table-stream.xml"));
    handleXmlContent(executeTableFlow(report), new File(gold, fileName + "-table-flow.xml"));
    handleXmlContent(executeTablePage(report), new File(gold, fileName + "-table-page.xml"));
    handleXmlContent(executePageable(report), new File(gold, fileName + "-page.xml"));
  }

  protected MasterReport postProcess(final MasterReport originalReport) throws Exception
  {
    return originalReport;
  }

  protected void handleXmlContent(final byte[] reportOutput, final File goldSample) throws Exception
  {
    final byte[] goldData;
    final InputStream goldInput = new BufferedInputStream(new FileInputStream(goldSample));
    final MemoryByteArrayOutputStream goldByteStream =
        new MemoryByteArrayOutputStream(Math.min(1024 * 1024, (int) goldSample.length()), 1024 * 1024);

    try
    {
      IOUtils.getInstance().copyStreams(goldInput, goldByteStream);
      goldData = goldByteStream.toByteArray();
      if (Arrays.equals(goldData, reportOutput))
      {
        return;
      }
    }
    finally
    {
      goldInput.close();
    }

    final Reader reader = new InputStreamReader(new ByteArrayInputStream(goldData), "UTF-8");
    final ByteArrayInputStream inputStream = new ByteArrayInputStream(reportOutput);
    final Reader report = new InputStreamReader(inputStream, "UTF-8");
    try
    {
      XMLAssert.assertXMLEqual("File " + goldSample + " failed", new Diff(reader, report), true);
    }
    catch (AssertionFailedError afe)
    {
      DebugLog.log(new String(reportOutput, "UTF-8"));
      throw afe;
    }
    finally
    {
      reader.close();
    }
  }

  protected byte[] executeTablePage(final MasterReport report)
      throws IOException, ReportProcessingException
  {
    final MemoryByteArrayOutputStream outputStream = new MemoryByteArrayOutputStream();
    try
    {
      final XmlTableOutputProcessor outputProcessor =
          new XmlTableOutputProcessor(outputStream, new XmlTableOutputProcessorMetaData(
              XmlTableOutputProcessorMetaData.PAGINATION_FULL, localFontRegistry));
      final ReportProcessor streamReportProcessor = new PageableReportProcessor(report, outputProcessor);
      try
      {
        streamReportProcessor.processReport();
      }
      finally
      {
        streamReportProcessor.close();
      }
    }
    finally
    {
      outputStream.close();
    }
    return (outputStream.toByteArray());
  }

  protected byte[] executeTableFlow(final MasterReport report)
      throws IOException, ReportProcessingException
  {
    final MemoryByteArrayOutputStream outputStream = new MemoryByteArrayOutputStream();
    try
    {
      final XmlTableOutputProcessor outputProcessor =
          new XmlTableOutputProcessor(outputStream, new XmlTableOutputProcessorMetaData(
              XmlTableOutputProcessorMetaData.PAGINATION_MANUAL, localFontRegistry));
      final ReportProcessor streamReportProcessor = new FlowReportProcessor(report, outputProcessor);
      try
      {
        streamReportProcessor.processReport();
      }
      finally
      {
        streamReportProcessor.close();
      }
    }
    finally
    {
      outputStream.close();
    }
    return (outputStream.toByteArray());
  }

  protected byte[] executePageable(final MasterReport report)
      throws IOException, ReportProcessingException
  {
    final MemoryByteArrayOutputStream outputStream = new MemoryByteArrayOutputStream();
    try
    {
      final XmlPageOutputProcessor outputProcessor = new XmlPageOutputProcessor
          (outputStream, new XmlPageOutputProcessorMetaData(localFontRegistry));
      final PageableReportProcessor streamReportProcessor =
          new PageableReportProcessor(report, outputProcessor);
      try
      {
        streamReportProcessor.processReport();
      }
      finally
      {
        streamReportProcessor.close();
      }
    }
    finally
    {
      outputStream.close();
    }
    return (outputStream.toByteArray());
  }

  protected byte[] executeTableStream(final MasterReport report)
      throws IOException, ReportProcessingException
  {
    final MemoryByteArrayOutputStream outputStream = new MemoryByteArrayOutputStream();
    try
    {
      final XmlTableOutputProcessor outputProcessor =
          new XmlTableOutputProcessor(outputStream, new XmlTableOutputProcessorMetaData(
              XmlTableOutputProcessorMetaData.PAGINATION_NONE, localFontRegistry));
      final ReportProcessor streamReportProcessor = new StreamReportProcessor(report, outputProcessor);
      try
      {
        streamReportProcessor.processReport();
      }
      finally
      {
        streamReportProcessor.close();
      }
    }
    finally
    {
      outputStream.close();
    }
    return (outputStream.toByteArray());
  }

  protected void initializeTestEnvironment() throws Exception
  {

  }

  protected void runAllGoldReports() throws Exception
  {
    final int numThreads = Math.max(1, ClassicEngineBoot.getInstance().getExtendedConfig().getIntProperty
        ("org.pentaho.reporting.engine.classic.core.testsupport.gold.MaxWorkerThreads", 3));

    StopWatch w = new StopWatch();
    w.start();
    try
    {
      if (numThreads == 1)
      {
        runAllGoldReportsSerial();
      }
      else
      {
        runAllGoldReportsInParallel(numThreads);
      }
    }
    finally
    {
      System.out.println(w.toString());
    }
  }

  protected void runAllGoldReportsSerial() throws Exception
  {
    initializeTestEnvironment();

    processReports("reports", ReportProcessingMode.legacy);
    processReports("reports", ReportProcessingMode.migration);
    processReports("reports", ReportProcessingMode.current);
    processReports("reports-4.0", ReportProcessingMode.migration);
    processReports("reports-4.0", ReportProcessingMode.current);

    System.out.println(findMarker());
  }

  protected void runAllGoldReportsInParallel(int threads) throws Exception
  {
    initializeTestEnvironment();

    final List<Throwable> errors = Collections.synchronizedList(new ArrayList<Throwable>());

    final ExecutorService threadPool = new ThreadPoolExecutor(threads, threads,
        0L, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<Runnable>(),
        new TestThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

    threadPool.submit(new ExecuteReportRunner("reports", ReportProcessingMode.legacy, errors));
    threadPool.submit(new ExecuteReportRunner("reports", ReportProcessingMode.current, errors));
    threadPool.submit(new ExecuteReportRunner("reports", ReportProcessingMode.migration, errors));
    threadPool.submit(new ExecuteReportRunner("reports-4.0", ReportProcessingMode.current, errors));
    threadPool.submit(new ExecuteReportRunner("reports-4.0", ReportProcessingMode.migration, errors));

    threadPool.shutdown();
    while (threadPool.isTerminated() == false)
    {
      threadPool.awaitTermination(5, TimeUnit.MINUTES);
    }
    if (errors.isEmpty() == false)
    {
      for (int i = 0; i < errors.size(); i++)
      {
        final Throwable throwable = errors.get(i);
        throwable.printStackTrace();
      }
      Assert.fail();
    }
  }

  private void processReports(final String sourceDirectoryName, final ReportProcessingMode mode) throws Exception
  {
    final File marker = findMarker();
    final File reports = new File(marker, sourceDirectoryName);
    final File gold = new File(marker, mode.getGoldDirectoryName());
    final FilenameFilter filter = createReportFilter();
    final File[] files = reports.listFiles(filter);

    if (files == null)
    {
      throw new IOException("IO-Error while listing files for '" + reports + "'");
    }

    final HashSet<String> fileSet = new HashSet<String>();
    for (final File file : files)
    {
      final String s = file.getName().toLowerCase();
      if (fileSet.add(s) == false)
      {
        // the toy systems MacOS X and Windows use case-insensitive file systems and completely
        // mess up when there are two files with what they consider the same name.
        throw new IllegalStateException("There is a golden sample with the same Windows/Mac " +
            "filename in the directory. Make sure your files are unique and lowercase.");
      }
    }

    for (final File file : files)
    {
      if (file.isDirectory())
      {
        continue;
      }

      try
      {
        System.out.printf("Processing %s in mode=%s%n", file, mode);
        run(file, gold, mode);
        System.out.printf("Finished   %s in mode=%s%n", file, mode);
      }
      catch (Throwable re)
      {
        throw new Exception("Failed at " + file, re);
      }
    }

  }

  protected void runSingleGoldReport(final String file, final ReportProcessingMode mode) throws Exception
  {
    initializeTestEnvironment();

    final File marker = findMarker();
    final File gold = new File(marker, mode.getGoldDirectoryName());

    try
    {
      final File reportFile = findReport(file);

      System.out.printf("Processing %s in mode=%s%n", file, mode);
      run(reportFile, gold, mode);
      System.out.printf("Finished   %s in mode=%s%n", file, mode);
    }
    catch (Throwable re)
    {
      throw new Exception("Failed at " + file, re);
    }

    System.out.println(marker);
  }

  private File findReport(final String file) throws FileNotFoundException
  {
    final File marker = findMarker();
    final File reports = new File(marker, "reports");
    final File reportFile = new File(reports, file);
    if (reportFile.exists())
    {
      return reportFile;
    }

    final File reports4 = new File(marker, "reports-4.0");
    final File reportFile4 = new File(reports4, file);
    if (reportFile4.exists())
    {
      return reportFile4;
    }

    throw new FileNotFoundException(file);
  }

  protected FilesystemFilter createReportFilter()
  {
    return new FilesystemFilter(".prpt", "Reports");
  }
}
