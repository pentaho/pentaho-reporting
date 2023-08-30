/*
* Copyright 2002 - 2017 Hitachi Vantara.  All rights reserved.
* 
* This software was developed by Hitachi Vantara and is provided under the terms
* of the Mozilla Public License, Version 1.1, or any later version. You may not use
* this file except in compliance with the license. If you need a copy of the license,
* please go to http://www.mozilla.org/MPL/MPL-1.1.txt. TThe Initial Developer is Pentaho Corporation.
*
* Software distributed under the Mozilla Public License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
* the license for the specific language governing your rights and limitations.
*/

package org.pentaho.plugin.jfreereport.reportcharts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYStepAreaRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.UnitType;

public class ExtendedXYLineChartExpression extends XYLineChartExpression {

  public static final String STEP_CHART_STR = "StepChart"; //$NON-NLS-1$
  public static final String STEP_AREA_CHART_STR = "StepAreaChart"; //$NON-NLS-1$
  public static final String DIFFERENCE_CHART_STR = "DifferenceChart"; //$NON-NLS-1$


  private static final long serialVersionUID = -167639029520233427L;
  private String chartType;


  public ExtendedXYLineChartExpression() {
    chartType = null;
  }

  protected JFreeChart computeXYChart( final XYDataset xyDataset ) {
    final JFreeChart rtn;
    if ( xyDataset instanceof TimeSeriesCollection ) {
      rtn =
        ChartFactory.createTimeSeriesChart( computeTitle(), getDomainTitle(), getRangeTitle(), xyDataset,
          isShowLegend(), false, false );
      rtn.getXYPlot().getDomainAxis().setLowerMargin( 0.025 );
      rtn.getXYPlot().getDomainAxis().setUpperMargin( 0.025 );
      rtn.getXYPlot().setInsets( new RectangleInsets( UnitType.ABSOLUTE, 0, 0, 0, 15 ) );
    } else {
      final PlotOrientation orientation = computePlotOrientation();
      rtn = ChartFactory.createXYLineChart( computeTitle(), getDomainTitle(), getRangeTitle(),
        xyDataset, orientation, isShowLegend(), false, false );
    }

    final String chartType = getChartType();
    if ( STEP_AREA_CHART_STR.equals( chartType ) ) {
      final XYItemRenderer renderer;
      if ( isMarkersVisible() ) {
        renderer = new XYStepAreaRenderer( XYStepAreaRenderer.AREA_AND_SHAPES );
      } else {
        renderer = new XYStepAreaRenderer( XYStepAreaRenderer.AREA );
      }
      rtn.getXYPlot().setRenderer( renderer );
    } else if ( STEP_CHART_STR.equals( chartType ) ) {
      rtn.getXYPlot().setRenderer( new XYStepRenderer( null, null ) );
    } else if ( DIFFERENCE_CHART_STR.equals( chartType ) ) {
      rtn.getXYPlot().setRenderer( new XYDifferenceRenderer() );
    }
    configureLogarithmicAxis( rtn.getXYPlot() );
    return rtn;
  }

  public String getChartType() {
    return chartType;
  }

  public void setChartType( final String chartType ) {
    this.chartType = chartType;
  }


}
