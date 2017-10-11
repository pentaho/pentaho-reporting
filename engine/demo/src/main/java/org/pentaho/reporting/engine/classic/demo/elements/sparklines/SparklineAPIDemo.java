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

package org.pentaho.reporting.engine.classic.demo.elements.sparklines;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.net.URL;
import java.util.Random;
import javax.swing.JComponent;
import javax.swing.table.DefaultTableModel;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportFooter;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.HorizontalLineElementFactory;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.PlainParameter;
import org.pentaho.reporting.engine.classic.demo.util.AbstractDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.engine.classic.demo.util.SimpleDemoFrame;
import org.pentaho.reporting.engine.classic.extensions.modules.sparklines.elementfactory.BarSparklineElementFactory;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

/**
 * This demo shows different usages of Sparkline module available in pentaho reporting extension project.
 *
 * @author Cedric Pronzato
 */
public class SparklineAPIDemo extends AbstractDemoHandler
{
  private DefaultTableModel data;
  private final Random random;

  public SparklineAPIDemo()
  {
    random = new Random();
    data = createData();
  }

  /**
   * Creates data of sales per months.
   *
   * @return The tabular report data.
   */
  private DefaultTableModel createData()
  {
    final String[] columnNames = {"January", "February", "March", "April", "May", "June", "July", "August",
        "September", "October", "November", "December"};
    DefaultTableModel data = new DefaultTableModel(columnNames, 10);

    for (int r = 0; r < 10; r++)
    {
      for (int i = 0; i < 12 && r < 10; i++)
      {
        // we want some negative values
        int neg = -random.nextInt(2);
        if (neg == 0)
        {
          neg = 1;
        }
        final Integer value = new Integer(neg * random.nextInt(30));
        data.setValueAt(value, r, i);
      }
    }
    return data;
  }

  /**
   * Returns the display name of the demo.
   *
   * @return the name.
   */
  public String getDemoName()
  {
    return "Sparkline bar-graph demo (API)";
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
    final MasterReport report = new MasterReport();
    report.setName("Sparkline Demo");
    final DefaultParameterDefinition paramDef = new DefaultParameterDefinition();
    final PlainParameter plainParameter = new PlainParameter("sparkline-data", Number[].class);
    plainParameter.setDefaultValue(new Number[]
        {new Integer(10), new Integer(5),
            new Integer(6), new Integer(3),
            new Integer(1), new Integer(2),
            new Integer(7), new Integer(9)});
    paramDef.addParameterDefinition(plainParameter);

    report.setParameterDefinition(paramDef);

    // using the field sparkline-data
    final BarSparklineElementFactory elementFactory = new BarSparklineElementFactory();
    elementFactory.setFieldname("sparkline-data");
    elementFactory.setAbsolutePosition(new Point2D.Float(0, 0));
    elementFactory.setMinimumSize(new Dimension(100, 10));
    elementFactory.setColor(Color.black);
    elementFactory.setHighColor(Color.red);
    elementFactory.setLastColor(Color.blue);
    elementFactory.setBackgroundColor(Color.orange);

    final ReportFooter footer = report.getReportFooter();
    footer.addElement(elementFactory.createElement());

    // using a formula
    final BarSparklineElementFactory itemsSparkFactory = new BarSparklineElementFactory();
    itemsSparkFactory.setFormula
        ("={[January]|[February]|[March]|[April]|[May]|[June]|" +
            "[July]|[August]|[September]|[October]|[November]|[December]}");
    itemsSparkFactory.setAbsolutePosition(new Point2D.Float(0, 0));
    itemsSparkFactory.setMinimumSize(new Dimension(100, 10));
    itemsSparkFactory.setHighColor(Color.green);
    itemsSparkFactory.setLastColor(Color.blue);
    //itemsSparkFactory.setBackgroundColor(Color.yellow);
    final ItemBand itemBand = report.getItemBand();
    itemBand.addElement(itemsSparkFactory.createElement());

    itemBand.addElement(HorizontalLineElementFactory.createHorizontalLine
        (15, null, new BasicStroke(5)));

    report.setDataFactory(new TableDataFactory("default", data));
    return report;
  }

  /**
   * Returns the URL of the HTML document describing this demo.
   *
   * @return the demo description.
   */
  public URL getDemoDescriptionSource()
  {
    return null;
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

  public static void main(final String[] args) throws Exception
  {
    ClassicEngineBoot.getInstance().start();

    final SparklineAPIDemo demoHandler = new SparklineAPIDemo();
//    BundleWriter.writeReportToZipFile(demoHandler.createReport(), new File("/tmp/sparklines.prpt"));

    final SimpleDemoFrame frame = new SimpleDemoFrame(demoHandler);
    frame.init();
    frame.pack();
    LibSwingUtil.centerFrameOnScreen(frame);
    frame.setVisible(true);
//    HtmlReportUtil.createDirectoryHTML(demoHandler.createReport(), "/tmp/test/report.html");
  }
}
