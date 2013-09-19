package gui;

import java.util.HashMap;
import java.util.Locale;

import org.pentaho.reporting.engine.classic.core.metadata.AbstractMetaData;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;

public abstract class AbstractEditableMetaData implements EditableMetaData
{
  private static class CompoundKey
  {
    private Locale locale;
    private String attributeName;

    private CompoundKey(final Locale locale, final String attributeName)
    {
      this.locale = locale;
      this.attributeName = attributeName;
    }

    public boolean equals(final Object o)
    {
      if (this == o)
      {
        return true;
      }
      if (!(o instanceof CompoundKey))
      {
        return false;
      }

      final CompoundKey that = (CompoundKey) o;

      if (!attributeName.equals(that.attributeName))
      {
        return false;
      }
      if (!locale.equals(that.locale))
      {
        return false;
      }

      return true;
    }

    public int hashCode()
    {
      int result = locale.hashCode();
      result = 31 * result + attributeName.hashCode();
      return result;
    }
  }

  private HashMap<CompoundKey, String> editResults;
  private AbstractMetaData backend;

  public AbstractEditableMetaData(final AbstractMetaData backend)
  {
    if (backend == null)
    {
      throw new NullPointerException();
    }
    this.backend = backend;
    this.editResults = new HashMap<CompoundKey, String>();
  }

  protected AbstractMetaData getBackend()
  {
    return backend;
  }

  public boolean isModified()
  {
    return editResults.isEmpty() == false;
  }

  public String getName()
  {
    final String s = backend.getName();
    final String[] strings = StringUtils.split(s, ".");
    if (strings.length > 0)
    {
      return strings[strings.length - 1];
    }
    return s;
  }

  public boolean isValidValue(final String attributeName, final Locale locale)
  {
    final String s = getMetaAttribute(attributeName, locale);
    if ("display-name".equals(attributeName) ||
        "group".equals(attributeName))
    {
      return StringUtils.isEmpty(s) == false;
    }
    if ("ordinal".equals(attributeName) ||
        "grouping.ordinal".equals(attributeName))
    {
      return ParserUtil.parseInt(s, Integer.MAX_VALUE) != Integer.MAX_VALUE;
    }
    
    return s != null;
  }

  public void setMetaAttribute(final String attributeName, final Locale locale, final String value)
  {
    if (value == null)
    {
      editResults.put(new CompoundKey(locale, attributeName), "");
    }
    else
    {
      editResults.put(new CompoundKey(locale, attributeName), value);
    }
  }

  public String getMetaAttribute(final String attributeName, final Locale locale)
  {
    final CompoundKey key = new CompoundKey(locale, attributeName);
    final String override = editResults.get(key);
    if (override != null || editResults.containsKey(key))
    {
      return override;
    }
    return backend.getMetaAttribute(attributeName, locale);
  }

  public final int getGroupingOrdinal(final Locale locale)
  {
    final String strOrd = getMetaAttribute("grouping.ordinal", locale);
    return ParserUtil.parseInt(strOrd, Integer.MAX_VALUE);
  }

  public final int getItemOrdinal(final Locale locale)
  {
    final String strOrd = getMetaAttribute("ordinal", locale);
    return ParserUtil.parseInt(strOrd, Integer.MAX_VALUE);
  }
}
