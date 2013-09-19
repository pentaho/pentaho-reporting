package org.pentaho.reporting.engine.classic.extensions.drilldown;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;

public interface LinkCustomizer
{
  public String format(final FormulaContext formulaContext,
                       final String configIndicator,
                       final String hostName,
                       final ParameterEntry[] entries) throws EvaluationException;
}
