/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.plugin.jfreereport.reportcharts;

import org.jfree.chart.plot.ThermometerPlot;

public enum ThermometerUnit {
  None( ThermometerPlot.UNITS_NONE ),
  Fahrenheit( ThermometerPlot.UNITS_FAHRENHEIT ),
  Celsius( ThermometerPlot.UNITS_CELCIUS ),
  Kelvin( ThermometerPlot.UNITS_KELVIN );

  private int unitConstant;

  ThermometerUnit( final int unitConstant ) {
    this.unitConstant = unitConstant;
  }

  public int getUnitConstant() {
    return unitConstant;
  }
}
