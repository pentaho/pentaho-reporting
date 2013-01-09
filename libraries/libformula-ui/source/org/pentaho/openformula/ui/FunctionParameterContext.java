package org.pentaho.openformula.ui;

import java.util.Arrays;

import org.pentaho.openformula.ui.model2.FunctionInformation;
import org.pentaho.reporting.libraries.formula.function.FunctionDescription;

public class FunctionParameterContext
{
  private FunctionDescription function;
  private String[] parameterValues;
  private FunctionInformation functionInformation;

  public FunctionParameterContext()
  {
  }

  public FunctionParameterContext(final FunctionDescription function,
                                  final String[] parameterValues,
                                  final FunctionInformation fn)
  {
    this.function = function;
    this.parameterValues = parameterValues;
    this.functionInformation = fn;
  }

  public FunctionInformation getFunctionInformation()
  {
    return functionInformation;
  }

  public FunctionDescription getFunction()
  {
    return function;
  }

  public int getFunctionParameterStartPosition()
  {
    if (functionInformation == null)
    {
      return -1;
    }
    return functionInformation.getFunctionParameterStart();
  }

  public String[] getParameterValues()
  {
    return parameterValues;
  }


  public static boolean isSameFunctionDescription(final FunctionDescription d1,
                                                   final FunctionDescription d2)
  {
    if (d1 == null || d2 == null)
    {
      return false;
    }
    if (d1.getClass().equals(d2.getClass()))
    {
      return true;
    }
    return false;
  }

  public String toString()
  {
    final StringBuffer sb = new StringBuffer();
    sb.append("FunctionParameterContext");
    sb.append("{function=").append(function);
    sb.append(", parameterValues=").append(parameterValues == null ? "null" : Arrays.asList(parameterValues).toString());
    sb.append(", functionInformation=").append(functionInformation);
    sb.append('}');
    return sb.toString();
  }
}
