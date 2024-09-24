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

package org.pentaho.reporting.engine.classic.demo.features.interactivity;

import java.io.IOException;
import java.net.URL;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.demo.util.AbstractXmlDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.engine.classic.demo.util.SimpleDemoFrame;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class InteractiveSwingDemo extends AbstractXmlDemoHandler
{

  public InteractiveSwingDemo()
  {
  }

  public String getDemoName()
  {
    return "Interactive Swing-Demo (Unified File Format)";
  }

  public MasterReport createReport() throws ReportDefinitionException
  {
    try
    {
      final ResourceManager resourceManager = new ResourceManager();
      final Resource directly = resourceManager.createDirectly(getReportDefinitionSource(), MasterReport.class);
      return (MasterReport) directly.getResource();
    }
    catch (Exception rde)
    {
      throw new ReportDefinitionException("Failed", rde);
    }
  }

  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative("interactive-swing.html", InteractiveHtmlDemo.class);
  }

  public JComponent getPresentationComponent()
  {
    return new JPanel();
  }

  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative("interactivity.prpt", InteractiveHtmlDemo.class);
  }

  public static void main(String[] args)
      throws ResourceException, IOException,
      ReportProcessingException, ReportDefinitionException
  {
    ClassicEngineBoot.getInstance().start();

    final InteractiveHtmlDemo handler = new InteractiveHtmlDemo();
    handler.createReport();

    final SimpleDemoFrame frame = new SimpleDemoFrame(handler);
    frame.init();
    frame.pack();
    LibSwingUtil.centerFrameOnScreen(frame);
    frame.setVisible(true);
  }
}
