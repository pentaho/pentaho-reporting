package org.pentaho.reporting.engine.classic.extensions.drilldown;

import org.pentaho.reporting.libraries.formula.function.AbstractFunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;
import org.pentaho.reporting.libraries.formula.function.userdefined.UserDefinedFunctionCategory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

public class UrlParameterSeparatorFunctionDescription extends AbstractFunctionDescription
{
  public UrlParameterSeparatorFunctionDescription()
  {
    super("URLPARAMETERSEPARATOR",
        "org.pentaho.reporting.engine.classic.extensions.drilldown.UrlParameterSeparator-Function");
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
    return 1;
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
    return false;
  }


}
