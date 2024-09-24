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
import java.io.FileOutputStream;
import java.net.URL;
import javax.swing.table.TableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.PageableTextOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.driver.TextFilePrinterDriver;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.opensource.OpenSourceProjects;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;

/**
 * A demonstration that shows how to generate a report and save it to PDF without displaying the print preview or the
 * PDF save-as dialog.
 *
 * @author David Gilbert
 */
public class StraightToPlainText
{
  private static final Log logger = LogFactory.getLog(StraightToPlainText.class);

  /**
   * Creates a new demo application.
   *
   * @param filename the output filename.
   * @throws ParseException if the report could not be parsed.
   */
  public StraightToPlainText(final String filename)
      throws ParseException
  {
    final URL in = ObjectUtilities.getResource
        ("org/pentaho/reporting/engine/classic/demo/opensource/opensource.xml", StraightToPlainText.class);
    final MasterReport report = parseReport(in);
    final TableModel data = new OpenSourceProjects();
    report.setDataFactory(new TableDataFactory
        ("default", data));
    savePlainText(report, filename);
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
      final MasterReport report = (MasterReport) resource.getResource();
      // plain text does not support images, so we do not care about the logo ..
      report.getParameterValues().put("logo", null);
      return report;
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
  public boolean savePlainText(final MasterReport report, final String fileName)
  {
    try
    {
      final BufferedOutputStream fout = new BufferedOutputStream
          (new FileOutputStream(fileName));

      // cpi = 15, lpi = 10
      final TextFilePrinterDriver pc = new TextFilePrinterDriver(fout, 15, 10);

      final PageableTextOutputProcessor outputProcessor = new PageableTextOutputProcessor(pc,
          report.getConfiguration());
      final PageableReportProcessor proc = new PageableReportProcessor(report, outputProcessor);
      proc.processReport();
      proc.close();
      fout.close();
      return true;
    }
    catch (Exception e)
    {
      logger.error("Writing PlainText failed.", e);
      return false;
    }
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
      //final StraightToPDF demo =
      new StraightToPlainText(System.getProperty("user.home") + "/OpenSource-Demo.txt");
      System.exit(0);
    }
    catch (Exception e)
    {
      logger.error("Failed to run demo", e);
      System.exit(1);
    }
  }

}
