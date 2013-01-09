package org.pentaho.reporting.libraries.formula.function.userdefined;

import java.util.LinkedList;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;
import org.pentaho.reporting.libraries.formula.typing.sequence.RecursiveSequence;

public class ArrayRightFunction implements Function
{
  public ArrayRightFunction()
  {
  }

  public String getCanonicalName()
  {
    return "ARRAYRIGHT";
  }

  public TypeValuePair evaluate(final FormulaContext context,
                                final ParameterCallback parameters) throws EvaluationException
  {
    final int parameterCount = parameters.getParameterCount();
    if (parameterCount < 1 || parameterCount > 2)
    {
      throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE);
    }
    final TypeRegistry typeRegistry = context.getTypeRegistry();

    final int length;
    if (parameterCount == 2)
    {
      final Number lengthVal = typeRegistry.convertToNumber(parameters.getType(1), parameters.getValue(1));
      if (lengthVal.doubleValue() < 0)
      {
        throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE);
      }
      length = lengthVal.intValue();
    }
    else
    {
      length = 1;
    }

    if (length < 0)
    {
      throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE);
    }

    final RecursiveSequence text = new RecursiveSequence(parameters.getValue(0), context);
    final LinkedList list = new LinkedList();
    while (text.hasNext())
    {
      final Object element = text.next();
      list.add(element);
      if (list.size() > length)
      {
        list.remove(0);
      }
    }

    final Object[] retval = list.toArray(new Object[list.size()]);
    return new TypeValuePair(AnyType.ANY_ARRAY, retval);
  }
}
