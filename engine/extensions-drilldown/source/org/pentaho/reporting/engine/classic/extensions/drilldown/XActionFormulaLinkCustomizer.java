package org.pentaho.reporting.engine.classic.extensions.drilldown;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;

/**
 * Pentaho dashboards violate the standard tripplet of "solution", "path", "action" and thus we have to
 * map them manually into something sane.
 *
 * @author Thomas Morgner.
 */
public class XActionFormulaLinkCustomizer extends FormulaLinkCustomizer
{
  public XActionFormulaLinkCustomizer()
  {
  }

  public String format(final FormulaContext formulaContext,
                       final String configIndicator,
                       final String reportPath,
                       final ParameterEntry[] entries) throws EvaluationException
  {
    final ParameterEntry[] entriesX = entries.clone();
    for (int i = 0; i < entriesX.length; i++)
    {
      final ParameterEntry parameterEntry = entriesX[i];
      if ("name".equals(parameterEntry.getParameterName()))
      {
        entriesX[i] = new ParameterEntry("action", parameterEntry.getParameterValue());
      }
    }
    return super.format(formulaContext, configIndicator, reportPath, entriesX);
  }

}