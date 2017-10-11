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
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.ThermometerPlot;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.ValueDataset;

import java.awt.*;

@SuppressWarnings( "UnusedDeclaration" )
public class ThermometerChartExpression extends AbstractChartExpression {
  private int bulbRadius;
  private int columnRadius;
  private ThermometerUnit thermometerUnits;
  private int criticalRangeHigh;
  private int criticalRangeLow;
  private int warningRangeHigh;
  private int warningRangeLow;
  private int normalRangeHigh;
  private int normalRangeLow;
  private Color mercuryPaint;
  private Color thermometerPaint;
  private Color warningRangeColor;
  private Color criticalRangeColor;
  private Color normalRangeColor;

  public ThermometerChartExpression() {
    this.bulbRadius = ThermometerPlotDefaults.getDefaultBulbRadius();
    this.columnRadius = ThermometerPlotDefaults.getDefaultColumnRadius();
    this.thermometerUnits = null;
    criticalRangeHigh = 100;
    criticalRangeLow = 75;
    warningRangeHigh = 75;
    warningRangeLow = 30;
    normalRangeHigh = 30;
    normalRangeLow = 0;
  }

  public Color getWarningRangeColor() {
    return warningRangeColor;
  }

  public void setWarningRangeColor( final Color warningRangeColor ) {
    this.warningRangeColor = warningRangeColor;
  }

  public Color getCriticalRangeColor() {
    return criticalRangeColor;
  }

  public void setCriticalRangeColor( final Color criticalRangeColor ) {
    this.criticalRangeColor = criticalRangeColor;
  }

  public Color getNormalRangeColor() {
    return normalRangeColor;
  }

  public void setNormalRangeColor( final Color normalRangeColor ) {
    this.normalRangeColor = normalRangeColor;
  }

  public int getBulbRadius() {
    return this.bulbRadius;
  }

  public void setBulbRadius( final int bulbRadius ) {
    this.bulbRadius = bulbRadius;
  }

  public int getColumnRadius() {
    return this.columnRadius;
  }

  public void setColumnRadius( final int columnRadius ) {
    this.columnRadius = columnRadius;
  }

  public ThermometerUnit getThermometerUnits() {
    return this.thermometerUnits;
  }

  public void setThermometerUnits( final ThermometerUnit thermometerUnits ) {
    this.thermometerUnits = thermometerUnits;
  }

  public int getCriticalRangeHigh() {
    return this.criticalRangeHigh;
  }

  public void setCriticalRangeHigh( final int criticalRangeHigh ) {
    this.criticalRangeHigh = criticalRangeHigh;
  }

  public int getCriticalRangeLow() {
    return this.criticalRangeLow;
  }

  public void setCriticalRangeLow( final int criticalRangeLow ) {
    this.criticalRangeLow = criticalRangeLow;
  }

  public int getWarningRangeHigh() {
    return this.warningRangeHigh;
  }

  public void setWarningRangeHigh( final int warningRangeHigh ) {
    this.warningRangeHigh = warningRangeHigh;
  }

  public int getWarningRangeLow() {
    return this.warningRangeLow;
  }

  public void setWarningRangeLow( final int warningRangeLow ) {
    this.warningRangeLow = warningRangeLow;
  }

  public int getNormalRangeHigh() {
    return this.normalRangeHigh;
  }

  public void setNormalRangeHigh( final int normalRangeHigh ) {
    this.normalRangeHigh = normalRangeHigh;
  }

  public int getNormalRangeLow() {
    return this.normalRangeLow;
  }

  public void setNormalRangeLow( final int normalRangeLow ) {
    this.normalRangeLow = normalRangeLow;
  }

  public Color getMercuryPaint() {
    return mercuryPaint;
  }

  public void setMercuryPaint( final Color mercuryPaint ) {
    this.mercuryPaint = mercuryPaint;
  }

  public Color getThermometerPaint() {
    return thermometerPaint;
  }

  public void setThermometerPaint( final Color thermometerPaint ) {
    this.thermometerPaint = thermometerPaint;
  }


  protected JFreeChart computeChart( final Dataset dataset ) {
    ValueDataset thermometerDataset = null;
    if ( dataset instanceof ValueDataset ) {
      thermometerDataset = (ValueDataset) dataset;
    }

    final ThermometerPlot plot = new ThermometerPlot( thermometerDataset );
    return new JFreeChart( computeTitle(), JFreeChart.DEFAULT_TITLE_FONT, plot, true );
  }

  protected void configureChart( final JFreeChart chart ) {
    super.configureChart( chart );

    final Plot plot = chart.getPlot();
    final ThermometerPlot thermometerPlot = (ThermometerPlot) plot;

    if ( isShowBorder() == false || isChartSectionOutline() == false ) {
      chart.setBorderVisible( false );
      thermometerPlot.setOutlineVisible( false );
    }

    if ( getThermometerUnits() != null ) {
      thermometerPlot.setUnits( getThermometerUnits().getUnitConstant() );
    }
    thermometerPlot
      .setLowerBound( Math.min( getCriticalRangeLow(), Math.min( getNormalRangeLow(), getWarningRangeLow() ) ) );
    thermometerPlot
      .setUpperBound( Math.max( getCriticalRangeHigh(), Math.max( getNormalRangeHigh(), getWarningRangeHigh() ) ) );
    thermometerPlot.setBulbRadius( getBulbRadius() );
    thermometerPlot.setColumnRadius( getColumnRadius() );
    thermometerPlot.setSubrange( ThermometerPlot.CRITICAL, getCriticalRangeLow(), getCriticalRangeHigh() );
    thermometerPlot.setSubrange( ThermometerPlot.WARNING, getWarningRangeLow(), getWarningRangeHigh() );
    thermometerPlot.setSubrange( ThermometerPlot.NORMAL, getNormalRangeLow(), getNormalRangeHigh() );
    if ( getMercuryPaint() != null ) {
      thermometerPlot.setMercuryPaint( getMercuryPaint() );
    }
    if ( getThermometerPaint() != null ) {
      thermometerPlot.setThermometerPaint( getThermometerPaint() );
    }

    if ( getCriticalRangeColor() != null ) {
      thermometerPlot.setSubrangePaint( ThermometerPlot.CRITICAL, getCriticalRangeColor() );
    }
    if ( getWarningRangeColor() != null ) {
      thermometerPlot.setSubrangePaint( ThermometerPlot.WARNING, getWarningRangeColor() );
    }
    if ( getNormalRangeColor() != null ) {
      thermometerPlot.setSubrangePaint( ThermometerPlot.NORMAL, getNormalRangeColor() );
    }
  }

  private static class ThermometerPlotDefaults extends ThermometerPlot {
    public static int getDefaultBulbRadius() {
      return ThermometerPlot.DEFAULT_BULB_RADIUS;
    }

    public static int getDefaultColumnRadius() {
      return ThermometerPlot.DEFAULT_COLUMN_RADIUS;
    }
  }
}
