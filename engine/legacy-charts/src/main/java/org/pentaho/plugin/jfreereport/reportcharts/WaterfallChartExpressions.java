/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.plugin.jfreereport.reportcharts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.pentaho.plugin.jfreereport.reportcharts.backport.FormattedCategoryAxis;

public class WaterfallChartExpressions extends CategoricalChartExpression {
  private static final long serialVersionUID = 8342198616002055989L;

  public WaterfallChartExpressions() {
  }

  protected JFreeChart computeCategoryChart( final CategoryDataset dataset ) {
    final JFreeChart chart = ChartFactory.createWaterfallChart( computeTitle(), getCategoryAxisLabel(),
      getValueAxisLabel(), dataset,
      computePlotOrientation(), isShowLegend(), false, false );
    chart.getCategoryPlot().setDomainAxis( new FormattedCategoryAxis( getCategoryAxisLabel(),
      getCategoricalAxisMessageFormat(), getRuntime().getResourceBundleFactory().getLocale() ) );
    return chart;
  }

}
