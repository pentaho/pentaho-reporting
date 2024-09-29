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


package org.pentaho.reporting.engine.classic.demo.ancient.demo.layouts;

import java.io.IOException;
import java.net.URL;
import javax.swing.JComponent;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.table.csv.CSVReportUtil;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.PlainParameter;
import org.pentaho.reporting.engine.classic.demo.util.AbstractXmlDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.PreviewHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * A simple report that shows the user input as report property value.
 *
 * @author Thomas Morgner
 */
public class StackedLayoutXMLDemoHandler extends AbstractXmlDemoHandler
{
  private DemoTextInputPanel panel;
  private PropertyUpdatePreviewHandler previewHandler;

  /**
   * Constructs the demo application.
   */
  public StackedLayoutXMLDemoHandler()
  {
    panel = new DemoTextInputPanel();
    previewHandler = new PropertyUpdatePreviewHandler(this);
  }

  public String getDemoName()
  {
    return "Stacked Layout Manager Demo (XML)";
  }

  public MasterReport createReport() throws ReportDefinitionException
  {
    final MasterReport report = parseReport();

    // The old XML formats have no sane way of defining parameters.
    final DefaultParameterDefinition paramDef = new DefaultParameterDefinition();
    paramDef.addParameterDefinition(new PlainParameter("Message1", String.class));
    paramDef.addParameterDefinition(new PlainParameter("Message2", String.class));

    report.getParameterValues().put("Message1", panel.getMessageOne());
    report.getParameterValues().put("Message2", panel.getMessageTwo());
    return report;
  }

  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative("stacked-layout.html", StackedLayoutXMLDemoHandler.class);
  }

  public JComponent getPresentationComponent()
  {
    return panel;
  }

  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative("stacked-layout.xml", StackedLayoutXMLDemoHandler.class);
  }

  public PreviewHandler getPreviewHandler()
  {
    return previewHandler;
  }


  public static void main(final String[] args) throws ReportProcessingException, IOException, ReportDefinitionException
  {
    ClassicEngineBoot.getInstance().start();

    final StackedLayoutXMLDemoHandler demoHandler = new StackedLayoutXMLDemoHandler();
//    final SimpleDemoFrame frame = new SimpleDemoFrame(demoHandler);
//    frame.init();
//    frame.pack();
//    RefineryUtilities.centerFrameOnScreen(frame);
//    frame.setVisible(true);
    CSVReportUtil.createCSV(demoHandler.createReport(), "/tmp/report.csv");
  }
}
