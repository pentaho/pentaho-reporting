package org.pentaho.plugin.jfreereport.reportcharts;

import org.jfree.chart.labels.PieToolTipGenerator;
import org.jfree.data.general.PieDataset;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.function.GenericExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.WrapperExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.util.IntegerCache;

public class FormulaPieTooltipGenerator implements PieToolTipGenerator
{
  private FormulaExpression formulaExpression;
  private GenericExpressionRuntime runtime;
  private static final String[] ADDITIONAL_COLUMN_KEYS = new String[]{
      "chart::key", "chart::keys",
      "chart::item", "chart::items",
      "chart::pie-index"
  };

  public FormulaPieTooltipGenerator(final ExpressionRuntime runtime,
                                    final String formula)
  {
    this.runtime = new GenericExpressionRuntime(runtime.getData(), runtime.getCurrentRow(),
        runtime.getProcessingContext());
    this.formulaExpression = new FormulaExpression();
    this.formulaExpression.setFormula(formula);
  }

  /**
   * Generates a tool tip text item for the specified item in the dataset.
   * This method can return <code>null</code> to indicate that no tool tip
   * should be displayed for an item.
   *
   * @param dataset the dataset (<code>null</code> not permitted).
   * @param key     the section key (<code>null</code> not permitted).
   * @return The tool tip text (possibly <code>null</code>).
   */
  public String generateToolTip(PieDataset dataset, Comparable key)
  {
    try
    {
      final Object[] keys = dataset.getKeys().toArray();
      final Object[] items = new Object[keys.length];
      for (int i = 0; i < keys.length; i++)
      {
        items[i] = dataset.getValue(i);
      }
      final Object[] values = new Object[]{
          key, keys,
          dataset.getValue(key), items,
          IntegerCache.getInteger(dataset.getIndex(key))
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
