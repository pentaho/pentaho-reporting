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
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.pentaho.plugin.jfreereport.reportcharts.backport.FormattedCategoryAxis;
import org.pentaho.plugin.jfreereport.reportcharts.backport.FormattedCategoryAxis3D;

public class LineChartExpression extends CategoricalChartExpression {

  private static final long serialVersionUID = 816438776025760907L;

  private String lineStyle;
  private float lineWidth;
  private boolean markersVisible;

  public LineChartExpression() {
    lineWidth = 1.0f;
    markersVisible = false;
  }

  protected JFreeChart computeCategoryChart( final CategoryDataset dataset ) {
    final PlotOrientation orientation = computePlotOrientation();
    if ( isThreeD() ) {
      final JFreeChart chart =
        ChartFactory.createLineChart3D( computeTitle(), getCategoryAxisLabel(), getValueAxisLabel(), dataset,
          orientation, isShowLegend(), false, false );
      chart.getCategoryPlot().setDomainAxis( new FormattedCategoryAxis3D( getCategoryAxisLabel(),
        getCategoricalAxisMessageFormat(), getRuntime().getResourceBundleFactory().getLocale() ) );
      final CategoryPlot plot = (CategoryPlot) chart.getPlot();
      configureLogarithmicAxis( plot );
      return chart;
    } else {
      final JFreeChart chart =
        ChartFactory.createLineChart( computeTitle(), getCategoryAxisLabel(), getValueAxisLabel(), dataset,
          orientation, isShowLegend(), false, false );
      chart.getCategoryPlot().setDomainAxis( new FormattedCategoryAxis( getCategoryAxisLabel(),
        getCategoricalAxisMessageFormat(), getRuntime().getResourceBundleFactory().getLocale() ) );
      final CategoryPlot plot = (CategoryPlot) chart.getPlot();
      configureLogarithmicAxis( plot );
      return chart;
    }
  }

  protected void configureChart( final JFreeChart chart ) {
    super.configureChart( chart );

    final CategoryPlot cpl = chart.getCategoryPlot();
    final CategoryItemRenderer renderer = cpl.getRenderer();
    renderer.setStroke( translateLineStyle( lineWidth, lineStyle ) );
    if ( renderer instanceof LineAndShapeRenderer ) {
      final LineAndShapeRenderer shapeRenderer = (LineAndShapeRenderer) renderer;
      shapeRenderer.setShapesVisible( isMarkersVisible() );
      shapeRenderer.setBaseShapesFilled( isMarkersVisible() );
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
