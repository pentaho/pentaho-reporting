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

package org.pentaho.reporting.engine.classic.extensions.legacy.charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.plot.ThermometerPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xy.DefaultIntervalXYDataset;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.util.TableOrder;
import org.pentaho.plugin.jfreereport.reportcharts.AbstractChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.AreaChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.BarChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.BarLineChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.BubbleChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.ExtendedXYLineChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.LineChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.MultiPieChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.PieChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.RadarChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.RingChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.ScatterPlotChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.ThermometerChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.WaterfallChartExpressions;
import org.pentaho.plugin.jfreereport.reportcharts.XYAreaChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.XYAreaLineChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.XYBarChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.XYLineChartExpression;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.filter.types.ContentFieldType;
import org.pentaho.reporting.engine.classic.core.filter.types.ElementTypeUtils;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;

import java.util.Locale;

public class LegacyChartType extends ContentFieldType {
  public static final LegacyChartType INSTANCE = new LegacyChartType();
  private transient ElementMetaData elementType;

  public LegacyChartType() {
    super( "legacy-chart" );
  }

  public Object getDesignValue( final ExpressionRuntime runtime, final ReportElement element ) {
    final Expression theExpression =
      element.getAttributeExpression( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE );
    return createChart( theExpression );
  }

  private XYZDataset createXYZDataset() {
    final DefaultXYZDataset xyzDataset = new DefaultXYZDataset();
    final double bs = 3;
    xyzDataset
      .addSeries( "First", new double[][] { { 1, 2, 3 }, { 2, 1, 3 }, { 0.1 * bs, 0.2 * bs, 0.1 * bs } } );// NON-NLS
    xyzDataset
      .addSeries( "Second", new double[][] { { 1, 2, 3 }, { 3, 0, 1 }, { 0.2 * bs, 0.1 * bs, 0.15 * bs } } );// NON-NLS
    return xyzDataset;
  }

  private PieDataset createPieDataset() {
    final DefaultPieDataset dataset = new DefaultPieDataset();
    dataset.setValue( "Part 1", 23 );// NON-NLS
    dataset.setValue( "Part 2", 35 );// NON-NLS
    dataset.setValue( "Part 3", 42 );// NON-NLS
    return dataset;
  }

  private XYDataset createIntervalXYDataset() {
    final DefaultIntervalXYDataset dataset = new DefaultIntervalXYDataset();
    dataset.addSeries( "First",
      new double[][] { { 1, 2, 3 }, { 3, 1, 2 }, { 1, 2, 1 }, { 4, 4, 4 }, { 3, 3, 3 }, { 4, 4, 4 }, } );// NON-NLS
    // dataset.addSeries("Second", new double[][]{{1, 2, 3}, {3, 0, 1}, {1, 2,
    // 3}, {3, 0, 1}, {1, 2, 3}, {3, 0, 1}, });//NON-NLS
    return dataset;
  }

  private CategoryDataset createDataset() {
    final String series1 = "First";// NON-NLS
    final String series2 = "Second";// NON-NLS
    final String series3 = "Third";// NON-NLS
    final String category1 = "Category 1";// NON-NLS
    final String category2 = "Category 2";// NON-NLS
    final String category3 = "Category 3";// NON-NLS
    final String category4 = "Category 4";// NON-NLS
    final String category5 = "Category 5";// NON-NLS
    final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    dataset.addValue( 1D, series1, category1 );
    dataset.addValue( 5D, series1, category2 );
    dataset.addValue( 4D, series1, category3 );
    dataset.addValue( 8D, series1, category4 );
    dataset.addValue( 7D, series1, category5 );

    dataset.addValue( 3D, series2, category1 );
    dataset.addValue( 4D, series2, category2 );
    dataset.addValue( 3D, series2, category3 );
    dataset.addValue( 5D, series2, category4 );
    dataset.addValue( 4D, series2, category5 );

    dataset.addValue( 1D, series3, category1 );
    dataset.addValue( 3D, series3, category2 );
    dataset.addValue( 2D, series3, category3 );
    dataset.addValue( 3D, series3, category4 );
    dataset.addValue( 2D, series3, category5 );

    return dataset;
  }


