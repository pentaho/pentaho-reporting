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


package org.pentaho.reporting.engine.classic.demo.ancient.demo.conditionalgroup;

import java.math.BigDecimal;
import java.net.URL;
import javax.swing.JComponent;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.demo.util.AbstractXmlDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.engine.classic.demo.util.SimpleDemoFrame;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

public class ConditionalGroupDemo extends AbstractXmlDemoHandler
{
  private ConditionalGroupTableModel data;

  public ConditionalGroupDemo()
  {
    this.data = new ConditionalGroupTableModel();
    this.data.addRecord("Income", "Account 1", null, new BigDecimal("9999.99"));
    this.data.addRecord("Income", "Account 2", null, new BigDecimal("9999.99"));
    this.data.addRecord("Expense", "Account A", "Account Z", new BigDecimal("9999.99"));
    this.data.addRecord("Expense", "Account A", "Account Y", new BigDecimal("9999.99"));
    this.data.addRecord("Expense", "Account B", null, new BigDecimal("9999.99"));
  }

  public String getDemoName()
  {
    return "Conditional Group Demo";
  }

  public JComponent getPresentationComponent()
  {
    return createDefaultTable(data);
  }

  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("conditional.html", ConditionalGroupDemo.class);
  }

  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("conditional-group-demo.xml", ConditionalGroupDemo.class);
  }


  public MasterReport createReport() throws ReportDefinitionException
  {
    MasterReport report = parseReport();
    report.setDataFactory(new TableDataFactory
        ("default", data));
    return report;
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

    final ConditionalGroupDemo handler = new ConditionalGroupDemo();
    final SimpleDemoFrame frame = new SimpleDemoFrame(handler);
    frame.init();
    frame.pack();
    LibSwingUtil.centerFrameOnScreen(frame);
    frame.setVisible(true);
  }
}
