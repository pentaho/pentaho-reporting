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


package org.pentaho.reporting.engine.classic.demo.features.autotable;

import java.net.URL;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.demo.util.AbstractXmlDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.engine.classic.demo.util.SimpleDemoFrame;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

public class AutoTableBundleDemo extends AbstractXmlDemoHandler
{
  public AutoTableBundleDemo()
  {
  }

  /**
   * Returns the display name of the demo.
   *
   * @return the name.
   */
  public String getDemoName()
  {
    return "Automatic-Report Demo";
  }

  /**
   * Creates the report. For XML reports, this will most likely call the ReportGenerator, while API reports may use this
   * function to build and return a new, fully initialized report object.
   *
   * @return the fully initialized JFreeReport object.
   * @throws org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException
   *          if an error occured preventing the report definition.
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
        ("auto-table.prpt", AutoTableBundleDemo.class);
  }

  /**
   * Returns the URL of the HTML document describing this demo.
   *
   * @return the demo description.
   */
  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("auto-table.html", AutoTableBundleDemo.class);
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

  public static void main(String[] args) throws Exception
  {
    ClassicEngineBoot.getInstance().start();

    final AutoTableBundleDemo handler = new AutoTableBundleDemo();

//    final JFreeReport report = handler.createReport();
//    report.setPreProcessor(new RelationalAutoGeneratorPreProcessor());
//    BundleWriter.writeReportToZipFile(report, new File("/tmp/auto-table.prpt"));

    final SimpleDemoFrame frame = new SimpleDemoFrame(handler);
    frame.init();
    frame.pack();
    LibSwingUtil.centerFrameOnScreen(frame);
    frame.setVisible(true);

  }
}
