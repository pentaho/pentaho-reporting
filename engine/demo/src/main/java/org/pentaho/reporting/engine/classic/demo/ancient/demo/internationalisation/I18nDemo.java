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


package org.pentaho.reporting.engine.classic.demo.ancient.demo.internationalisation;

import java.net.URL;
import javax.swing.JComponent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.demo.util.AbstractXmlDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.PreviewHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.engine.classic.demo.util.SimpleDemoFrame;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

/**
 * A simple report where column 3 displays (column 1 / column 2) as a percentage.
 *
 * @author David Gilbert
 */
public class I18nDemo extends AbstractXmlDemoHandler
{

  /**
   * The data for the report.
   */
  private TableModel data;

  /**
   * Constructs the demo application.
   */
  public I18nDemo()
  {
    this.data = createData();
  }


  public String getDemoName()
  {
    return "Internationalisation Demo";
  }

  public MasterReport createReport() throws ReportDefinitionException
  {
    MasterReport report = parseReport();
    report.setDataFactory(new TableDataFactory
        ("default", data));
    return report;
  }

  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("i18n.html", I18nDemo.class);
  }

  public JComponent getPresentationComponent()
  {
    return createDefaultTable(data);
  }

  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("i18n.xml", I18nDemo.class);
  }

  /**
   * Creates a sample dataset. <!-- (Used in JUnitTest) -->
   *
   * @return A <code>TableModel</code>.
   */
  public static TableModel createData()
  {
    final DefaultTableModel data = new DefaultTableModel();
    data.addColumn("Data");
    data.addColumn("A");
    data.addColumn("B");
    data.addColumn("C");
    data.addRow(new Object[]{"data.firstElement", new Double(43.0), new Double(127.5), new Double(10001.999)});
    data.addRow(new Object[]{"data.secondElement", new Double(57.0), new Double(108.5), new Double(-10001.999)});
    data.addRow(new Object[]{"data.thirdElement", new Double(35.0), new Double(164.8), new Double(-999.9999)});
    data.addRow(new Object[]{"data.fourthElement", new Double(86.0), new Double(164.0), new Double(999.9999)});
    data.addRow(new Object[]{"data.lastElement", new Double(12.0), new Double(103.2), new Double(0.999)});
    return data;
  }

  public PreviewHandler getPreviewHandler()
  {
    return new LocaleUpdatePreviewHandler(this);
  }


  public static void main(String[] args)
  {
    // initialize JFreeReport
    ClassicEngineBoot.getInstance().start();

    final I18nDemo handler = new I18nDemo();
    final SimpleDemoFrame frame = new SimpleDemoFrame(handler);
    frame.init();
    frame.pack();
    LibSwingUtil.centerFrameOnScreen(frame);
    frame.setVisible(true);

  }
}
