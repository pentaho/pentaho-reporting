package org.pentaho.reporting.library.parameter;

public class ParameterValidationException extends ParameterException
{
  public ParameterValidationException()
  {
  }

  public ParameterValidationException(final String message)
  {
    super(message);
  }

  public ParameterValidationException(final String message, final Throwable cause)
  {
    super(message, cause);
  }

  public ParameterValidationException(final Throwable cause)
  {
    super(cause);
  }
}
