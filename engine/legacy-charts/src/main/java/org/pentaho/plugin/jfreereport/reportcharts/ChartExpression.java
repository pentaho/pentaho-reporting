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

import org.pentaho.reporting.engine.classic.core.function.Expression;

public interface ChartExpression extends Expression {
  public String getDataSource();

  public void setDataSource( String dataSource );

  public String[] getHyperlinkFormulas();
}
