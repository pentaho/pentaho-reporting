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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.plugin.jfreereport.reportcharts;

import java.awt.Color;
import java.awt.Paint;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.ThermometerPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.ValueDataset;

final class ThermometerPlotDefaults extends ThermometerPlot
{
  public static final int getDefaultBulbRadius()
  {
    return ThermometerPlot.DEFAULT_BULB_RADIUS;
  }

  public static final int getDefaultColumnRadius()
  {
    return ThermometerPlot.DEFAULT_COLUMN_RADIUS;
  }
}

public class ThermometerChartExpression extends AbstractChartExpression
{
  private static final long serialVersionUID = 1L; // TODO

  private int bulbRadius;
  private int columnRadius;
  private String thermometerUnits;
  private int criticalRangeHigh;
  private int criticalRangeLow;
  private int warningRangeHigh;
  private int warningRangeLow;
  private int normalRangeHigh;
  private int normalRangeLow;

  private static final Map<String, Integer> THERMOMETER_UNITS;

  static
  {
    final Map<String, Integer> unitMap = new HashMap<String, Integer>();
    unitMap.put("celcius", ThermometerPlot.UNITS_CELCIUS);
    unitMap.put("farenheit", ThermometerPlot.UNITS_FAHRENHEIT);
    unitMap.put("kelvin", ThermometerPlot.UNITS_KELVIN);
    unitMap.put("none", ThermometerPlot.UNITS_NONE);
    THERMOMETER_UNITS = Collections.unmodifiableMap(unitMap);
  }

  public int getBulbRadius()
  {
    return this.bulbRadius;
  }

  public void setBulbRadius(int bulbRadius)
  {
    this.bulbRadius = bulbRadius;
  }

  public int getColumnRadius()
  {
    return this.columnRadius;
  }

  public void setColumnRadius(int columnRadius)
  {
    this.columnRadius = columnRadius;
  }

  public String getThermometerUnits()
  {
    return this.thermometerUnits;
  }

  public void setThermometerUnits(String thermometerUnits)
  {
    this.thermometerUnits = thermometerUnits;
  }

  public int getCriticalRangeHigh()
  {
    return this.criticalRangeHigh;
  }

  public void setCriticalRangeHigh(int criticalRangeHigh)
  {
    this.criticalRangeHigh = criticalRangeHigh;
  }

  public int getCriticalRangeLow()
  {
    return this.criticalRangeLow;
  }

  public void setCriticalRangeLow(int criticalRangeLow)
  {
    this.criticalRangeLow = criticalRangeLow;
  }

  public int getWarningRangeHigh()
  {
    return this.warningRangeHigh;
  }

  public void setWarningRangeHigh(int warningRangeHigh)
  {
    this.warningRangeHigh = warningRangeHigh;
  }

  public int getWarningRangeLow()
  {
    return this.warningRangeLow;
  }

  public void setWarningRangeLow(int warningRangeLow)
  {
    this.warningRangeLow = warningRangeLow;
  }

  public int getNormalRangeHigh()
  {
    return this.normalRangeHigh;
  }

  public void setNormalRangeHigh(int normalRangeHigh)
  {
    this.normalRangeHigh = normalRangeHigh;
  }

  public int getNormalRangeLow()
  {
    return this.normalRangeLow;
  }

  public void setNormalRangeLow(int normalRangeLow)
  {
    this.normalRangeLow = normalRangeLow;
  }

  public ThermometerChartExpression()
  {
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

  protected JFreeChart computeChart(final Dataset dataset)
  {
    ValueDataset thermometerDataset = null;
    if (dataset instanceof ValueDataset)
    {
      thermometerDataset = (ValueDataset) dataset;
    }

    final ThermometerPlot plot = new ThermometerPlot(thermometerDataset);
    return new JFreeChart(computeTitle(), JFreeChart.DEFAULT_TITLE_FONT, plot, true);
  }

  protected void configureChart(final JFreeChart chart)
  {
    super.configureChart(chart);

    final Plot plot = chart.getPlot();
    final ThermometerPlot thermometerPlot = (ThermometerPlot)plot;
    final ValueDataset valueDS = thermometerPlot.getDataset();

    if (isShowBorder() == false || isChartSectionOutline() == false)
    {
      chart.setBorderVisible(false);
      thermometerPlot.setOutlineVisible(false);
    }

    if (getThermometerUnits() != null) {
      thermometerPlot.setUnits(THERMOMETER_UNITS.get(getThermometerUnits().toLowerCase()));
    }
    thermometerPlot.setBulbRadius(getBulbRadius());
    thermometerPlot.setColumnRadius(getColumnRadius());
    thermometerPlot.setSubrange(ThermometerPlot.CRITICAL, getCriticalRangeLow(), getCriticalRangeHigh());
    thermometerPlot.setSubrange(ThermometerPlot.WARNING, getWarningRangeLow(), getWarningRangeHigh());
    thermometerPlot.setSubrange(ThermometerPlot.NORMAL, getNormalRangeLow(), getNormalRangeHigh());
  }

}
