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
import org.jfree.chart.plot.RingPlot;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.PieDataset;

public class RingChartExpression extends PieChartExpression {

  private static final long serialVersionUID = 8157232155813173422L;

  private double sectionDepth;

  public RingChartExpression() {
    sectionDepth = 0.5;
  }

  protected JFreeChart computeChart( final Dataset dataset ) {
    PieDataset pieDataset = null;
    if ( dataset instanceof PieDataset ) {
      pieDataset = (PieDataset) dataset;
    }

    return ChartFactory.createRingChart( computeTitle(), pieDataset, isShowLegend(), false, false );
  }

  protected void configureChart( final JFreeChart chart ) {
    super.configureChart( chart );

    final RingPlot ringPlot = (RingPlot) chart.getPlot();
    ringPlot.setSectionDepth( sectionDepth );
  }

  public double getSectionDepth() {
    return sectionDepth;
  }

  public void setSectionDepth( final double sectionDepth ) {
    this.sectionDepth = sectionDepth;
  }

}
