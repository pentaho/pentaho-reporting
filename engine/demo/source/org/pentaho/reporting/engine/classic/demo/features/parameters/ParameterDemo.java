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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.demo.features.parameters;

import java.io.IOException;
import java.net.URL;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.demo.util.AbstractXmlDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.engine.classic.demo.util.SimpleDemoFrame;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.repository.ContentIOException;

/**
 * Creation-Date: Feb 21, 2007, 4:01:57 PM
 *
 * @author Thomas Morgner
 */
public class ParameterDemo extends AbstractXmlDemoHandler
{
  public ParameterDemo()
  {
  }

  /**
   * Returns the display name of the demo.
   *
   * @return the name.
   */
  public String getDemoName()
  {
    return "Parameter Definition Demo (Unified File Format)";
  }

  /**
   * Creates the report. For XML reports, this will most likely call the ReportGenerator, while API reports may use this
   * function to build and return a new, fully initialized report object.
   *
   * @return the fully initialized JFreeReport object.
   * @throws ReportDefinitionException if an error occured preventing the report definition.
   */
  public MasterReport createReport() throws ReportDefinitionException
  {
    return parseReport();
  }

  /**
   * Returns the URL of the XML definition for this report.
   *
   * @return the URL of the report definition.
   */
  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("parameters.prpt", ParameterDemo.class);
  }

  /**
   * Returns the URL of the HTML document describing this demo.
   *
   * @return the demo description.
   */
  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("parameters.html", ParameterDemo.class);
  }

  /**
   * Returns the presentation component for this demo. This component is shown before the real report generation is
   * started. Ususally it contains a JTable with the demo data and/or input components, which allow to configure the
   * report.
   *
   * @return the presentation component, never null.
   */
  public JComponent getPresentationComponent()
  {
    return new JPanel();
  }

  public static void main(String[] args) throws ReportProcessingException, IOException, ReportDefinitionException, ContentIOException, BundleWriterException
  {
    ClassicEngineBoot.getInstance().start();

    final ParameterDemo handler = new ParameterDemo();
    final SimpleDemoFrame frame = new SimpleDemoFrame(handler);
    frame.init();
    frame.pack();
    LibSwingUtil.centerFrameOnScreen(frame);
    frame.setVisible(true);
  }
}