  public Object getValue( final ExpressionRuntime runtime, final ReportElement element ) {
    if ( runtime == null ) {
      throw new NullPointerException( "Runtime must never be null." );
    }
    if ( element == null ) {
      throw new NullPointerException( "Element must never be null." );
    }

    final Object value = ElementTypeUtils.queryStaticValue( element );
    if ( value != null ) {
      final Object filteredValue = filter( runtime, element, value );
      if ( filteredValue != null ) {
        return filteredValue;
      }
    }
    final Object nullValue = element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE );
    return filter( runtime, element, nullValue );
  }

  public void configureDesignTimeDefaults( final ReportElement element, final Locale locale ) {
    final AbstractChartExpression theExpression = new BarChartExpression();
    element.setAttributeExpression( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, theExpression );
    element.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( 280 ) );
    element.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, new Float( 190 ) );
  }

  private JFreeChart createChart( final Expression aExpression ) {
    if ( aExpression instanceof BarLineChartExpression ) {
      final CategoryAxis catAxis = new CategoryAxis( "Category" );// NON-NLS
      final NumberAxis barsAxis = new NumberAxis( "Value" );// NON-NLS
      final NumberAxis linesAxis = new NumberAxis( "Value2" );// NON-NLS

      final CategoryPlot plot = new CategoryPlot( createDataset(), catAxis, barsAxis, new BarRenderer() );
      plot.setRenderer( 1, new LineAndShapeRenderer() );

      // add lines dataset and axis to plot
      plot.setDataset( 1, createDataset() );
      plot.setRangeAxis( 1, linesAxis );

      // map lines to second axis
      plot.mapDatasetToRangeAxis( 1, 1 );

      // set rendering order
      plot.setDatasetRenderingOrder( DatasetRenderingOrder.FORWARD );

      // set location of second axis
      plot.setRangeAxisLocation( 1, AxisLocation.BOTTOM_OR_RIGHT );

      return new JFreeChart( "Bar Line Chart", plot );
    }

    if ( aExpression instanceof RingChartExpression ) {
      return ChartFactory.createRingChart( "Ring Chart", createPieDataset(), true, false, false );// NON-NLS
    }
    if ( aExpression instanceof AreaChartExpression ) {
      return ChartFactory.createAreaChart( "Area Chart", "Category", "Value", createDataset(), PlotOrientation.VERTICAL,
        true, false, false );// NON-NLS
    }
    if ( aExpression instanceof BarChartExpression ) {
      return ChartFactory.createBarChart( "Bar Chart", "Category", "Value", createDataset(), PlotOrientation.VERTICAL,
        true, false, false );// NON-NLS

    }
    if ( aExpression instanceof LineChartExpression ) {
      return ChartFactory.createLineChart( "Line Chart", "Category", "Value", createDataset(), PlotOrientation.VERTICAL,
        true, false, false );// NON-NLS
    }
    if ( aExpression instanceof MultiPieChartExpression ) {
      return ChartFactory.createMultiplePieChart( "Multi Pie Chart", createDataset(), TableOrder.BY_COLUMN, true, false,
        false );// NON-NLS
    }
    if ( aExpression instanceof PieChartExpression ) {
      return ChartFactory.createPieChart( "Pie Chart", createPieDataset(), true, false, false );// NON-NLS
    }
    if ( aExpression instanceof WaterfallChartExpressions ) {
      return ChartFactory.createWaterfallChart( "Bar Chart", "Category", "Value", createDataset(),
        PlotOrientation.HORIZONTAL, true, false, false );// NON-NLS
    }
    if ( aExpression instanceof BubbleChartExpression ) {
      return ChartFactory.createBubbleChart( "Bubble Chart", "X", "Y", createXYZDataset(), PlotOrientation.VERTICAL,
        true, false, false );// NON-NLS
    }
    if ( aExpression instanceof ExtendedXYLineChartExpression ) {
      return ChartFactory.createXYLineChart( "XY Line Chart", "X", "Y", createXYZDataset(), PlotOrientation.VERTICAL,
        true, false, false );// NON-NLS
    }
    if ( aExpression instanceof ScatterPlotChartExpression ) {
      return ChartFactory.createScatterPlot( "Scatter Chart", "X", "Y", createXYZDataset(), PlotOrientation.VERTICAL,
        true, false, false );// NON-NLS
    }
    if ( aExpression instanceof XYAreaLineChartExpression ) {
      final NumberAxis catAxis = new NumberAxis( "Range" );// NON-NLS
      final NumberAxis barsAxis = new NumberAxis( "Value" );// NON-NLS
      final NumberAxis linesAxis = new NumberAxis( "Value2" );// NON-NLS

      final XYPlot plot = new XYPlot( createXYZDataset(), catAxis, barsAxis, new XYAreaRenderer() );
      plot.setRenderer( 1, new XYLineAndShapeRenderer() );

      // add lines dataset and axis to plot
      plot.setDataset( 1, createXYZDataset() );
      plot.setRangeAxis( 1, linesAxis );

      // map lines to second axis
      plot.mapDatasetToRangeAxis( 1, 1 );

      // set rendering order
      plot.setDatasetRenderingOrder( DatasetRenderingOrder.FORWARD );

      // set location of second axis
      plot.setRangeAxisLocation( 1, AxisLocation.BOTTOM_OR_RIGHT );

      return new JFreeChart( "XY Area Line Chart", plot );// NON-NLS
    }
    if ( aExpression instanceof XYAreaChartExpression ) {
      return ChartFactory.createXYAreaChart( "XY Area Chart", "X", "Y", createXYZDataset(), PlotOrientation.VERTICAL,
        true, false, false );// NON-NLS
    }
    if ( aExpression instanceof XYBarChartExpression ) {
      return XYBarChartExpression.createXYBarChart( "XY Bar Chart", "X", false, "Y", createIntervalXYDataset(),
        PlotOrientation.VERTICAL, true, false, false );// NON-NLS
    }
    if ( aExpression instanceof XYLineChartExpression ) {
      return ChartFactory.createXYLineChart( "XY Line Chart", "X", "Y", createXYZDataset(), PlotOrientation.VERTICAL,
        true, false, false );// NON-NLS
    }
    if ( aExpression instanceof RadarChartExpression ) {
      final SpiderWebPlot plot = new SpiderWebPlot( createDataset() );
      return new JFreeChart( "Radar Chart", JFreeChart.DEFAULT_TITLE_FONT, plot, true );
    }
    if ( aExpression instanceof ThermometerChartExpression ) {
      final DefaultValueDataset dataset = new DefaultValueDataset( new Double( 65.0 ) );
      final ThermometerPlot plot = new ThermometerPlot( dataset );

      return new JFreeChart( "Thermometer Chart", JFreeChart.DEFAULT_TITLE_FONT, plot, true );
    }
    return null;
  }
}
