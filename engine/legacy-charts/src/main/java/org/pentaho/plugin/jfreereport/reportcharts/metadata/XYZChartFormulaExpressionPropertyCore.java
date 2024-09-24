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

package org.pentaho.plugin.jfreereport.reportcharts.metadata;

import org.pentaho.reporting.engine.classic.core.metadata.DefaultExpressionPropertyCore;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionPropertyMetaData;

public class XYZChartFormulaExpressionPropertyCore extends DefaultExpressionPropertyCore {
  private static final String[] ADDITIONAL_COLUMN_KEYS = new String[] {
    "chart::x-value", "chart::y-value", "chart::z-value",
    "chart::series-index", "chart::series-key", "chart::series-count",
    "chart::item-index", "chart::item-count"
  };

  public XYZChartFormulaExpressionPropertyCore() {
  }

  public String[] getExtraCalculationFields( final ExpressionPropertyMetaData metaData ) {
    return ADDITIONAL_COLUMN_KEYS.clone();
  }
}
