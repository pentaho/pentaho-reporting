package org.pentaho.reporting.engine.classic.core.function.formula;

import org.pentaho.reporting.engine.classic.core.function.ReportFormulaContext;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlTableModule;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.coretypes.LogicalType;

public class DashboardModeFunction implements Function
{
  public DashboardModeFunction()
  {
  }

  public String getCanonicalName()
  {
    return "DASHBOARDMODE";
  }

  public TypeValuePair evaluate(final FormulaContext context,
                                final ParameterCallback parameters) throws EvaluationException
  {
    final ReportFormulaContext rfc = (ReportFormulaContext) context;
    if (isDashboardMode(rfc))
    {
      return new TypeValuePair(LogicalType.TYPE, Boolean.TRUE);
    }
    return new TypeValuePair(LogicalType.TYPE, Boolean.FALSE);
  }

  public static boolean isDashboardMode(final ReportFormulaContext rfc)
  {
    final boolean value = "true".equals(rfc.getConfiguration().getConfigProperty(HtmlTableModule.BODY_FRAGMENT));

    if (value)
    {
      final String exportType = rfc.getExportType();
      if (exportType.startsWith("table/html") &&
          HtmlTableModule.ZIP_HTML_EXPORT_TYPE.equals(exportType) == false)
      {
        return true;
      }
    }
    return false;
  }
}
