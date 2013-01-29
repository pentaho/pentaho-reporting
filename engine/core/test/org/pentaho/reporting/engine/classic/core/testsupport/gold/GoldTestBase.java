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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.TimeZone;
import javax.naming.spi.NamingManager;

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
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class GoldTestBase
{
  private LocalFontRegistry localFontRegistry;

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

  @Before
  public void setUp() throws Exception
  {
    Locale.setDefault(Locale.US);
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

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

  public static MasterReport parseReport(final Object file) throws ResourceException
  {
    final ResourceManager manager = new ResourceManager();
    manager.registerDefaults();
    final Resource resource = manager.createDirectly(file, MasterReport.class);
    return (MasterReport) resource.getResource();
  }

  protected void handleXmlContent(final byte[] reportOutput, final File goldSample) throws Exception
  {
    final Reader reader = new InputStreamReader(new FileInputStream(goldSample), "UTF-8");
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
    initializeTestEnvironment();
    
    processReports("reports", ReportProcessingMode.legacy);
    processReports("reports", ReportProcessingMode.migration);
    processReports("reports", ReportProcessingMode.current);
    processReports("reports-4.0", ReportProcessingMode.migration);
    processReports("reports-4.0", ReportProcessingMode.current);

    System.out.println(findMarker());
  }

  private void processReports(final String sourceDirectoryName, ReportProcessingMode mode) throws Exception
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
    if (reportFile.exists())
    {
      return reportFile4;
    }

    throw new FileNotFoundException(file);
  }

  protected FilesystemFilter createReportFilter()
  {
    return new FilesystemFilter(".prpt", "Reports");
  }

  public static File locateGoldenSampleReport(final String name)
  {
    final FilesystemFilter filesystemFilter = new FilesystemFilter(name, "Reports");
    final File marker = findMarker();
    final File reports = new File(marker, "reports");
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

    return null;
  }
}
