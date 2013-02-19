package org.pentaho.reporting.library.parameter.defaults;

import org.pentaho.reporting.library.parameter.ListParameter;
import org.pentaho.reporting.library.parameter.ParameterContext;
import org.pentaho.reporting.library.parameter.ParameterDataTable;
import org.pentaho.reporting.library.parameter.ParameterException;

public class DefaultListParameter extends AbstractParameter implements ListParameter
{
  private boolean strictValueCheck;
  private boolean allowMultiSelection;
  private boolean allowResetOnInvalidValue;
  private String keyColumn;
  private String textColumn;

  public DefaultListParameter(final String name, final Class valueType, final String keyColumn)
  {
    super(name, valueType);
    if (keyColumn == null)
    {
      throw new NullPointerException();
    }
    this.keyColumn = keyColumn;
    this.textColumn = keyColumn;
  }

  public boolean isStrictValueCheck()
  {
    return strictValueCheck;
  }

  public void setStrictValueCheck(final boolean strictValueCheck)
  {
    this.strictValueCheck = strictValueCheck;
  }

  public boolean isAllowMultiSelection()
  {
    return allowMultiSelection;
  }

  public void setAllowMultiSelection(final boolean allowMultiSelection)
  {
    this.allowMultiSelection = allowMultiSelection;
  }

  public boolean isAllowResetOnInvalidValue()
  {
    return allowResetOnInvalidValue;
  }

  public void setAllowResetOnInvalidValue(final boolean allowResetOnInvalidValue)
  {
    this.allowResetOnInvalidValue = allowResetOnInvalidValue;
  }

  public ParameterDataTable getValues(final ParameterContext context) throws ParameterException
  {
    return null;
  }

  public String getKeyColumn()
  {
    return keyColumn;
  }

  public void setKeyColumn(final String keyColumn)
  {
    if (keyColumn == null)
    {
      throw new NullPointerException();
    }
    this.keyColumn = keyColumn;
  }

  public String getTextColumn()
  {
    return textColumn;
  }

  public void setTextColumn(final String textColumn)
  {
    if (textColumn == null)
    {
      throw new NullPointerException();
    }
    this.textColumn = textColumn;
  }
}
