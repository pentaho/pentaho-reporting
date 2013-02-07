package org.pentaho.reporting.library.parameter;

public class ParameterException extends Exception
{
  public ParameterException()
  {
  }

  public ParameterException(final String message)
  {
    super(message);
  }

  public ParameterException(final String message, final Throwable cause)
  {
    super(message, cause);
  }

  public ParameterException(final Throwable cause)
  {
    super(cause);
  }
}
