package org.pentaho.reporting.libraries.formula.function.userdefined;

import org.pentaho.reporting.libraries.formula.function.AbstractFunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;

public class ArrayConcatenateFunctionDescription extends AbstractFunctionDescription
{
  private static final long serialVersionUID = 6842849993225519941L;

  public ArrayConcatenateFunctionDescription()
  {
    super("ARRAYCONCATENATE", "org.pentaho.reporting.libraries.formula.function.userdefined.ArrayConcatenate-Function");
  }

  public FunctionCategory getCategory()
  {
    return UserDefinedFunctionCategory.CATEGORY;
  }

  public int getParameterCount()
  {
    return 1;
  }

  public Type getParameterType(final int position)
  {
    return AnyType.ANY_SEQUENCE;
  }

  public boolean isInfiniteParameterCount()
  {
    return true;
  }

  public Type getValueType()
  {
    return AnyType.ANY_ARRAY;
  }

  public boolean isParameterMandatory(final int position)
  {
    return false;
  }
}
