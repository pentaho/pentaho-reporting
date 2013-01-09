package org.pentaho.reporting.engine.classic.core.function.formula;

import javax.swing.table.TableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.function.ReportFormulaContext;
import org.pentaho.reporting.engine.classic.core.states.QueryDataRowWrapper;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;

public class SingleValueQueryFunction implements Function
{
  private static final Log logger = LogFactory.getLog(SingleValueQueryFunction.class);

  public SingleValueQueryFunction()
  {
  }

  public String getCanonicalName()
  {
    return "SINGLEVALUEQUERY";
  }

  public TypeValuePair evaluate(final FormulaContext context,
                                final ParameterCallback parameters) throws EvaluationException
  {
    if (context instanceof ReportFormulaContext == false)
    {
      throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_INVALID_FUNCTION_VALUE);
    }

    final ReportFormulaContext rfc = (ReportFormulaContext) context;

    final int parameterCount = parameters.getParameterCount();
    if (parameterCount < 1)
    {
      throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE);
    }

    final Type textType = parameters.getType(0);
    final Object textValue = parameters.getValue(0);
    final String query =
        context.getTypeRegistry().convertToText(textType, textValue);

    if (query == null)
    {
      throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE);
    }

    final String resultColumn;
    if (parameterCount > 1)
    {
      final Type encodingType = parameters.getType(1);
      final Object encodingValue = parameters.getValue(1);
      resultColumn = context.getTypeRegistry().convertToText(encodingType, encodingValue);
      if (resultColumn == null)
      {
        throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE);
      }
    }
    else
    {
      resultColumn = null;
    }


    final int queryTimeOut;
    if (parameterCount > 2)
    {
      final Type encodingType = parameters.getType(2);
      final Object encodingValue = parameters.getValue(2);
      final Number number = context.getTypeRegistry().convertToNumber(encodingType, encodingValue);
      if (number == null)
      {
        throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE);
      }
      queryTimeOut = number.intValue();
    }
    else
    {
      queryTimeOut = 0;
    }

    final Object result = performQuery(rfc, query, resultColumn, queryTimeOut);
    return new TypeValuePair(AnyType.TYPE, result);
  }

  private Object performQuery(final ReportFormulaContext context,
                              final String query,
                              final String column,
                              final int queryTimeout)
  {

    try
    {
      final DataFactory dataFactory = context.getRuntime().getDataFactory();
      final TableModel tableModel = dataFactory.queryData(query,
          new QueryDataRowWrapper(context.getDataRow(), 1, queryTimeout));
      if (tableModel == null)
      {
        return null;
      }

      final int columnCount = tableModel.getColumnCount();
      if (tableModel.getRowCount() == 0 || columnCount == 0)
      {
        return null;
      }
      if (column == null)
      {
        return tableModel.getValueAt(0, 0);
      }
      for (int i = 0; i < columnCount; i++)
      {
        if (column.equals(tableModel.getColumnName(i)))
        {
          return tableModel.getValueAt(0, i);
        }
      }
    }
    catch (Exception e)
    {
      SingleValueQueryFunction.logger.warn("SingleValueQueryFunction: Failed to perform query", e);
    }
    return null;
  }
}
