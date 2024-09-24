/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
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

public class MultiSimpleXmlChartDemo extends AbstractXmlDemoHandler
{
  private TableModel data;

  public MultiSimpleXmlChartDemo()
  {
    data = createTableModel();
  }

  private TableModel createTableModel()
  {
    final Object[][] data = new Object[12][];
    for (int i = 0; i < 12; i++)
    {
      data[i] = new Object[]{createChart(i + 1995)};
    }

    final String[] colNames = {
        "Chart"
    };
    return new DefaultTableModel(data, colNames);
  }

  public String getDemoName()
  {
    return "Multiple JFreeChart Demo (Simple-XML)";
  }

  /**
   * Creates a sample dataset for the demo.
   *
   * @return A sample dataset.
   */
  private PieDataset createSampleDataset()
  {
    final DefaultPieDataset result = new DefaultPieDataset();
    // cheating: java has a higher chance to be the best language :)
    result.setValue("Java", new Integer((int) (Math.random() * 200)));
    result.setValue("Visual Basic", new Integer((int) (Math.random() * 50)));
    result.setValue("C/C++", new Integer((int) (Math.random() * 100)));
    result.setValue("PHP", new Integer((int) (Math.random() * 50)));
    result.setValue("Perl", new Integer((int) (Math.random() * 100)));
    return result;

  }

  /**
   * Creates a sample chart.
   *
   * @return A chart.
   */
  private JFreeChart createChart(final int year)
  {

    final JFreeChart chart = ChartFactory.createPieChart3D(
        "Programming Language of the Year " + year, // chart title
        createSampleDataset(), // data
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

  public MasterReport createReport() throws ReportDefinitionException
  {
    final MasterReport report = parseReport();
    report.setDataFactory(new TableDataFactory("default", data));
    return report;
  }

  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative("multi-chart.html", MultiSimpleXmlChartDemo.class);
  }

  public JComponent getPresentationComponent()
  {
    return createDefaultTable(data);
  }

  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative("multi-chart-simple.xml", MultiSimpleXmlChartDemo.class);
  }

}
