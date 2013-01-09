package org.pentaho.reporting.libraries.designtime.swing.date;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

public class DateConverter
{
  public static Date convertToDateType(final Date date, final Class targetType)
  {
    if (targetType.equals(java.sql.Date.class))
    {
      return new java.sql.Date(date.getTime());
    }

    if (targetType.equals(Time.class))
    {
      return new Time(date.getTime());
    }
    if (targetType.equals(Timestamp.class))
    {
      return new Timestamp(date.getTime());
    }

    return new Date(date.getTime());
  }
}
