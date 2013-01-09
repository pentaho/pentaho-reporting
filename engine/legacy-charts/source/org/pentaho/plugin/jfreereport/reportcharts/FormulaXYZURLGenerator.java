package org.pentaho.plugin.jfreereport.reportcharts;

import org.jfree.chart.urls.XYZURLGenerator;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.function.WrapperExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.util.IntegerCache;

public class FormulaXYZURLGenerator implements XYZURLGenerator
{
  private FormulaExpression formulaExpression;
  private ExpressionRuntime runtime;
  private static final String[] ADDITIONAL_COLUMN_KEYS = new String[]{
      "chart::x-value", "chart::y-value", "chart::z-value",
      "chart::series-index", "chart::series-key", "chart::series-count",
      "chart::item-index", "chart::item-count"
  };

  public FormulaXYZURLGenerator(final ExpressionRuntime runtime,
                                final String formula)
  {
    this.runtime = runtime;
    this.formulaExpression = new FormulaExpression();
    this.formulaExpression.setFormula(formula);
  }

  /**
   * Generates a URL for a particular item within a series.
   *
   * @param dataset the dataset.
   * @param series  the series number (zero-based index).
   * @param item    the item number (zero-based index).
   * @return The generated URL.
   */
  public String generateURL(final XYDataset dataset, final int series, final int item)
  {
    if (dataset instanceof XYZDataset)
    {
      return generateURL((XYZDataset) dataset, series, item);
    }
    try
    {
      final Object[] values = new Object[]{
          dataset.getX(series, item), dataset.getY(series, item), null,
          IntegerCache.getInteger(series), dataset.getSeriesKey(series),
          IntegerCache.getInteger(dataset.getSeriesCount()),
          IntegerCache.getInteger(item), IntegerCache.getInteger(dataset.getItemCount(series))
      };
      formulaExpression.setRuntime(new WrapperExpressionRuntime
          (new StaticDataRow(ADDITIONAL_COLUMN_KEYS, values), runtime));
      final Object o = formulaExpression.getValue();
      if (o == null)
      {
        return null;
      }
      return String.valueOf(o);
    }
    finally
    {
      formulaExpression.setRuntime(null);
    }
  }

  /**
   * Generates a URL for a particular item within a series. As a guideline,
   * the URL should be valid within the context of an XHTML 1.0 document.
   *
   * @param dataset the dataset (<code>null</code> not permitted).
   * @param series  the series index (zero-based).
   * @param item    the item index (zero-based).
   * @return A string containing the generated URL.
   */
  public String generateURL(final XYZDataset dataset, final int series, final int item)
  {
    try
    {
      final Object[] values = new Object[]{
          dataset.getX(series, item), dataset.getY(series, item), dataset.getZ(series, item),
          IntegerCache.getInteger(series), dataset.getSeriesKey(series),
          IntegerCache.getInteger(dataset.getSeriesCount()),
          IntegerCache.getInteger(item), IntegerCache.getInteger(dataset.getItemCount(series))
      };
      formulaExpression.setRuntime(new WrapperExpressionRuntime
          (new StaticDataRow(ADDITIONAL_COLUMN_KEYS, values), runtime));
      final Object o = formulaExpression.getValue();
      if (o == null)
      {
        return null;
      }
      return String.valueOf(o);
    }
    finally
    {
      formulaExpression.setRuntime(null);
    }
  }
}
