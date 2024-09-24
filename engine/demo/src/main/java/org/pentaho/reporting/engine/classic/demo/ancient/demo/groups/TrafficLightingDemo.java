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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.groups;

import java.io.IOException;
import java.net.URL;
import javax.swing.JComponent;
import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.demo.util.AbstractXmlDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.engine.classic.demo.util.SimpleDemoFrame;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.repository.ContentIOException;

/**
 * This demo shows how to define nested groups.
 *
 * @author Thomas Morgner
 */
public class TrafficLightingDemo extends AbstractXmlDemoHandler
{
  private TableModel data;

  public TrafficLightingDemo()
  {
    data = new ColorAndLetterTableModel();
  }

  public String getDemoName()
  {
    return "Traffic-Lighting";
  }

  public MasterReport createReport() throws ReportDefinitionException
  {
    final MasterReport report = parseReport();
    report.setDataFactory(new TableDataFactory
        ("default", data));
    return report;
  }

  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative("trafficlighting.html", TrafficLightingDemo.class);
  }

  public JComponent getPresentationComponent()
  {
    return createDefaultTable(data);
  }

  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative("trafficlighting.xml", TrafficLightingDemo.class);
  }


  /**
   * Entry point for running the demo application...
   *
   * @param args ignored.
   */
  public static void main(final String[] args)
      throws ReportProcessingException,
      IOException, ReportDefinitionException, ContentIOException, BundleWriterException
  {
    // initialize JFreeReport
    ClassicEngineBoot.getInstance().start();

    final TrafficLightingDemo handler = new TrafficLightingDemo();
    final MasterReport report = handler.createReport();
//
//    try
//    {
//      final File file = new File("/tmp/report-out");
//      file.mkdirs();
//      BundleWriter.writeReportToDirectory(report, file);
//    }
//    catch (Exception e)
//    {
//      // ignore
//    }

    final SimpleDemoFrame frame = new SimpleDemoFrame(handler);
    frame.init();
    frame.pack();
    LibSwingUtil.centerFrameOnScreen(frame);
    frame.setVisible(true);
//    PdfReportUtil.createPDF(report, new NullOutputStream());
    //HtmlReportUtil.createStreamHTML(handler.createReport(), "/tmp/groups.html");
//    ExcelReportUtil.createXLS(handler.createReport(), "/tmp/groups.xls");
  }
}
