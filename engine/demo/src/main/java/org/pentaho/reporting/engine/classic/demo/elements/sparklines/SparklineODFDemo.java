/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.demo.elements.sparklines;

import java.net.URL;
import javax.swing.JComponent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.demo.util.AbstractXmlDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.engine.classic.demo.util.SimpleDemoFrame;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

/**
 * This demo shows different usages of Sparkline module available in pentaho reporting extension project.
 *
 * @author Cedric Pronzato
 */
public class SparklineODFDemo extends AbstractXmlDemoHandler
{
  private TableModel data;

  public SparklineODFDemo()
  {
    data = createData();
  }

  /**
   * Creates data of sales per months.
   *
   * @return The tabular report data.
   */
  private TableModel createData()
  {
    try
    {
      final MasterReport report = createReport();
      return report.getDataFactory().queryData(report.getQuery(), report.getParameterValues());
    }
    catch (Exception e)
    {
      // ignore generate a new dataset
      final String[] columnNames = {"January", "February", "March", "April", "May", "June", "July", "August",
          "September", "October", "November", "December"};
      return new DefaultTableModel(columnNames, 0);
    }
  }

  /**
   * Returns the display name of the demo.
   *
   * @return the name.
   */
  public String getDemoName()
  {
    return "Sparkline bar-graph demo (Unified File Format)";
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
   * Returns the URL of the HTML document describing this demo.
   *
   * @return the demo description.
   */
  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("sparkline-simple.html", SparklineXMLDemo.class);
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
    return createDefaultTable(data);
  }

  /**
   * Returns the URL of the XML definition for this report.
   *
   * @return the URL of the report definition.
   */
  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("sparklines.prpt", SparklineODFDemo.class);
  }

  public static void main(final String[] args) throws Exception
  {
    ClassicEngineBoot.getInstance().start();

    final SparklineODFDemo demoHandler = new SparklineODFDemo();

    final SimpleDemoFrame frame = new SimpleDemoFrame(demoHandler);
    frame.init();
    frame.pack();
    LibSwingUtil.centerFrameOnScreen(frame);
    frame.setVisible(true);

  }
}
