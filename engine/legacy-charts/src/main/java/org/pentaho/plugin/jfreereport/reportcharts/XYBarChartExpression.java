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

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StackedXYBarRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.UnitType;
import org.pentaho.plugin.jfreereport.reportcharts.backport.ExtTimeTableXYDataset;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;

/**
 * @author gmoran
 */
public class XYBarChartExpression extends XYChartExpression {
  private static final long serialVersionUID = -1190325024526412335L;

  private Double margin;
  private boolean renderPercentages;
  private boolean shadowVisible;
  private int shadowXOffset;
  private int shadowYOffset;

  public XYBarChartExpression() {
  }

  public boolean isShadowVisible() {
    return shadowVisible;
  }

  public void setShadowVisible( final boolean shadowVisible ) {
    this.shadowVisible = shadowVisible;
  }

  public int getShadowXOffset() {
    return shadowXOffset;
  }

  public void setShadowXOffset( final int shadowXOffset ) {
    this.shadowXOffset = shadowXOffset;
  }

  public int getShadowYOffset() {
    return shadowYOffset;
  }

  public void setShadowYOffset( final int shadowYOffset ) {
    this.shadowYOffset = shadowYOffset;
  }

  public Double getMargin() {
    return margin;
  }

  public void setMargin( final Double margin ) {
    this.margin = margin;
  }

  public boolean isRenderPercentages() {
    return renderPercentages;
  }

  public void setRenderPercentages( final boolean renderPercentages ) {
    this.renderPercentages = renderPercentages;
  }

