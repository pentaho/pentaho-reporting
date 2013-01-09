package org.pentaho.reporting.engine.classic.core.function.formula;

import org.pentaho.reporting.libraries.formula.function.AbstractFunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;
import org.pentaho.reporting.libraries.formula.function.information.InformationFunctionCategory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.LogicalType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

public class DashboardModeFunctionDescription extends AbstractFunctionDescription
{
  /**
   * Default Constructor.
   */
  public DashboardModeFunctionDescription()
  {
    super("DASHBOARDMODE", "org.pentaho.reporting.engine.classic.core.function.formula.DashboardMode-Function");
  }

  /**
   * Returns the expected value type. This function returns a LogicalType.
   *
   * @return LogicalType.TYPE
   */
  public Type getValueType()
  {
    return LogicalType.TYPE;
  }

  /**
   * Returns the number of parameters expected by the function.
   *
   * @return 1.
   */
  public int getParameterCount()
  {
    return 0;
  }

  /**
   * Returns the parameter type of the function parameters.
   *
   * @param position the parameter index.
   * @return always TextType.TYPE.
   */
  public Type getParameterType(final int position)
  {
    return TextType.TYPE;
  }

  /**
   * Defines, whether the parameter at the given position is mandatory. A mandatory parameter must be filled in, while
   * optional parameters need not to be filled in.
   *
   * @param position the position of the parameter.
   * @return true, as all parameters are mandatory.
   */
  public boolean isParameterMandatory(final int position)
  {
    return false;
  }

  /**
   * Returns the function category. The function category groups functions by their expected use.
   *
   * @return InformationFunctionCategory.CATEGORY.
   */
  public FunctionCategory getCategory()
  {
    return InformationFunctionCategory.CATEGORY;
  }
}
