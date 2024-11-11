/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.plugin.jfreereport.reportcharts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.UnitType;

public class XYLineChartExpression extends XYChartExpression {
  private static final long serialVersionUID = 588996014868712814L;

  private String lineStyle;
  private float lineWidth;
  private boolean markersVisible;

  public XYLineChartExpression() {
    lineWidth = 1.0f;
  }


  protected JFreeChart computeXYChart( final XYDataset xyDataset ) {
    final JFreeChart chart;
    if ( xyDataset instanceof TimeSeriesCollection ) {
      chart = ChartFactory.createTimeSeriesChart( computeTitle(), getDomainTitle(), getRangeTitle(), xyDataset,
        isShowLegend(), false, false );
      chart.getXYPlot().getDomainAxis().setLowerMargin( 0.025 );
      chart.getXYPlot().getDomainAxis().setUpperMargin( 0.025 );
      chart.getXYPlot().setInsets( new RectangleInsets( UnitType.ABSOLUTE, 0, 0, 0, 15 ) );
    } else {
      final PlotOrientation orientation = computePlotOrientation();
      chart = ChartFactory.createXYLineChart( computeTitle(), getDomainTitle(), getRangeTitle(),
        xyDataset, orientation, isShowLegend(), false, false );
    }
    configureLogarithmicAxis( chart.getXYPlot() );
    return chart;
  }


  protected void configureChart( final JFreeChart chart ) {
    super.configureChart( chart );

    final XYPlot xypl = chart.getXYPlot();
    final XYItemRenderer renderer = xypl.getRenderer();
    renderer.setStroke( translateLineStyle( lineWidth, lineStyle ) );
    if ( renderer instanceof XYLineAndShapeRenderer ) {
      final XYLineAndShapeRenderer renderer1 = (XYLineAndShapeRenderer) renderer;
      renderer1.setShapesVisible( isMarkersVisible() );
      renderer1.setBaseShapesFilled( isMarkersVisible() );
    }

  }

  /**
   * @return returns the style set for the lines
   */
  public String getLineStyle() {
    return lineStyle;
  }

  /**
   * @param value set the style for all line series
   */
  public void setLineStyle( final String value ) {
    lineStyle = value;
  }

  /**
   * @return the width of all line series Valid values are float numbers zero or greater
   */
  public float getLineWidth() {
    return lineWidth;
  }

  /**
   * @param value set the width of all line series Valid values are float numbers zero or greater
   */
  public void setLineWidth( final float value ) {
    lineWidth = value;
  }

  /**
   * @return boolean whether the markers (data points) for all series are displayed
   */
  public boolean isMarkersVisible() {
    return markersVisible;
  }

  /**
   * @param markersVisible set whether the markers (data points) for all series should be displayed
   */
  public void setMarkersVisible( final boolean markersVisible ) {
    this.markersVisible = markersVisible;
  }

}
