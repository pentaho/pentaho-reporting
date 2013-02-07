package org.pentaho.reporting.library.parameter.validation;

import org.pentaho.reporting.library.parameter.ParameterData;
import org.pentaho.reporting.library.parameter.ParameterValidationResult;
import org.pentaho.reporting.library.parameter.ValidationMessage;

public class DefaultParameterValidationResult implements ParameterValidationResult
{
  public DefaultParameterValidationResult()
  {
  }

  public void updateParameterValues(final ParameterData trustedValues)
  {

  }

  public ParameterData getParameterValues()
  {
    return null;
  }

  public void addGlobalError(final ValidationMessage message)
  {

  }

  public void addError(final String parameterName, final ValidationMessage message)
  {

  }

  public ValidationMessage[] getGlobalErrors()
  {
    return new ValidationMessage[0];
  }

  public ValidationMessage[] getErrors(final String parameter)
  {
    return new ValidationMessage[0];
  }

  public String[] getErrorParameterNames()
  {
    return new String[0];
  }

  public boolean isParameterSetValid()
  {
    return false;
  }
}
