package org.pentaho.reporting.libraries.designtime.swing.settings;

import java.util.Locale;
import java.util.TimeZone;

public interface LocaleSettings
{
  public String getDateFormatPattern();
  public String getTimeFormatPattern();
  public String getDatetimeFormatPattern();

  public Locale getLocale();
  public TimeZone getTimeZone();
}
