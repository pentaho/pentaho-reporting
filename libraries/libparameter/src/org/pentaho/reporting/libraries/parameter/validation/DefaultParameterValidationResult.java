package org.pentaho.reporting.libraries.parameter.validation;

import java.util.ArrayList;
import java.util.Set;

import org.pentaho.reporting.libraries.base.util.HashNMap;
import org.pentaho.reporting.libraries.parameter.ParameterData;
import org.pentaho.reporting.libraries.parameter.ParameterValidationResult;
import org.pentaho.reporting.libraries.parameter.ValidationMessage;

public class DefaultParameterValidationResult implements ParameterValidationResult
{
  private ArrayList<ValidationMessage> globalErrors;
  private HashNMap<String, ValidationMessage> parameterErrors;
  private ParameterData parameterData;

  public DefaultParameterValidationResult()
  {
    globalErrors = new ArrayList<ValidationMessage>();
    parameterErrors = new HashNMap<String, ValidationMessage>();
  }

  public void updateParameterValues(final ParameterData trustedValues)
  {
    parameterData = trustedValues;
  }

  public ParameterData getParameterValues()
  {
    return parameterData;
  }

  public void addGlobalError(final ValidationMessage message)
  {
    globalErrors.add(message);
  }

  public void addError(final String parameterName, final ValidationMessage message)
  {
    parameterErrors.add(parameterName, message);
  }

  public ValidationMessage[] getGlobalErrors()
  {
    return globalErrors.toArray(new ValidationMessage[globalErrors.size()]);
  }

  public ValidationMessage[] getErrors(final String parameter)
  {
    return parameterErrors.toArray(parameter, new ValidationMessage[0]);
  }

  public String[] getErrorParameterNames()
  {
    final Set<String> strings = parameterErrors.keySet();
    return strings.toArray(new String[strings.size()]);
  }

  public boolean isParameterSetValid()
  {
    return parameterErrors.isEmpty() && globalErrors.isEmpty();
  }
}
