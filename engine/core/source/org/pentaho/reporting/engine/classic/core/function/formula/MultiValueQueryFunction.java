package org.pentaho.reporting.engine.classic.core.function.formula;

import java.util.ArrayList;
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

public class MultiValueQueryFunction implements Function
{
  private static final Log logger = LogFactory.getLog(MultiValueQueryFunction.class);

  public MultiValueQueryFunction()
  {
  }

  public String getCanonicalName()
  {
    return "MULTIVALUEQUERY";
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

    final int queryLimit;
    if (parameterCount > 3)
    {
      final Type encodingType = parameters.getType(3);
      final Object encodingValue = parameters.getValue(3);
      final Number number = context.getTypeRegistry().convertToNumber(encodingType, encodingValue);
      if (number == null)
      {
        throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE);
      }
      queryLimit = number.intValue();
    }
    else
    {
      queryLimit = 0;
    }

    final Object result = performQuery(rfc, query, resultColumn, queryTimeOut, queryLimit);
    return new TypeValuePair(AnyType.ANY_ARRAY, result);
  }

  private Object performQuery(final ReportFormulaContext context,
                              final String query,
                              final String columnName,
                              final int queryTimeout,
                              final int queryLimit) throws EvaluationException
  {

    try
    {
      final DataFactory dataFactory = context.getRuntime().getDataFactory();
      final TableModel tableModel = dataFactory.queryData(query,
          new QueryDataRowWrapper(context.getDataRow(), queryLimit, queryTimeout));
      if (tableModel == null)
      {
        return null;
      }
      final int columnCount = tableModel.getColumnCount();
      if (tableModel.getRowCount() == 0 || columnCount == 0)
      {
        return null;
      }

      for (int column = 0; column < columnCount; column++)
      {
        if (columnName == null || columnName.equals(tableModel.getColumnName(column)))
        {
          final ArrayList<Object> values = new ArrayList<Object>();
          final int rowCount = tableModel.getRowCount();
          for (int row = 0; row < rowCount; row++)
          {
            values.add(tableModel.getValueAt(row, column));
          }
          return values.toArray();
        }
      }
      throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_UNEXPECTED_VALUE);
    }
    catch (EvaluationException e)
    {
      throw e;
    }
    catch (Exception e)
    {
      logger.warn("SingleValueQueryFunction: Failed to perform query", e);
      throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_UNEXPECTED_VALUE);
    }
  }
}