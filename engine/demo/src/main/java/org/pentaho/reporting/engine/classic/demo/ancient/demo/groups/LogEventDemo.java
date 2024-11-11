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


package org.pentaho.reporting.engine.classic.demo.ancient.demo.groups;

import java.net.URL;
import java.util.GregorianCalendar;
import javax.swing.JComponent;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.demo.util.AbstractXmlDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.engine.classic.demo.util.SimpleDemoFrame;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

public class LogEventDemo extends AbstractXmlDemoHandler
{
  private LogEventTableModel tableModel;

  public LogEventDemo()
  {
    tableModel = new LogEventTableModel();
    GregorianCalendar gc = new GregorianCalendar();

    gc.set(2000, 10, 24, 19, 24, 0);
    tableModel.addEvent(new LogEvent(gc.getTime(), "Arrival", "Aliens arrived in New York"));

    gc.set(2000, 10, 24, 20, 14, 0);
    tableModel.addEvent(new LogEvent(gc.getTime(), "Meeting", "The UFO was stolen in the bronx."));

    gc.set(2000, 10, 24, 22, 14, 0);
    tableModel.addEvent(new LogEvent(gc.getTime(), "Meeting", "The aliens have been robbed."));

    gc.set(2000, 10, 24, 23, 23, 0);
    tableModel.addEvent(new LogEvent(gc.getTime(), "Event",
        "Hungry and with only little money, the alien entered a restaurant of an famous fast-food chain."));

    gc.set(2000, 10, 24, 23, 23, 0);
    tableModel.addEvent(new LogEvent(gc.getTime(), "Event",
        "The aliens died from drinking strange looking mineral water."));

    gc.set(2000, 10, 25, 9, 10, 0);
    tableModel.addEvent(new LogEvent(gc.getTime(), "Arrival", "The mother of the alien arrives."));

    gc.set(2000, 10, 25, 9, 14, 0);
    tableModel.addEvent(new LogEvent(gc.getTime(), "Meeting", "The UFO was stolen in the lower east side."));

    gc.set(2000, 10, 25, 10, 19, 0);
    tableModel.addEvent(new LogEvent(gc.getTime(), "Meeting", "The alien mother calls their growd."));

    gc.set(2000, 11, 25, 20, 59, 0);
    tableModel.addEvent(new LogEvent(gc.getTime(), "Diplomacy",
        "A vogon construction fleet appeared over all major cities."));

    gc.set(2000, 11, 25, 21, 10, 0);
    tableModel.addEvent(new LogEvent(gc.getTime(), "Diplomacy",
        "The United Nations fire a cloud of deadly lawyers against the vogons."));

    gc.set(2000, 11, 25, 21, 15, 0);
    tableModel.addEvent(new LogEvent(gc.getTime(), "Diplomacy",
        "No vogon survived. Earth is declared uninhabitable to intelligent life."));

    gc.set(2000, 12, 10, 10, 0, 0);
    tableModel.addEvent(new LogEvent(gc.getTime(), "Meeting",
        "The lawyers, pushed by their sucess, took over control over earth."));

    gc.set(2000, 11, 26, 18, 0, 0);
    tableModel.addEvent(new LogEvent(gc.getTime(), "Doomsday", "Life has gone."));
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
    final MasterReport report = parseReport();
    report.setDataFactory(new TableDataFactory
        ("default", tableModel));
    return report;
  }

  /**
   * Returns the URL of the HTML document describing this demo.
   *
   * @return the demo description.
   */
  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative("log-events.html", GroupsDemo.class);
  }

  /**
   * Returns the display name of the demo.
   *
   * @return the name.
   */
  public String getDemoName()
  {
    return "Log-Event demo";
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
    return createDefaultTable(tableModel);
  }

  /**
   * Returns the URL of the XML definition for this report.
   *
   * @return the URL of the report definition.
   */
  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative("log-events.xml", GroupsDemo.class);
  }

  /**
   * Entry point for running the demo application...
   *
   * @param args ignored.
   */
  public static void main(final String[] args)
  {
    // initialize JFreeReport
    ClassicEngineBoot.getInstance().start();

    final LogEventDemo handler = new LogEventDemo();
    final SimpleDemoFrame frame = new SimpleDemoFrame(handler);
    frame.init();
    frame.pack();
    LibSwingUtil.centerFrameOnScreen(frame);
    frame.setVisible(true);
  }
}
