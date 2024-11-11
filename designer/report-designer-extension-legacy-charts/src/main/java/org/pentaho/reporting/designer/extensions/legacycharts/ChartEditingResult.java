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


package org.pentaho.reporting.designer.extensions.legacycharts;

import org.pentaho.reporting.engine.classic.core.function.Expression;

public class ChartEditingResult {
  private Expression originalChartExpression;
  private Expression originalPrimaryDataSource;
  private Expression originalSecondaryDataSource;

  private Expression chartExpression;
  private Expression primaryDataSource;
  private Expression secondaryDataSource;

  public ChartEditingResult( final Expression originalChartExpression,
                             final Expression originalPrimaryDataSource,
                             final Expression originalSecondaryDataSource,
                             final Expression chartExpression,
                             final Expression primaryDataSource,
                             final Expression secondaryDataSource ) {
    this.originalChartExpression = originalChartExpression;
    this.originalPrimaryDataSource = originalPrimaryDataSource;
    this.originalSecondaryDataSource = originalSecondaryDataSource;
    this.chartExpression = chartExpression;
    this.primaryDataSource = primaryDataSource;
    this.secondaryDataSource = secondaryDataSource;
  }

  public Expression getOriginalChartExpression() {
    return originalChartExpression;
  }

  public Expression getOriginalPrimaryDataSource() {
    return originalPrimaryDataSource;
  }

  public Expression getOriginalSecondaryDataSource() {
    return originalSecondaryDataSource;
  }

  public Expression getChartExpression() {
    return chartExpression;
  }

  public Expression getPrimaryDataSource() {
    return primaryDataSource;
  }

  public Expression getSecondaryDataSource() {
    return secondaryDataSource;
  }
}
