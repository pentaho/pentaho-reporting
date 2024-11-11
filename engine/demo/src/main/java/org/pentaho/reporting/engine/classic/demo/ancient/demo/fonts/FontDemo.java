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


package org.pentaho.reporting.engine.classic.demo.ancient.demo.fonts;

import java.net.URL;
import javax.swing.JComponent;
import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewDialog;
import org.pentaho.reporting.engine.classic.demo.util.AbstractXmlDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * This Demo loads all available fonts and prints some sample text with each font.
 *
 * @author Thomas Morgner
 */
public class FontDemo extends AbstractXmlDemoHandler
{
  private TableModel data;

  public FontDemo()
  {
    data = new FontTableModel();
  }

  public String getDemoName()
  {
    return "Font Demo";
  }

  public MasterReport createReport() throws ReportDefinitionException
  {
    final MasterReport report = parseReport();
    report.setDataFactory(new TableDataFactory
        ("default", data));
    return report;
  }

  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative("fonts.html", FontDemo.class);
  }

  public JComponent getPresentationComponent()
  {
    return createDefaultTable(data);
  }

  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative("fonts.xml", FontDemo.class);
  }


  /**
   * Entry point for running the demo application...
   *
   * @param args ignored.
   */
  public static void main(final String[] args)
      throws ReportDefinitionException
  {
    // initialize JFreeReport
    ClassicEngineBoot.getInstance().start();

    final FontDemo handler = new FontDemo();
    PreviewDialog dialog = new PreviewDialog(handler.createReport());
    dialog.setSize(500, 300);
    dialog.setModal(true);
    dialog.setVisible(true);
    System.exit(0);
//    PdfReportUtil.createPDF(handler.createReport(), "/tmp/exporte.pdf");

//    final SimpleDemoFrame frame = new SimpleDemoFrame(handler);
//    frame.init();
//    frame.pack();
//    RefineryUtilities.centerFrameOnScreen(frame);
//    frame.setVisible(true);

  }
}
