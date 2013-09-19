package org.pentaho.reporting.libraries.formula;

import java.util.Locale;
import java.util.TimeZone;

public interface FormulaContextFactory
{
  public FormulaContext create(Locale locale, TimeZone timeZone);
}
