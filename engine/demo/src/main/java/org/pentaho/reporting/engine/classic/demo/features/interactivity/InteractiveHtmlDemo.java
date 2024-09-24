/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.engine.classic.demo.features.interactivity;

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
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class InteractiveHtmlDemo extends AbstractXmlDemoHandler
{

  public InteractiveHtmlDemo()
  {
  }

  public String getDemoName()
  {
    return "Interactive HTML-Demo";
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
    return ObjectUtilities.getResourceRelative("interactive-html.html", InteractiveHtmlDemo.class);
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

    HtmlReportUtil.createStreamHTML(handler.createReport(), "/tmp/report.html");
  }
}
