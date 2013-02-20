package org.pentaho.reporting.libraries.parameter.validation;

import org.pentaho.reporting.libraries.parameter.ValidationMessage;

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
