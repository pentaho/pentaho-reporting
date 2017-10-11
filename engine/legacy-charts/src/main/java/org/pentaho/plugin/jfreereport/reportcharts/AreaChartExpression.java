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
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.urls.StandardCategoryURLGenerator;
import org.jfree.data.category.CategoryDataset;
import org.pentaho.plugin.jfreereport.reportcharts.backport.FormattedCategoryAxis;
import org.pentaho.plugin.jfreereport.reportcharts.backport.StackedAreaRenderer;

public class AreaChartExpression extends StackedCategoricalChartExpression {
  private static final long serialVersionUID = -2663954070786799503L;
  private boolean renderAsPercentages;

  public AreaChartExpression() {
  }

  public boolean isRenderAsPercentages() {
    return renderAsPercentages;
  }

  public void setRenderAsPercentages( final boolean renderAsPercentages ) {
    this.renderAsPercentages = renderAsPercentages;
  }

  protected JFreeChart computeCategoryChart( final CategoryDataset categoryDataset ) {
    final PlotOrientation orientation = computePlotOrientation();
    final JFreeChart chart;
    if ( isStacked() ) {
      chart = createStackedAreaChart
        ( computeTitle(), getCategoryAxisLabel(), getValueAxisLabel(),
          categoryDataset, orientation, isShowLegend(),
          false, false );
    } else {
      chart = ChartFactory.createAreaChart
        ( computeTitle(), getCategoryAxisLabel(), getValueAxisLabel(), categoryDataset,
          orientation, isShowLegend(), false, false );
      chart.getCategoryPlot().setDomainAxis( new FormattedCategoryAxis( getCategoryAxisLabel(),
        getCategoricalAxisMessageFormat(), getRuntime().getResourceBundleFactory().getLocale() ) );
    }

    configureLogarithmicAxis( chart.getCategoryPlot() );
    return chart;
  }

  /**
   * Creates a stacked area chart with default settings.  The chart object returned by this method uses a {@link
   * CategoryPlot} instance as the plot, with a {@link org.jfree.chart.axis.CategoryAxis} for the domain axis, a {@link
   * org.jfree.chart.axis.NumberAxis} as the range axis, and a {@link org.jfree.chart.renderer.category
   * .StackedAreaRenderer}
   * as the renderer.
   *
   * @param title             the chart title (<code>null</code> permitted).
   * @param categoryAxisLabel the label for the category axis (<code>null</code> permitted).
   * @param valueAxisLabel    the label for the value axis (<code>null</code> permitted).
   * @param dataset           the dataset for the chart (<code>null</code> permitted).
   * @param orientation       the plot orientation (horizontal or vertical) (<code>null</code> not permitted).
   * @param legend            a flag specifying whether or not a legend is required.
   * @param tooltips          configure chart to generate tool tips?
   * @param urls              configure chart to generate URLs?
   * @return A stacked area chart.
   */
  private JFreeChart createStackedAreaChart( final String title,
                                             final String categoryAxisLabel, final String valueAxisLabel,
                                             final CategoryDataset dataset, final PlotOrientation orientation,
                                             final boolean legend, final boolean tooltips, final boolean urls ) {

    if ( orientation == null ) {
      throw new IllegalArgumentException( "Null 'orientation' argument." );
    }
    final CategoryAxis categoryAxis = new FormattedCategoryAxis( categoryAxisLabel,
      getCategoricalAxisMessageFormat(), getRuntime().getResourceBundleFactory().getLocale() );
    categoryAxis.setCategoryMargin( 0.0 );
    final ValueAxis valueAxis = new NumberAxis( valueAxisLabel );

    final StackedAreaRenderer renderer = new StackedAreaRenderer();
    if ( tooltips ) {
      renderer.setBaseToolTipGenerator(
        new StandardCategoryToolTipGenerator() );
    }
    if ( urls ) {
      renderer.setBaseItemURLGenerator(
        new StandardCategoryURLGenerator() );
    }

    final CategoryPlot plot = new CategoryPlot( dataset, categoryAxis, valueAxis,
      renderer );
    plot.setOrientation( orientation );
    return new JFreeChart( title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend );
  }

  protected void configureChart( final JFreeChart chart ) {
    super.configureChart( chart );
    final CategoryPlot cpl = chart.getCategoryPlot();

    cpl.getDomainAxis().setCategoryMargin( 0.0 );

    final CategoryItemRenderer renderer = cpl.getRenderer();
    if ( ( isStacked() ) && renderAsPercentages && ( renderer instanceof StackedAreaRenderer ) ) {
      final StackedAreaRenderer sbr = (StackedAreaRenderer) renderer;
      sbr.setRenderAsPercentages( true );
    }
  }

}
