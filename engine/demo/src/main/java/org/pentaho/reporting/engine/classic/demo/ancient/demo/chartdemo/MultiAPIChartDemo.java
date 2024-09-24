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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.chartdemo;

import java.awt.geom.Point2D;
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
import org.pentaho.reporting.engine.classic.core.elementfactory.ContentFieldElementFactory;
import org.pentaho.reporting.engine.classic.demo.util.AbstractDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.libraries.base.util.FloatDimension;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class MultiAPIChartDemo extends AbstractDemoHandler
{
  private TableModel data;

  public MultiAPIChartDemo()
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
    return "Multiple JFreeChart Demo (API)";
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
    // empty as implementation side effect
    final MasterReport report = new MasterReport();

    final ContentFieldElementFactory factory = new ContentFieldElementFactory();
    factory.setName("drawable-field");
    factory.setAbsolutePosition(new Point2D.Float(7, 7));
    factory.setMinimumSize(new FloatDimension(400, 250));
    factory.setFieldname("Chart");
    report.getItemBand().addElement(factory.createElement());
    report.setDataFactory(new TableDataFactory("default", data));
    return report;
  }

  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative("multi-chart.html", MultiAPIChartDemo.class);
  }

  public JComponent getPresentationComponent()
  {
    return createDefaultTable(data);
  }

}
