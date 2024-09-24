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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.subreport;

import java.io.IOException;
import java.net.URL;
import javax.swing.JComponent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.misc.tablemodel.JoiningTableModel;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException;
import org.pentaho.reporting.engine.classic.demo.util.AbstractXmlDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.engine.classic.demo.util.SimpleDemoFrame;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

/**
 * The MultiReportDemo combines data from multiple table models into one single report.
 * <p/>
 * For a detailed explaination of the demo have a look at the file <a href="multireport.html">file</a>'.
 *
 * @author Thomas Morgner
 */
public class SubReportDemo extends AbstractXmlDemoHandler
{
  /**
   * The data for the report.
   */
  private final TableModel data;

  public SubReportDemo()
  {
    this.data = createJoinedTableModel();
  }

  public String getDemoName()
  {
    return "Sub-Report Demo";
  }

  public MasterReport createReport() throws ReportDefinitionException
  {
    final MasterReport report = parseReport();
    final TableDataFactory tableDataFactory = new TableDataFactory();
    tableDataFactory.addTable("default", new DefaultTableModel());
    tableDataFactory.addTable("fruit", createFruitTableModel());
    tableDataFactory.addTable("color", createColorTableModel());
    report.setDataFactory(tableDataFactory);
    return report;
  }

  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("subreport.html", SubReportDemo.class);
  }

  public JComponent getPresentationComponent()
  {
    return createDefaultTable(data);
  }

  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("joined-report.xml", SubReportDemo.class);
  }

  private TableModel createFruitTableModel()
  {
    final String[] names = new String[]{"Id Number", "Cat", "Fruit"};
    final Object[][] data = new Object[][]{
        {"I1", "A", "Apple"},
        {"I2", "A", "Orange"},
        {"I2", "A", "Orange"},
        {"I2", "A", "Orange"},
        {"I2", "A", "Orange"},
        {"I2", "A", "Orange"},
        {"I2", "A", "Orange"},
        {"I2", "A", "Orange"},
        {"I2", "A", "Orange"},
        {"I3", "B", "Water melon"},
        {"I3", "B", "Water melon"},
        {"I3", "B", "Water melon"},
        {"I3", "B", "Water melon"},
        {"I3", "B", "Water melon"},
        {"I3", "B", "Water melon"},
        {"I3", "B", "Water melon"},
        {"I3", "B", "Water melon"},
        {"I4", "B", "Strawberry"},
    };
    return new DefaultTableModel(data, names);
  }

  private TableModel createColorTableModel()
  {
    final String[] names = new String[]{"Number", "Group", "Color"};
    final Object[][] data = new Object[][]{
        {new Integer(1), "X", "Red"},
        {new Integer(2), "X", "Green"},
        {new Integer(3), "Y", "Yellow"},
        {new Integer(3), "Y", "Yellow"},
        {new Integer(4), "Y", "Blue"},
        {new Integer(4), "Y", "Blue"},
        {new Integer(5), "Z", "Orange"},
        {new Integer(5), "Z", "Orange"},
        {new Integer(5), "Z", "Orange"},
        {new Integer(6), "Z", "White"},
        {new Integer(6), "Z", "White"},
        {new Integer(6), "Z", "White"},
    };
    return new DefaultTableModel(data, names);
  }

  private TableModel createJoinedTableModel()
  {
    final JoiningTableModel jtm = new JoiningTableModel();
    jtm.addTableModel("Color", createColorTableModel());
    jtm.addTableModel("Fruit", createFruitTableModel());
    return jtm;
  }

  public static void main(final String[] args)
      throws ReportProcessingException, IOException, ReportDefinitionException, ReportWriterException
  {
    ClassicEngineBoot.getInstance().start();

    final SubReportDemo demoHandler = new SubReportDemo();
    final boolean preview = false;
    if (preview)
    {
      final SimpleDemoFrame frame = new SimpleDemoFrame(demoHandler);
      frame.init();
      frame.pack();
      LibSwingUtil.centerFrameOnScreen(frame);
      frame.setVisible(true);
    }
    else
    {
      HtmlReportUtil.createStreamHTML(demoHandler.createReport(), "/tmp/test.html");
    }
//    final JFreeReport report = demoHandler.createReport();
//    final ReportWriter writer = new ReportWriter(report, "ISO-8859-1",
//        ReportWriter.createDefaultConfiguration(report));
//    writer.addClassFactoryFactory(new URLClassFactory());
//    writer.addClassFactoryFactory(new DefaultClassFactory());
//    writer.addClassFactoryFactory(new BandLayoutClassFactory());
//    writer.addClassFactoryFactory(new ArrayClassFactory());
//    writer.addClassFactoryFactory(new ExtraShapesClassFactory());
//    writer.addStyleKeyFactory(new DefaultStyleKeyFactory());
//    writer.addStyleKeyFactory(new PageableLayoutStyleKeyFactory());
//    writer.addTemplateCollection(new DefaultTemplateCollection());
//    writer.addElementFactory(new DefaultElementFactory());
//    writer.addDataSourceFactory(new DefaultDataSourceFactory());
//    final OutputStreamWriter w = new OutputStreamWriter(System.out);
//    writer.write(w);
//    w.close();
  }
}
