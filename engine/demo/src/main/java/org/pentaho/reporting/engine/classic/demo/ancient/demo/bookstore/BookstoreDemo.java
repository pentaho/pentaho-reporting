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


package org.pentaho.reporting.engine.classic.demo.ancient.demo.bookstore;

import java.io.IOException;
import java.net.URL;
import javax.swing.JComponent;
import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewDialog;
import org.pentaho.reporting.engine.classic.demo.util.AbstractXmlDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * A demo showing how to print simple invoices.
 *
 * @author Thomas Morgner
 */
public class BookstoreDemo extends AbstractXmlDemoHandler
{
  /**
   * The data model to be used in the demo.
   */
  private TableModel data;

  /**
   * Default constructor.
   */
  public BookstoreDemo()
  {
    data = new BookstoreTableModel();
  }

  /**
   * Returns the display name of the demo.
   *
   * @return the name.
   */
  public String getDemoName()
  {
    return "Bookstore (Invoice) Demo";
  }

  /**
   * Creates the report. This calls the standard parse method and then assigns the table model to the report.
   *
   * @return the fully initialized JFreeReport object.
   * @throws ReportDefinitionException if an error occured preventing the report definition.
   */
  public MasterReport createReport() throws ReportDefinitionException
  {
    final MasterReport report = parseReport();
    report.setDataFactory(new TableDataFactory
        ("default", data));
    return report;
  }

  /**
   * Returns the URL of the HTML document describing this demo.
   *
   * @return the demo description.
   */
  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative("bookstore.html", BookstoreDemo.class);
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
    return ObjectUtilities.getResourceRelative("bookstore.xml", BookstoreDemo.class);
  }


  /**
   * Entry point for running the demo application...
   *
   * @param args ignored.
   */
  public static void main(final String[] args) throws ReportDefinitionException, ReportProcessingException, IOException
  {
    // initialize JFreeReport
    ClassicEngineBoot.getInstance().start();

    final BookstoreDemo handler = new BookstoreDemo();

//    HtmlReportUtil.createDirectoryHTML(handler.createReport(), "/tmp/report.html");
//    ExcelReportUtil.createXLS(handler.createReport(), "/tmp/report.xls");
    final MasterReport report = handler.createReport();
    final PreviewDialog dialog = new PreviewDialog();
    dialog.setReportJob(report);
    dialog.setSize(500, 500);
    dialog.setModal(true);
    dialog.setVisible(true);
    System.exit(0);
//
//    final SimpleDemoFrame frame = new SimpleDemoFrame(handler);
//    frame.init();
//    frame.pack();
//    RefineryUtilities.centerFrameOnScreen(frame);
//    frame.setVisible(true);

  }
}
