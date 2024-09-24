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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.opensource;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;
import javax.swing.JComponent;
import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.engine.classic.demo.util.AbstractXmlDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.WaitingImageObserver;

/**
 * A simple JFreeReport demonstration.  The generated report lists some free and open source software projects for the
 * Java programming language.
 *
 * @author David Gilbert
 */
public class OpenSourceXMLDemoHandler extends AbstractXmlDemoHandler
{
  /**
   * The data for the report.
   */
  private TableModel data;

  /**
   * Constructs the demo application.
   */
  public OpenSourceXMLDemoHandler()
  {
    this.data = new OpenSourceProjects();
  }

  public String getDemoName()
  {
    return "Open Source Demo (XML)";
  }

  public MasterReport createReport() throws ReportDefinitionException
  {
    MasterReport report = parseReport();
    report.setDataFactory(new TableDataFactory
        ("default", data));

    final URL imageURL = ObjectUtilities.getResourceRelative
        ("gorilla.jpg", OpenSourceXMLDemoHandler.class);
    final Image image = Toolkit.getDefaultToolkit().createImage(imageURL);
    final WaitingImageObserver obs = new WaitingImageObserver(image);
    obs.waitImageLoaded();
    report.getParameterValues().put("logo", image);
    return report;
  }

  public JComponent getPresentationComponent()
  {
    return createDefaultTable(data);
  }

  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("opensource-xml.html", OpenSourceXMLDemoHandler.class);
  }

  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("opensource.xml", OpenSourceXMLDemoHandler.class);
  }

  public static void main(final String[] args)
      throws ReportDefinitionException, ReportProcessingException, IOException
  {
    ClassicEngineBoot.getInstance().start();
    final OpenSourceXMLDemoHandler handler = new OpenSourceXMLDemoHandler();
//    PdfReportUtil.createPDF(handler.createReport(), "/tmp/report.pdf");

    HtmlReportUtil.createDirectoryHTML(handler.createReport(), "/tmp/report.html");


  }
}
