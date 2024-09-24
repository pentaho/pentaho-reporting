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

import java.awt.Color;
import java.net.URL;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.demo.util.AbstractXmlDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class BasicExtXmlChartDemo extends AbstractXmlDemoHandler
{
  public BasicExtXmlChartDemo()
  {
  }

  public String getDemoName()
  {
    return "Basic JFreeChart Demo (Ext-XML)";
  }

  /**
   * Creates a sample dataset for the demo.
   *
   * @return A sample dataset.
   */
  private PieDataset createSampleDataset()
  {

    final DefaultPieDataset result = new DefaultPieDataset();
    result.setValue("Java", new Double(43.2));
    result.setValue("Visual Basic", new Double(10.0));
    result.setValue("C/C++", new Double(17.5));
    result.setValue("PHP", new Double(32.5));
    result.setValue("Perl", new Double(0.0));
    return result;

  }

  /**
   * Creates a sample chart.
   *
   * @param dataset the dataset.
   * @return A chart.
   */
  private JFreeChart createChart(final PieDataset dataset)
  {

    final JFreeChart chart = ChartFactory.createPieChart3D(
        "Pie Chart 3D Demo 1", // chart title
        dataset, // data
        true, // include legend
        true,
        false
    );

    // set the background color for the chart...
    chart.setBackgroundPaint(Color.yellow);
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
    // create a dataset...
    final PieDataset dataset = createSampleDataset();
    // create the chart...
    final JFreeChart chart = createChart(dataset);
    report.getParameterValues().put("Chart", chart);
    return report;
  }

  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative("basic-chart.html", BasicExtXmlChartDemo.class);
  }

  public JComponent getPresentationComponent()
  {
    return new JPanel();
  }

  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative("basic-chart-ext.xml", BasicExtXmlChartDemo.class);
  }

}
