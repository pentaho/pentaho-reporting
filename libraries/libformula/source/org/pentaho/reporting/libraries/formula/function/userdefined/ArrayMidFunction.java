package org.pentaho.reporting.libraries.formula.function.userdefined;

import java.util.ArrayList;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Sequence;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;
import org.pentaho.reporting.libraries.formula.typing.sequence.RecursiveSequence;

public class ArrayMidFunction implements Function
{
  public ArrayMidFunction()
  {
  }

  public String getCanonicalName()
  {
    return "ARRAYMID";
  }

  public TypeValuePair evaluate(final FormulaContext context,
                                final ParameterCallback parameters) throws EvaluationException
  {
    final int parameterCount = parameters.getParameterCount();
    if (parameterCount != 3)
    {
      throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE);
    }
    final TypeRegistry typeRegistry = context.getTypeRegistry();

    final Object textValue = parameters.getValue(0);
    final Type startType = parameters.getType(1);
    final Object startValue = parameters.getValue(1);
    final Type lengthType = parameters.getType(2);
    final Object lengthValue = parameters.getValue(2);

    final Sequence text = new RecursiveSequence(textValue, context);
    final Number start = typeRegistry.convertToNumber(startType, startValue);
    final Number length = typeRegistry.convertToNumber(lengthType, lengthValue);

    if (length.doubleValue() < 0 || start.doubleValue() < 1)
    {
      throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE);
    }

    return new TypeValuePair(AnyType.ANY_ARRAY, process(text, start.intValue(), length.intValue()));
  }

  public static Object[] process(final Sequence text, final int start, final int length) throws EvaluationException
  {
    final ArrayList retval = new ArrayList(length);
    int counter = 0;
    final int end = start + length;
    while (text.hasNext())
    {
      final Object o = text.next();
      counter += 1;
      if (counter == end)
      {
        break;
      }
      if (counter >= start)
      {
        retval.add(o);
      }
    }
    return retval.toArray();
  }
}
