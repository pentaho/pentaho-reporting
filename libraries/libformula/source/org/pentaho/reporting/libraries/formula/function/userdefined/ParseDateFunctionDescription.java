package org.pentaho.reporting.libraries.formula.function.userdefined;

import org.pentaho.reporting.libraries.formula.function.AbstractFunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.DateTimeType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

public class ParseDateFunctionDescription extends AbstractFunctionDescription
{
  private static final long serialVersionUID = 5710880620780379157L;

  public ParseDateFunctionDescription()
  {
    super("PARSEDATE", "org.pentaho.reporting.libraries.formula.function.userdefined.ParseDate-Function");
  }

  public int getParameterCount()
  {
    return 4;
  }

  public Type getParameterType(final int position)
  {
    return TextType.TYPE;
  }

  public Type getValueType()
  {
    return DateTimeType.DATETIME_TYPE;
  }

  /**
   * Defines, whether the parameter at the given position is mandatory. A
   * mandatory parameter must be filled in, while optional parameters need not
   * to be filled in.
   *
   * @return
   */
  public boolean isParameterMandatory(final int position)
  {
    return position < 2;
  }

  public FunctionCategory getCategory()
  {
    return UserDefinedFunctionCategory.CATEGORY;
  }

}
