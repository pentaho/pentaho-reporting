package gui;

import java.util.Locale;

public interface EditableMetaData
{
  public void setMetaAttribute(final String attributeName, final Locale locale, final String value);
  public String getMetaAttribute(final String attributeName, final Locale locale);
  public String getName();

  public int getGroupingOrdinal(Locale locale);
  public int getItemOrdinal(Locale locale);

  public boolean isValidValue(final String attributeName, final Locale locale);
  public boolean isValid(final Locale locale, boolean deepCheck);
  public boolean isModified();
  public String printBundleText(final Locale locale);
}
