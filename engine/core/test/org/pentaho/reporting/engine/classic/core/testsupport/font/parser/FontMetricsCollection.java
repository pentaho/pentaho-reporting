package org.pentaho.reporting.engine.classic.core.testsupport.font.parser;

import java.util.LinkedHashMap;

import org.pentaho.reporting.engine.classic.core.testsupport.font.LocalFontFamily;
import org.pentaho.reporting.engine.classic.core.testsupport.font.LocalFontMetricsBase;

public class FontMetricsCollection
{
  private LinkedHashMap<String, LocalFontMetricsBase> fontMetrics;
  private LinkedHashMap<String, LocalFontFamily> fontFamilies;
  private String fallbackName;

  public FontMetricsCollection()
  {
    fontMetrics = new LinkedHashMap<String, LocalFontMetricsBase>();
    fontFamilies = new LinkedHashMap<String, LocalFontFamily>();
  }

  public String getFallbackName()
  {
    return fallbackName;
  }

  public void setFallbackName(final String fallbackName)
  {
    this.fallbackName = fallbackName;
  }

  public void defineMetrics(final String source, final LocalFontMetricsBase localFontMetricsBase)
  {
    fontMetrics.put(source, localFontMetricsBase);
  }

  public LocalFontMetricsBase getMetrics(final String source)
  {
    return fontMetrics.get(source);
  }

  public void addFontFamily(final LocalFontFamily fontFamily)
  {
    fontFamilies.put(fontFamily.getFamilyName(), fontFamily);
  }

  public String[] getFontFamilies()
  {
    return fontFamilies.keySet().toArray(new String[fontFamilies.size()]);
  }

  public LocalFontFamily getFontFamily(final String name)
  {
    return fontFamilies.get(name);
  }
}
