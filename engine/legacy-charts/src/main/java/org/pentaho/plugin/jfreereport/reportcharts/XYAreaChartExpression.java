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

package org.pentaho.plugin.jfreereport.reportcharts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer2;
import org.jfree.chart.renderer.xy.XYAreaRenderer2;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.UnitType;
import org.pentaho.plugin.jfreereport.reportcharts.backport.ExtTimeTableXYDataset;

public class XYAreaChartExpression extends XYLineChartExpression {
  private static final long serialVersionUID = -8475649582680588912L;

  public XYAreaChartExpression() {
  }

  public static JFreeChart createTimeSeriesChart( final String title,
                                                  final String timeAxisLabel,
                                                  final String valueAxisLabel,
                                                  final XYDataset dataset,
                                                  final boolean legend,
                                                  final boolean tooltips,
                                                  final boolean urls,
                                                  final boolean stacked ) {
    final ValueAxis timeAxis = new DateAxis( timeAxisLabel );
    timeAxis.setLowerMargin( 0.025 );  // reduce the default margins
    timeAxis.setUpperMargin( 0.025 );
    final NumberAxis valueAxis = new NumberAxis( valueAxisLabel );
    valueAxis.setAutoRangeIncludesZero( false );  // override default
    final XYPlot plot = new XYPlot( dataset, timeAxis, valueAxis, null );
    plot.setInsets( new RectangleInsets( UnitType.ABSOLUTE, 0, 0, 0, 15 ) );

    XYToolTipGenerator toolTipGenerator = null;
    if ( tooltips ) {
      toolTipGenerator
        = StandardXYToolTipGenerator.getTimeSeriesInstance();
    }

    XYURLGenerator urlGenerator = null;
    if ( urls ) {
      urlGenerator = new StandardXYURLGenerator();
    }

    final XYAreaRenderer2 renderer;
    if ( stacked ) {
      renderer = new StackedXYAreaRenderer2();
    } else {
      renderer = new XYAreaRenderer2();
    }
    renderer.setBaseToolTipGenerator( toolTipGenerator );
    renderer.setURLGenerator( urlGenerator );
    plot.setRenderer( renderer );

    return new JFreeChart( title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend );
  }

  protected JFreeChart computeXYChart( final XYDataset xyDataset ) {
    final JFreeChart chart;
    if ( xyDataset instanceof TimeSeriesCollection ) {

      if ( isStacked() ) {
        final ExtTimeTableXYDataset tableXYDataset = convertToTable( xyDataset );
        chart = createTimeSeriesChart( computeTitle(), getDomainTitle(), getRangeTitle(), tableXYDataset,
          isShowLegend(), false, false, isStacked() );
      } else {
        chart = createTimeSeriesChart( computeTitle(), getDomainTitle(), getRangeTitle(), xyDataset,
          isShowLegend(), false, false, isStacked() );
      }
    } else {
      final PlotOrientation orientation = computePlotOrientation();
      if ( isStacked() ) {
        chart = createStackedXYAreaChart( computeTitle(), getDomainTitle(), getRangeTitle(),
          xyDataset, orientation, isShowLegend(), false, false );
      } else {
        chart = ChartFactory.createXYAreaChart( computeTitle(), getDomainTitle(), getRangeTitle(),
          xyDataset, orientation, isShowLegend(), false, false );
      }
    }

    configureLogarithmicAxis( chart.getXYPlot() );
    return chart;
  }

  /**
   * Creates a stacked XY area plot.  The chart object returned by this method uses an {@link
   * org.jfree.chart.plot.XYPlot} instance as the plot, with a {@link org.jfree.chart.axis.NumberAxis} for the domain
   * axis, a {@link org.jfree.chart.axis.NumberAxis} as the range axis, and a {@link
   * org.jfree.chart.renderer.xy.StackedXYAreaRenderer2} as the renderer.
   *
   * @param title       the chart title (<code>null</code> permitted).
   * @param xAxisLabel  a label for the X-axis (<code>null</code> permitted).
   * @param yAxisLabel  a label for the Y-axis (<code>null</code> permitted).
   * @param dataset     the dataset for the chart (<code>null</code> permitted).
   * @param orientation the plot orientation (horizontal or vertical) (<code>null</code> NOT permitted).
   * @param legend      a flag specifying whether or not a legend is required.
   * @param tooltips    configure chart to generate tool tips?
   * @param urls        configure chart to generate URLs?
   * @return A stacked XY area chart.
   */
  protected static JFreeChart createStackedXYAreaChart( final String title,
                                                        final String xAxisLabel,
                                                        final String yAxisLabel,
                                                        final XYDataset dataset,
                                                        final PlotOrientation orientation,
                                                        final boolean legend,
                                                        final boolean tooltips,
                                                        final boolean urls ) {

    if ( orientation == null ) {
      throw new IllegalArgumentException( "Null 'orientation' argument." );
    }
    final NumberAxis xAxis = new NumberAxis( xAxisLabel );
    xAxis.setAutoRangeIncludesZero( false );
    xAxis.setLowerMargin( 0.0 );
    xAxis.setUpperMargin( 0.0 );
    final NumberAxis yAxis = new NumberAxis( yAxisLabel );
    XYToolTipGenerator toolTipGenerator = null;
    if ( tooltips ) {
      toolTipGenerator = new StandardXYToolTipGenerator();
    }

    XYURLGenerator urlGenerator = null;
    if ( urls ) {
      urlGenerator = new StandardXYURLGenerator();
    }
    final StackedXYAreaRenderer2 renderer = new StackedXYAreaRenderer2( toolTipGenerator, urlGenerator );
    renderer.setOutline( true );
    final XYPlot plot = new XYPlot( dataset, xAxis, yAxis, renderer );
    plot.setOrientation( orientation );

    plot.setRangeAxis( yAxis );  // forces recalculation of the axis range

    return new JFreeChart( title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend );
  }

}
