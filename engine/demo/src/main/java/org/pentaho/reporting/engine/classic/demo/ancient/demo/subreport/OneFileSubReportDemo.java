/*
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.demo.ancient.demo.subreport;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import javax.swing.JComponent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.misc.tablemodel.JoiningTableModel;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ArrayClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ExtraShapesClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.URLClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.datasource.DefaultDataSourceFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.elements.DefaultElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.objects.BandLayoutClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.objects.DefaultClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.stylekey.DefaultStyleKeyFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.stylekey.PageableLayoutStyleKeyFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.templates.DefaultTemplateCollection;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriter;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException;
import org.pentaho.reporting.engine.classic.demo.util.AbstractXmlDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * The MultiReportDemo combines data from multiple table models into one single report.
 * <p/>
 * For a detailed explaination of the demo have a look at the file <a href="multireport.html">file</a>'.
 *
 * @author Thomas Morgner
 */
public class OneFileSubReportDemo extends AbstractXmlDemoHandler
{
  /**
   * The data for the report.
   */
  private final TableModel data;

  public OneFileSubReportDemo()
  {
    this.data = createJoinedTableModel();
  }

  public String getDemoName()
  {
    return "Multi-Report Demo (Sub-Report-Version; One file)";
  }

  public MasterReport createReport() throws ReportDefinitionException
  {
    return parseReport();
  }

  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("multireport.html", OneFileSubReportDemo.class);
  }

  public JComponent getPresentationComponent()
  {
    return createDefaultTable(data);
  }

  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("one-file-subreport.xml", OneFileSubReportDemo.class);
  }

  public static TableModel createFruitTableModel()
  {
    final String[] names = new String[]{"Id Number", "Cat", "Fruit"};
    final Object[][] data = new Object[][]{
        {"I1", "A", "Apple"},
        {"I2", "A", "Orange"},
        {"I3", "B", "Water melon"},
        {"I4", "B", "Strawberry"},
    };
    return new DefaultTableModel(data, names);
  }

  public static TableModel createColorTableModel()
  {
    final String[] names = new String[]{"Number", "Group", "Color"};
    final Object[][] data = new Object[][]{
        {new Integer(1), "X", "Red"},
        {new Integer(2), "X", "Green"},
        {new Integer(3), "Y", "Yellow"},
        {new Integer(4), "Y", "Blue"},
        {new Integer(5), "Z", "Orange"},
        {new Integer(6), "Z", "White"},
    };
    return new DefaultTableModel(data, names);
  }

  private TableModel createJoinedTableModel()
  {
    final JoiningTableModel jtm = new JoiningTableModel();
    jtm.addTableModel("Color", OneFileSubReportDemo.createColorTableModel());
    jtm.addTableModel("Fruit", OneFileSubReportDemo.createFruitTableModel());
    return jtm;
  }

  public static void main(final String[] args)
      throws ReportDefinitionException, IOException, ReportWriterException
  {
    ClassicEngineBoot.getInstance().start();

    final OneFileSubReportDemo demoHandler = new OneFileSubReportDemo();
//    final SimpleDemoFrame frame = new SimpleDemoFrame(demoHandler);
//    frame.init();
//    frame.pack();
//    RefineryUtilities.centerFrameOnScreen(frame);
//    frame.setVisible(true);
    final MasterReport report = demoHandler.createReport();
    final ReportWriter writer = new ReportWriter(report, "ISO-8859-1",
        ReportWriter.createDefaultConfiguration(report));
    writer.addClassFactoryFactory(new URLClassFactory());
    writer.addClassFactoryFactory(new DefaultClassFactory());
    writer.addClassFactoryFactory(new BandLayoutClassFactory());
    writer.addClassFactoryFactory(new ArrayClassFactory());
    writer.addClassFactoryFactory(new ExtraShapesClassFactory());
    writer.addStyleKeyFactory(new DefaultStyleKeyFactory());
    writer.addStyleKeyFactory(new PageableLayoutStyleKeyFactory());
    writer.addTemplateCollection(new DefaultTemplateCollection());
    writer.addElementFactory(new DefaultElementFactory());
    writer.addDataSourceFactory(new DefaultDataSourceFactory());
    final OutputStreamWriter w = new OutputStreamWriter(System.out);
    writer.write(w);
    w.close();
  }
}
