package org.pentaho.plugin.jfreereport.reportcharts.metadata;

import org.pentaho.reporting.engine.classic.core.metadata.DefaultExpressionPropertyCore;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionPropertyMetaData;

public class PieChartFormulaExpressionPropertyCore extends DefaultExpressionPropertyCore
{
  private static final String[] ADDITIONAL_COLUMN_KEYS = new String[]{
      "chart::key", "chart::keys",
      "chart::item", "chart::items",
      "chart::pie-index"
  };

  public PieChartFormulaExpressionPropertyCore()
  {
  }

  public String[] getExtraCalculationFields(final ExpressionPropertyMetaData metaData)
  {
    return ADDITIONAL_COLUMN_KEYS;
  }
}