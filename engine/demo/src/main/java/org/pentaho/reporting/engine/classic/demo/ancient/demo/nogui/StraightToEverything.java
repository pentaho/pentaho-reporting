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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.demo.ancient.demo.nogui;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import javax.swing.table.TableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.PageableTextOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.driver.TextFilePrinterDriver;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.StreamReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.csv.StreamCSVOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.RTFReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.opensource.OpenSourceProjects;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;

/**
 * A demonstration that shows how to generate a report and save it to PDF without displaying the print preview or the
 * PDF save-as dialog. The methods to save the report to the various file formats are also implemented in
 *
 * @author Thomas Morgner
 */
public class StraightToEverything
{
  private static final Log logger = LogFactory.getLog(StraightToEverything.class);

  /**
   * Creates a new demo application.
   *
   * @param filename the output filename.
   * @throws ParseException if the report could not be parsed.
   */
  public StraightToEverything(final String filename)
      throws ParseException
  {
    final URL in = ObjectUtilities.getResource
        ("org/pentaho/reporting/engine/classic/demo/opensource/opensource.xml", StraightToEverything.class);
    final MasterReport report = parseReport(in);
    final TableModel data = new OpenSourceProjects();
    report.setDataFactory(new TableDataFactory
        ("default", data));
    try
    {
      createPDF(report, filename + ".pdf");
      createCSV(report, filename + ".csv");
      createDirectoryHTML(report, filename + ".html");
      createPlainText(report, filename + ".txt");
      createRTF(report, filename + ".rtf");
      createStreamHTML(report, filename + "-single-file.html");
      createXLS(report, filename + ".xls");
      createZIPHTML(report, filename + ".zip");
    }
    catch (Exception e)
    {
      logger.error("Failed to write report", e);
    }
  }

  /**
   * Reads the report from the specified template file.
   *
   * @param templateURL the template location.
   * @return a report.
   * @throws ParseException if the report could not be parsed.
   */
  private MasterReport parseReport(final URL templateURL)
      throws ParseException
  {
    try
    {
      final ResourceManager mgr = new ResourceManager();
      final Resource resource = mgr.createDirectly(templateURL, MasterReport.class);
      return (MasterReport) resource.getResource();
    }
    catch (Exception e)
    {
      throw new ParseException("Failed to parse the report", e);
    }
  }

  /**
   * Saves a report to PDF format.
   *
   * @param report   the report.
   * @param fileName target file name.
   * @return true or false.
   */
  public static boolean createPDF(final MasterReport report, final String fileName)
  {
    OutputStream out = null;
    try
    {
      out = new BufferedOutputStream(new FileOutputStream(new File(fileName)));
      final PdfOutputProcessor outputProcessor = new PdfOutputProcessor(report.getConfiguration(), out,
          report.getResourceManager());
      final PageableReportProcessor proc = new PageableReportProcessor(report, outputProcessor);
      proc.processReport();
      proc.close();
      out.close();
      return true;
    }
    catch (Exception e)
    {
      logger.error("Writing PDF failed.", e);
      return false;
    }
    finally
    {
      try
      {
        if (out != null)
        {
          out.close();
        }
      }
      catch (Exception e)
      {
        logger.error("Saving PDF failed.", e);
      }
    }
  }

  /**
   * Saves a report to plain text format.
   *
   * @param report   the report.
   * @param filename target file name.
   * @throws Exception if an error occurs.
   */
  public static void createPlainText(final MasterReport report, final String filename)
      throws Exception
  {
    final OutputStream fout = new BufferedOutputStream(new FileOutputStream(filename));
    // cpi = 15, lpi = 10
    final TextFilePrinterDriver pc = new TextFilePrinterDriver(fout, 15, 10);

    final PageableTextOutputProcessor outputProcessor = new PageableTextOutputProcessor(pc, report.getConfiguration());
    final PageableReportProcessor proc = new PageableReportProcessor(report, outputProcessor);
    proc.processReport();
    proc.close();
    fout.close();
  }

  /**
   * Saves a report to rich-text format (RTF).
   *
   * @param report   the report.
   * @param filename target file name.
   * @throws Exception if an error occurs.
   */
  public static void createRTF(final MasterReport report, final String filename)
      throws Exception
  {
    RTFReportUtil.createRTF(report, filename);
  }

  /**
   * Saves a report to CSV format.
   *
   * @param report   the report.
   * @param filename target file name.
   * @throws Exception if an error occurs.
   */
  public static void createCSV(final MasterReport report, final String filename)
      throws Exception
  {

    final OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(filename));
    final StreamCSVOutputProcessor target = new StreamCSVOutputProcessor(outputStream);

    final StreamReportProcessor reportProcessor = new StreamReportProcessor(report, target);
    reportProcessor.processReport();
    outputStream.close();
  }

  /**
   * Saves a report to Excel format.
   *
   * @param report   the report.
   * @param filename target file name.
   * @throws Exception if an error occurs.
   */
  public static void createXLS(final MasterReport report, final String filename)
      throws Exception
  {
    ExcelReportUtil.createXLS(report, filename);
  }

  /**
   * Saves a report into a single HTML format.
   *
   * @param report   the report.
   * @param filename target file name.
   * @throws Exception if an error occurs.
   */
  public static void createStreamHTML(final MasterReport report, final String filename)
      throws Exception
  {
    HtmlReportUtil.createStreamHTML(report, filename);
  }

  /**
   * Saves a report to HTML. The HTML file is stored in a directory.
   *
   * @param report   the report.
   * @param filename target file name.
   * @throws Exception if an error occurs.
   */
  public static void createDirectoryHTML(final MasterReport report,
                                         final String filename)
      throws Exception
  {
    HtmlReportUtil.createDirectoryHTML(report, filename);
  }

  /**
   * Saves a report in a ZIP file. The zip file contains a HTML document.
   *
   * @param report   the report.
   * @param filename target file name.
   * @throws Exception if an error occurs.
   */
  public static void createZIPHTML(final MasterReport report, final String filename)
      throws Exception
  {
    HtmlReportUtil.createZIPHTML(report, filename);
  }

  /**
   * Demo starting point.
   *
   * @param args ignored.
   */
  public static void main(final String[] args)
  {
    ClassicEngineBoot.getInstance().start();
    try
    {
      final String folder;
      if (args.length == 0)
      {
        folder = System.getProperty("user.home");
      }
      else
      {
        folder = args[0];
      }
      //final StraightToEverything demo =
      new StraightToEverything(folder + "/OpenSource-Demo");
      System.exit(0);
    }
    catch (Exception e)
    {
      logger.error("Failed to run demo", e);
      System.exit(1);
    }
  }

}
