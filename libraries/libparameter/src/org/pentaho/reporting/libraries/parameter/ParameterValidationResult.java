package org.pentaho.reporting.libraries.parameter;

public interface ParameterValidationResult
{
  public ParameterData getParameterValues();

  public void addGlobalError(final ValidationMessage message);

  public void addError(final String parameterName,
                       final ValidationMessage message);

  public ValidationMessage[] getGlobalErrors();

  public ValidationMessage[] getErrors(final String parameter);

  public String[] getErrorParameterNames();

  public boolean isParameterSetValid();

  void updateParameterValues(ParameterData trustedValues);
}
