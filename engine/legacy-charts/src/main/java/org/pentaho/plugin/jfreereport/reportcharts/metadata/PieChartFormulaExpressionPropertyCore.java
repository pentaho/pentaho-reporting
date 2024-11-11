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


package org.pentaho.plugin.jfreereport.reportcharts.metadata;

import org.pentaho.reporting.engine.classic.core.metadata.DefaultExpressionPropertyCore;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionPropertyMetaData;

public class PieChartFormulaExpressionPropertyCore extends DefaultExpressionPropertyCore {
  private static final String[] ADDITIONAL_COLUMN_KEYS = new String[] {
    "chart::key", "chart::keys",
    "chart::item", "chart::items",
    "chart::pie-index"
  };

  public PieChartFormulaExpressionPropertyCore() {
  }

  public String[] getExtraCalculationFields( final ExpressionPropertyMetaData metaData ) {
    return ADDITIONAL_COLUMN_KEYS.clone();
  }
}
