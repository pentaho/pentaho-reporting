package org.pentaho.reporting.libraries.formula.function.userdefined;

import org.pentaho.reporting.libraries.formula.function.AbstractFunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;

public class ArrayLeftFunctionDescription extends AbstractFunctionDescription
{
  public ArrayLeftFunctionDescription()
  {
    super("ARRAYLEFT", "org.pentaho.reporting.libraries.formula.function.userdefined.ArrayLeft-Function");
  }

  public Type getValueType()
  {
    return AnyType.ANY_ARRAY;
  }

  public FunctionCategory getCategory()
  {
    return UserDefinedFunctionCategory.CATEGORY;
  }

  public int getParameterCount()
  {
    return 2;
  }

  public Type getParameterType(final int position)
  {
    if (position == 0)
    {
      return AnyType.ANY_ARRAY;
    }

    return NumberType.GENERIC_NUMBER;
  }

  public boolean isParameterMandatory(final int position)
  {
    if (position == 2)
    {
      return false;
    }
    return true;
  }

}
