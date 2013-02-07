package org.pentaho.reporting.library.parameter.validation;

import org.pentaho.reporting.library.parameter.ValidationMessage;

public class DefaultValidationMessage implements ValidationMessage
{
  private String message;

  public DefaultValidationMessage(final String message)
  {
    this.message = message;
  }

  public String getMessage()
  {
    return message;
  }
}
