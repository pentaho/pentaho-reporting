package org.pentaho.plugin.jfreereport.reportcharts;

import org.jfree.chart.labels.XYZToolTipGenerator;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.function.WrapperExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.util.IntegerCache;

public class FormulaXYZTooltipGenerator implements XYZToolTipGenerator
{
  private FormulaExpression formulaExpression;
  private ExpressionRuntime runtime;
  private static final String[] ADDITIONAL_COLUMN_KEYS = new String[]{
      "chart::x-value", "chart::y-value", "chart::z-value",
      "chart::series-index", "chart::series-key", "chart::series-count",
      "chart::item-index", "chart::item-count"
  };

  public FormulaXYZTooltipGenerator(final ExpressionRuntime runtime,
                                    final String formula)
  {
    this.runtime = runtime;
    this.formulaExpression = new FormulaExpression();
    this.formulaExpression.setFormula(formula);
  }

  /**
   * Generates the tooltip text for the specified item.
   *
   * @param dataset the dataset (<code>null</code> not permitted).
   * @param series  the series index (zero-based).
   * @param item    the item index (zero-based).
   * @return The tooltip text (possibly <code>null</code>).
   */
  public String generateToolTip(final XYDataset dataset, final int series, final int item)
  {
    if (dataset instanceof XYZDataset)
    {
      return generateToolTip((XYZDataset) dataset, series, item);
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
   * Generates a tool tip text item for a particular item within a series.
   *
   * @param dataset the dataset (<code>null</code> not permitted).
   * @param series  the series index (zero-based).
   * @param item    the item index (zero-based).
   * @return The tooltip text (possibly <code>null</code>).
   */
  public String generateToolTip(final XYZDataset dataset, final int series, final int item)
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
