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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.swingicons;

import java.io.IOException;
import java.net.URL;
import javax.swing.JComponent;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.demo.util.AbstractXmlDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * A demonstration application. <P> This demo is written up in the JFreeReport PDF Documentation.  Please notify David
 * Gilbert (david.gilbert@object-refinery.com) if you need to make changes to this file. <P> To run this demo, you need
 * to have the Java Look and Feel Icons jar file on your classpath.
 *
 * @author David Gilbert
 */
public class SwingIconsDemo extends AbstractXmlDemoHandler
{
  private SwingIconsDemoPanel demoPanel;

  /**
   * Constructs the demo application.
   */
  public SwingIconsDemo()
  {
    demoPanel = new SwingIconsDemoPanel();
  }

  public JComponent getPresentationComponent()
  {
    return demoPanel;
  }

  public String getDemoName()
  {
    return "Swing Icons Report";
  }

  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("swing-icons.html", SwingIconsDemo.class);
  }

  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("swing-icons.xml", SwingIconsDemo.class);
  }

  public MasterReport createReport() throws ReportDefinitionException
  {
    MasterReport report = parseReport();
    report.setDataFactory(new TableDataFactory
        ("default", demoPanel.getData()));
    return report;
  }

  /**
   * Entry point for running the demo application...
   *
   * @param args ignored.
   */
  public static void main(final String[] args) throws ReportProcessingException, IOException, ReportDefinitionException
  {
    // initialize JFreeReport
    ClassicEngineBoot.getInstance().start();

    final SwingIconsDemo handler = new SwingIconsDemo();

//    final SimpleDemoFrame frame = new SimpleDemoFrame(handler);
//    frame.init();
//    frame.pack();
//    RefineryUtilities.centerFrameOnScreen(frame);
//    frame.setVisible(true);
    //HtmlReportUtil.createZIPHTML(handler.createReport(), "/tmp/report.zip");
    final MasterReport report = handler.createReport();
    PdfReportUtil.createPDF(report, "/tmp/report.pdf");
    //ExcelReportUtil.createXLS(handler.createReport(), "/tmp/report.xls");
  }

}
