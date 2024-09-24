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

public abstract class StackedCategoricalChartExpression extends CategoricalChartExpression {
  private boolean stacked;

  protected StackedCategoricalChartExpression() {
  }

  public boolean isStacked() {
    return stacked;
  }

  public void setStacked( final boolean value ) {
    stacked = value;
  }

}
