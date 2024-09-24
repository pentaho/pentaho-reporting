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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.chartdemo;

import java.net.URL;
import javax.swing.JComponent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.demo.util.AbstractXmlDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class TableJFreeChartDemo extends AbstractXmlDemoHandler
{
  private TableModel data;


  /**
   * Creates a new demo.
   */
  public TableJFreeChartDemo()
  {
    data = createTableModel();
  }

  public String getDemoName()
  {
    return "Table And JFreeChart Demo (Simple-XML)";
  }

  /**
   * Creates a sample dataset for the demo.
   *
   * @return A sample dataset.
   */
  private PieDataset createSampleDataset(final int[] votes)
  {
    final DefaultPieDataset result = new DefaultPieDataset();
    // cheating: java has a higher chance to be the best language :)
    result.setValue("Java", new Integer(votes[0]));
    result.setValue("Visual Basic", new Integer(votes[1]));
    result.setValue("C/C++", new Integer(votes[2]));
    result.setValue("PHP", new Integer(votes[3]));
    result.setValue("Perl", new Integer(votes[4]));
    return result;
  }

  /**
   * Creates a sample chart.
   *
   * @return A chart.
   */
  private JFreeChart createChart(final int year, final int[] votes)
  {

    final JFreeChart chart = ChartFactory.createPieChart3D(
        "Programming Language of the Year " + (year), // chart title
        createSampleDataset(votes), // data
        true, // include legend
        true,
        false
    );

    // set the background color for the chart...
    final PiePlot3D plot = (PiePlot3D) chart.getPlot();
    plot.setStartAngle(270);
//    plot.setDirection(Rotation.CLOCKWISE);
    plot.setForegroundAlpha(0.5f);
    plot.setNoDataMessage("No data to display");

    return chart;

  }

  private TableModel createTableModel()
  {
    final Object[][] data = new Object[12 * 5][];
    final int[] votes = new int[5];
    for (int i = 0; i < 12; i++)
    {
      final Integer year = new Integer(1995 + i);
      votes[0] = (int) (Math.random() * 200);
      votes[1] = (int) (Math.random() * 50);
      votes[2] = (int) (Math.random() * 100);
      votes[3] = (int) (Math.random() * 50);
      votes[4] = (int) (Math.random() * 100);

      final JFreeChart chart = createChart(year.intValue(), votes);

      data[i * 5] = new Object[]{
          year, "Java", new Integer(votes[0]), chart
      };
      data[i * 5 + 1] = new Object[]{
          year, "Visual Basic", new Integer(votes[1]), chart
      };
      data[i * 5 + 2] = new Object[]{
          year, "C/C++", new Integer(votes[2]), chart
      };
      data[i * 5 + 3] = new Object[]{
          year, "PHP", new Integer(votes[3]), chart
      };
      data[i * 5 + 4] = new Object[]{
          year, "Perl", new Integer(votes[4]), chart
      };

    }

    final String[] colNames = {
        "year", "language", "votes", "chart"
    };

    return new DefaultTableModel(data, colNames);
  }

  public MasterReport createReport() throws ReportDefinitionException
  {
    final MasterReport report = parseReport();
    report.setDataFactory(new TableDataFactory("default", data));
    return report;
  }

  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative("table-chart.html", MultiSimpleXmlChartDemo.class);
  }

  public JComponent getPresentationComponent()
  {
    return createDefaultTable(data);
  }

  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative("table-chart-simple.xml", MultiSimpleXmlChartDemo.class);
  }
}
