package org.pentaho.reporting.libraries.formula.function.userdefined;

import java.util.ArrayList;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Sequence;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;
import org.pentaho.reporting.libraries.formula.typing.sequence.RecursiveSequence;

public class ArrayConcatenateFunction implements Function
{
  public ArrayConcatenateFunction()
  {
  }

  public String getCanonicalName()
  {
    return "ARRAYCONCATENATE";
  }

  public TypeValuePair evaluate(final FormulaContext context,
                                final ParameterCallback parameters) throws EvaluationException
  {
    final ArrayList computedResult = new ArrayList(512);
    final int parameterCount = parameters.getParameterCount();

    if (parameterCount == 0)
    {
      throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE);
    }

    for (int paramIdx = 0; paramIdx < parameterCount; paramIdx++)
    {
      final Object value = parameters.getValue(paramIdx);
      final Sequence sequence = new RecursiveSequence(value, context);

      while (sequence.hasNext())
      {
        final Object o = sequence.next();
        computedResult.add(o);
      }
    }

    return new TypeValuePair(AnyType.ANY_ARRAY, computedResult.toArray());
  }
}
