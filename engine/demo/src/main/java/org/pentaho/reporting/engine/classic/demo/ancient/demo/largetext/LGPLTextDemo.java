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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.largetext;

import java.io.IOException;
import java.net.URL;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.engine.classic.demo.util.AbstractXmlDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * A simple JFreeReport demonstration.  The generated report contains the complete text of the LGPL.
 *
 * @author Thomas Morgner
 */
public class LGPLTextDemo extends AbstractXmlDemoHandler
{

  /**
   * Constructs the demo application.
   */
  public LGPLTextDemo()
  {
  }

  public MasterReport createReport() throws ReportDefinitionException
  {
    return parseReport();
  }

  public String getDemoName()
  {
    return "Large Text Demo";
  }

  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative("lgpl.html", LGPLTextDemo.class);
  }

  public JComponent getPresentationComponent()
  {
    return new JPanel();
  }

  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative("lgpl.xml", LGPLTextDemo.class);
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

    final LGPLTextDemo handler = new LGPLTextDemo();
    HtmlReportUtil.createDirectoryHTML(handler.createReport(), "/tmp/report.html");
//    final SimpleDemoFrame frame = new SimpleDemoFrame(handler);
//    frame.init();
//    frame.pack();
//    RefineryUtilities.centerFrameOnScreen(frame);
//    frame.setVisible(true);
  }

}
