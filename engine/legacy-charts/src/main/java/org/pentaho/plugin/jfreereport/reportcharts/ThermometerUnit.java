/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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
