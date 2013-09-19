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

public class ArrayLeftFunction implements Function
{
  public ArrayLeftFunction()
  {
  }

  public String getCanonicalName()
  {
    return "ARRAYLEFT";
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

    final Object textValue = parameters.getValue(0);

    final Sequence text = new RecursiveSequence(textValue, context);
    final int length;
    if (parameterCount == 2)
    {
      final Type lengthType = parameters.getType(1);
      final Object lengthValue = parameters.getValue(1);
      final Number lengthConv = typeRegistry.convertToNumber(lengthType, lengthValue);
      if (lengthConv.doubleValue() < 0)
      {
        throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE);
      }

      length = lengthConv.intValue();
    }
    else
    {
      length = 1;
    }

    final ArrayList retval = new ArrayList(length);
    if (length > 0)
    {
      while (text.hasNext())
      {
        final Object o = text.next();
        retval.add(o);
        if (retval.size() == length)
        {
          break;
        }
      }
    }
    // Note that MID(T;1;Length) produces the same results as LEFT(T;Length).
    return new TypeValuePair(AnyType.ANY_ARRAY, retval.toArray());
  }
}
