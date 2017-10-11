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
