package org.pentaho.reporting.library.parameter.values;

import org.pentaho.reporting.library.parameter.LibParameterBoot;

public class ValueConversionException extends RuntimeException
{
  private static ThreadLocal localInstance = new ThreadLocal();
  private static volatile Boolean useCause;
  private String message;

  public ValueConversionException()
  {
  }

  public ValueConversionException(final String message)
  {
    super(message);
    this.message = message;
  }

  public ValueConversionException(final String message, final Throwable cause)
  {
    super(message, cause);
    this.message = message;
  }

  public ValueConversionException(final Throwable cause)
  {
    super(cause);
  }

  public String getMessage()
  {
    return message;
  }

  public static ValueConversionException getInstance(final String message, final Throwable cause)
  {
    if (useCause == null)
    {
      useCause = ("true".equals(LibParameterBoot.getInstance().getGlobalConfig().getConfigProperty
          ("org.pentaho.reporting.library.parameter.ValueConversionExceptionWithDetailedCause")));
    }

    if (Boolean.TRUE.equals(useCause))
    {
      return new ValueConversionException(message, cause);
    }

    final ValueConversionException o = (ValueConversionException) localInstance.get();
    if (o == null)
    {
      final ValueConversionException retval = new ValueConversionException(message);
      localInstance.set(retval);
      return retval;
    }

    o.fillInStackTrace();
    o.message = message;
    o.printStackTrace();
    return o;
  }
}
