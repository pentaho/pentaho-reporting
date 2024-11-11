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