  protected static JFreeChart createTimeSeriesChart( final String title,
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

    final XYBarRenderer renderer;
    if ( stacked ) {
      renderer = new StackedXYBarRenderer();
    } else {
      renderer = new XYBarRenderer();
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
          isShowLegend(), false, false, true );
      } else {
        chart = createTimeSeriesChart( computeTitle(), getDomainTitle(), getRangeTitle(), xyDataset,
          isShowLegend(), false, false, false );
      }
    } else {
      final PlotOrientation orientation = computePlotOrientation();
      if ( isStacked() && xyDataset instanceof XYSeriesCollection ) {
        final XYSeriesCollection xySeriesCollection = (XYSeriesCollection) xyDataset;
        chart = createStackedXYBarChart( computeTitle(), getDomainTitle(), false, getRangeTitle(),
          convertToTable( xySeriesCollection ), orientation, isShowLegend(), false, false );
      } else if ( isStacked() && xyDataset instanceof TableXYDataset ) {
        final TableXYDataset dataset = (TableXYDataset) xyDataset;
        chart = createStackedXYBarChart( computeTitle(), getDomainTitle(), false, getRangeTitle(),
          dataset, orientation, isShowLegend(), false, false );
      } else {
        chart = createXYBarChart( computeTitle(), getDomainTitle(), false, getRangeTitle(),
          xyDataset, orientation, isShowLegend(), false, false );
      }
    }

    configureLogarithmicAxis( chart.getXYPlot() );
    return chart;
  }


  /**
   * Creates and returns a default instance of an XY bar chart.
   * <p/>
   * The chart object returned by this method uses an {@link XYPlot} instance as the plot, with a {@link
   * org.jfree.chart.axis.DateAxis} for the domain axis, a {@link org.jfree.chart.axis.NumberAxis} as the range axis,
   * and a {@link XYBarRenderer} as the renderer.
   *
   * @param title       the chart title (<code>null</code> permitted).
   * @param xAxisLabel  a label for the X-axis (<code>null</code> permitted).
   * @param dateAxis    make the domain axis display dates?
   * @param yAxisLabel  a label for the Y-axis (<code>null</code> permitted).
   * @param dataset     the dataset for the chart (<code>null</code> permitted).
   * @param orientation the orientation (horizontal or vertical) (<code>null</code> NOT permitted).
   * @param legend      a flag specifying whether or not a legend is required.
   * @param tooltips    configure chart to generate tool tips?
   * @param urls        configure chart to generate URLs?
   * @return An XY bar chart.
   */
  public static JFreeChart createXYBarChart( final String title,
                                             final String xAxisLabel,
                                             final boolean dateAxis,
                                             final String yAxisLabel,
                                             final XYDataset dataset,
                                             final PlotOrientation orientation,
                                             final boolean legend,
                                             final boolean tooltips,
                                             final boolean urls ) {

    if ( orientation == null ) {
      throw new IllegalArgumentException( "Null 'orientation' argument." );
    }
    ValueAxis domainAxis = null;
    if ( dateAxis ) {
      domainAxis = new DateAxis( xAxisLabel );
    } else {
      final NumberAxis axis = new NumberAxis( xAxisLabel );
      axis.setAutoRangeIncludesZero( false );
      domainAxis = axis;
    }
    final ValueAxis valueAxis = new NumberAxis( yAxisLabel );

    final XYBarRenderer renderer = new XYBarRenderer();
    renderer.setUseYInterval( true );
    if ( tooltips ) {
      final XYToolTipGenerator tt;
      if ( dateAxis ) {
        tt = StandardXYToolTipGenerator.getTimeSeriesInstance();
      } else {
        tt = new StandardXYToolTipGenerator();
      }
      renderer.setBaseToolTipGenerator( tt );
    }
    if ( urls ) {
      renderer.setURLGenerator( new StandardXYURLGenerator() );
    }

    final XYPlot plot = new XYPlot( dataset, domainAxis, valueAxis, renderer );
    plot.setOrientation( orientation );

    return new JFreeChart( title, JFreeChart.DEFAULT_TITLE_FONT,
      plot, legend );
  }

  private static JFreeChart createStackedXYBarChart( final String title,
                                                     final String xAxisLabel,
                                                     final boolean dateAxis,
                                                     final String yAxisLabel,
                                                     final TableXYDataset dataset,
                                                     final PlotOrientation orientation,
                                                     final boolean legend,
                                                     final boolean tooltips,
                                                     final boolean urls ) {

    if ( orientation == null ) {
      throw new IllegalArgumentException( "Null 'orientation' argument." );
    }
    ValueAxis domainAxis = null;
    if ( dateAxis ) {
      domainAxis = new DateAxis( xAxisLabel );
    } else {
      final NumberAxis axis = new NumberAxis( xAxisLabel );
      axis.setAutoRangeIncludesZero( false );
      domainAxis = axis;
    }
    final ValueAxis valueAxis = new NumberAxis( yAxisLabel );

    final StackedXYBarRenderer renderer = new StackedXYBarRenderer();
    renderer.setUseYInterval( true );
    if ( tooltips ) {
      final XYToolTipGenerator tt;
      if ( dateAxis ) {
        tt = StandardXYToolTipGenerator.getTimeSeriesInstance();
      } else {
        tt = new StandardXYToolTipGenerator();
      }
      renderer.setBaseToolTipGenerator( tt );
    }
    if ( urls ) {
      renderer.setURLGenerator( new StandardXYURLGenerator() );
    }

    final XYPlot plot = new XYPlot( dataset, domainAxis, valueAxis, renderer );
    plot.setOrientation( orientation );

    return new JFreeChart( title, JFreeChart.DEFAULT_TITLE_FONT,
      plot, legend );
  }


  protected void configureChart( final JFreeChart chart ) {
    super.configureChart( chart );

    final XYPlot xypl = chart.getXYPlot();
    final XYItemRenderer renderer = xypl.getRenderer();
    final XYBarRenderer br = (XYBarRenderer) renderer;
    br.setDrawBarOutline( isChartSectionOutline() );
    if ( margin != null ) {
      br.setMargin( margin.doubleValue() );
    }

    br.setShadowVisible( shadowVisible );
    br.setShadowXOffset( shadowXOffset );
    br.setShadowYOffset( shadowYOffset );

    if ( ( isStacked() ) && renderPercentages && ( br instanceof StackedXYBarRenderer ) ) {
      final StackedXYBarRenderer sbr = (StackedXYBarRenderer) br;
      sbr.setRenderAsPercentages( true );

      final ValueAxis rangeAxis = xypl.getRangeAxis();
      final int level = getRuntime().getProcessingContext().getCompatibilityLevel();
      if ( ClassicEngineBoot.isEnforceCompatibilityFor( level, 3, 8 ) ) {
        if ( getRangeMinimum() != 0 ) {
          rangeAxis.setLowerBound( getRangeMinimum() );
        }
        if ( getRangeMaximum() != 1 ) {
          rangeAxis.setUpperBound( getRangeMaximum() );
        }
        if ( getRangeMinimum() == 0 && getRangeMaximum() == 0 ) {
          rangeAxis.setLowerBound( 0 );
          rangeAxis.setUpperBound( 1 );
          rangeAxis.setAutoRange( true );
        }
      } else {
        rangeAxis.setLowerBound( getRangeMinimum() );
        rangeAxis.setUpperBound( getRangeMaximum() );
        rangeAxis.setAutoRange( isRangeAxisAutoRange() );
      }
    }

  }

  /**
   * @return
   * @deprecated This maps directly to chartSectionOutline
   */
  public boolean isDrawBarOutline() {
    return isChartSectionOutline();
  }

  /**
   * @deprecated This maps directly to chartSectionOutline
   */
  public void setDrawBarOutline( final boolean value ) {
    setChartSectionOutline( value );
  }

}
