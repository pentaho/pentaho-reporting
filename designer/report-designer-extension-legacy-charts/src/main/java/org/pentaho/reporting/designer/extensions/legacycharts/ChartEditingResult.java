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
