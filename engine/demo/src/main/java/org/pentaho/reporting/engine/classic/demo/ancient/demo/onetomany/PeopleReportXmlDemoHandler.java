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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.onetomany;

import java.io.IOException;
import java.net.URL;
import javax.swing.JComponent;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.demo.util.AbstractXmlDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.engine.classic.demo.util.SimpleDemoFrame;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

/**
 * Creation-Date: 27.08.2005, 13:12:03
 *
 * @author Thomas Morgner
 */
public class PeopleReportXmlDemoHandler extends AbstractXmlDemoHandler
{
  private PeopleReportTableModel tableModel;

  public PeopleReportXmlDemoHandler()
  {
    tableModel = new PeopleReportTableModel();
  }

  public String getDemoName()
  {
    return "One-To-Many-Elements Reports Demo (XML-Version)";
  }

  public MasterReport createReport() throws ReportDefinitionException
  {
    MasterReport report = parseReport();
    report.setDataFactory(new TableDataFactory
        ("default", tableModel));
    return report;
  }

  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("people-xml.html", PeopleReportXmlDemoHandler.class);
  }

  public JComponent getPresentationComponent()
  {
    return createDefaultTable(tableModel);
  }

  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("joined-report.xml", PeopleReportXmlDemoHandler.class);
  }


  /**
   * Entry point for running the demo application...
   *
   * @param args ignored.
   */
  public static void main(final String[] args)
      throws ReportDefinitionException, ReportProcessingException, IOException
  {
    // initialize JFreeReport
    ClassicEngineBoot.getInstance().start();
    final PeopleReportXmlDemoHandler handler = new PeopleReportXmlDemoHandler();

    final SimpleDemoFrame frame = new SimpleDemoFrame(handler);
    frame.init();
    frame.pack();
    LibSwingUtil.centerFrameOnScreen(frame);
    frame.setVisible(true);
//    HtmlReportUtil.createDirectoryHTML(handler.createReport(), "/tmp/report.html");
  }
}

