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


package org.pentaho.reporting.engine.classic.demo.features.autotable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.DriverConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.SQLReportDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriter;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.wizard.RelationalAutoGeneratorPreProcessor;
import org.pentaho.reporting.engine.classic.demo.util.AbstractDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.engine.classic.demo.util.SimpleDemoFrame;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.repository.ContentIOException;

public class AutoTableAPIDemo extends AbstractDemoHandler
{
  public AutoTableAPIDemo()
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
   * @throws ReportDefinitionException if an error occured preventing the report definition.
   */
  public MasterReport createReport() throws ReportDefinitionException
  {
    final DriverConnectionProvider drc = new DriverConnectionProvider();
    drc.setDriver("org.hsqldb.jdbcDriver");
    drc.setUrl("jdbc:hsqldb:./sql/sampledata");
    drc.setProperty("user", "sa");
    drc.setProperty("password", "");
    final SQLReportDataFactory sqlDataFactory = new SQLReportDataFactory(drc);
    sqlDataFactory.setQuery("default", "      SELECT\n" +
        "           QUADRANT_ACTUALS.REGION,\n" +
        "           QUADRANT_ACTUALS.DEPARTMENT,\n" +
        "           QUADRANT_ACTUALS.POSITIONTITLE,\n" +
        "           QUADRANT_ACTUALS.ACTUAL,\n" +
        "           QUADRANT_ACTUALS.BUDGET,\n" +
        "           QUADRANT_ACTUALS.VARIANCE\n" +
        "      FROM\n" +
        "           QUADRANT_ACTUALS\n" +
        "      ORDER BY\n" +
        "          REGION, DEPARTMENT, POSITIONTITLE", null, null);

    final MasterReport report = new MasterReport();
    report.setDataFactory(sqlDataFactory);
    report.setQuery("default");
    report.addPreProcessor(new RelationalAutoGeneratorPreProcessor());
    return report;
  }

  /**
   * Returns the URL of the HTML document describing this demo.
   *
   * @return the demo description.
   */
  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("auto-table.html", AutoTableAPIDemo.class);
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

  public void writeBundle(final String path)
      throws IOException, ContentIOException, BundleWriterException, ReportDefinitionException
  {
    BundleWriter.writeReportToZipFile(createReport(), new File(path));
  }

  public static void main(final String[] args) throws ReportProcessingException, IOException, ReportDefinitionException, ContentIOException, BundleWriterException
  {
    ClassicEngineBoot.getInstance().start();

    final AutoTableAPIDemo handler = new AutoTableAPIDemo();
    handler.writeBundle("/tmp/auto-table.prc");

    final SimpleDemoFrame frame = new SimpleDemoFrame(handler);
    frame.init();
    frame.pack();
    LibSwingUtil.centerFrameOnScreen(frame);
    frame.setVisible(true);
//    ExcelReportUtil.createXLS(handler.createReport(), "/tmp/report.xls");
//    PdfReportUtil.createPDF(handler.createReport(), "/tmp/report.pdf");

  }
}
