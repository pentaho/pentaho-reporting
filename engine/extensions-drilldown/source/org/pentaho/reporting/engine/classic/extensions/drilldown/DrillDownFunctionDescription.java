package org.pentaho.reporting.engine.classic.extensions.drilldown;

import org.pentaho.reporting.libraries.formula.function.AbstractFunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;
import org.pentaho.reporting.libraries.formula.function.userdefined.UserDefinedFunctionCategory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

public class DrillDownFunctionDescription extends AbstractFunctionDescription
{
  public DrillDownFunctionDescription()
  {
    super("DRILLDOWN", "org.pentaho.reporting.engine.classic.extensions.drilldown.DrillDown-Function");
  }

  public Type getValueType()
  {
    return TextType.TYPE;
  }

  public FunctionCategory getCategory()
  {
    return UserDefinedFunctionCategory.CATEGORY;
  }

  public int getParameterCount()
  {
    return 3;
  }

  /**
   * Returns the parameter type at the given position using the function
   * metadata. The first parameter is at the position 0;
   *
   * @param position The parameter index.
   * @return The parameter type.
   */
  public Type getParameterType(final int position)
  {
    if (position == 2)
    {
      return AnyType.ANY_ARRAY;
    }
    return TextType.TYPE;
  }

  /**
   * Defines, whether the parameter at the given position is mandatory. A
   * mandatory parameter must be filled in, while optional parameters need
   * not to be filled in.
   *
   * @return
   */
  public boolean isParameterMandatory(final int position)
  {
    return true;
  }
}
