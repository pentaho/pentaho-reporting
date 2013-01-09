package org.pentaho.plugin.jfreereport.reportcharts.metadata;

import org.pentaho.reporting.engine.classic.core.metadata.DefaultExpressionPropertyCore;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionPropertyMetaData;

public class CategoryChartFormulaExpressionPropertyCore extends DefaultExpressionPropertyCore
{
  public static final String[] ADDITIONAL_COLUMN_KEYS = new String[]{
      "chart::series-key", "chart::category-key",
      "chart::series-index", "chart::category-index",
      "chart::series-keys", "chart::category-keys",
      "chart::value"
  };

  public CategoryChartFormulaExpressionPropertyCore()
  {
  }

  public String[] getExtraCalculationFields(final ExpressionPropertyMetaData metaData)
  {
    return ADDITIONAL_COLUMN_KEYS;
  }
}
