package org.pentaho.reporting.libraries.formula.function.userdefined;

import org.pentaho.reporting.libraries.formula.function.AbstractFunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;
import org.pentaho.reporting.libraries.formula.function.text.TextFunctionCategory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

public class ArrayRightFunctionDescription extends AbstractFunctionDescription
{
  private static final long serialVersionUID = -1227750116961019328L;

  public ArrayRightFunctionDescription()
  {
    super("ARRAYRIGHT", "org.pentaho.reporting.libraries.formula.function.userdefined.ArrayRight-Function");
  }

  public FunctionCategory getCategory()
  {
    return TextFunctionCategory.CATEGORY;
  }

  public int getParameterCount()
  {
    return 2;
  }

  public Type getParameterType(final int position)
  {
    if(position == 0)
    {
      return AnyType.ANY_ARRAY;
    }

    return NumberType.GENERIC_NUMBER;
  }

  public Type getValueType()
  {
    return TextType.TYPE;
  }

  public boolean isParameterMandatory(final int position)
  {
    if(position == 2)
    {
      return false;
    }
    return true;
  }

}

