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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.huge;

import java.io.IOException;
import java.net.URL;
import javax.swing.JComponent;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.engine.classic.demo.util.AbstractXmlDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * A demo handler, that shows how JFreeReport handles reports with a huge row count.
 *
 * @author Thomas Morgner
 */
public class VeryLargeReportDemo extends AbstractXmlDemoHandler
{
  private HugeLetterAndColorTableModel data;

  public VeryLargeReportDemo()
  {
    data = new HugeLetterAndColorTableModel();
  }

  public String getDemoName()
  {
    return "Very Large Report Demo";
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
    return ObjectUtilities.getResourceRelative("large-report.html", VeryLargeReportDemo.class);
  }

  public JComponent getPresentationComponent()
  {
    return createDefaultTable(data);
  }

  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative("large-report.xml", VeryLargeReportDemo.class);
  }


  /**
   * Entry point for running the demo application...
   *
   * @param args ignored.
   */
  public static void main(final String[] args) throws ReportDefinitionException, ReportProcessingException, IOException
  {
    // initialize JFreeReport
    ClassicEngineBoot.getInstance().start();

    final VeryLargeReportDemo handler = new VeryLargeReportDemo();
//    final SimpleDemoFrame frame = new SimpleDemoFrame(handler);
//    frame.init();
//    frame.pack();
//    LibSwingUtil.centerFrameOnScreen(frame);
//    frame.setVisible(true);

    PdfReportUtil.createPDF(handler.createReport(), "/tmp/report.pdf");
    HtmlReportUtil.createStreamHTML(handler.createReport(), "/tmp/report.html");

    long startPDF = System.currentTimeMillis();
    PdfReportUtil.createPDF(handler.createReport(), "/tmp/report.pdf");
    long endPDF = System.currentTimeMillis();

    long startHTML = System.currentTimeMillis();
    HtmlReportUtil.createStreamHTML(handler.createReport(), "/tmp/report.html");
    long endHTML = System.currentTimeMillis();

    System.out.println("PDF:  " + (endPDF - startPDF));
    System.out.println("HTML: " + (endHTML - startHTML));
  }
}
