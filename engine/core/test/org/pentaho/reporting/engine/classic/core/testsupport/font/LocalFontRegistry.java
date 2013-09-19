package org.pentaho.reporting.engine.classic.core.testsupport.font;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.testsupport.font.parser.FontMetricsCollection;
import org.pentaho.reporting.libraries.fonts.LibFontBoot;
import org.pentaho.reporting.libraries.fonts.cache.FontCache;
import org.pentaho.reporting.libraries.fonts.registry.FontFamily;
import org.pentaho.reporting.libraries.fonts.registry.FontIdentifier;
import org.pentaho.reporting.libraries.fonts.registry.FontMetricsFactory;
import org.pentaho.reporting.libraries.fonts.registry.FontRegistry;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class LocalFontRegistry implements FontRegistry
{
  private static FontCache secondLevelCache;
  private FontMetricsCollection fontMetricsCollection;

  protected static synchronized FontCache internalGetSecondLevelCache()
  {
    if (secondLevelCache == null)
    {
      secondLevelCache = LibFontBoot.getInstance().createDefaultCache();
    }
    return secondLevelCache;
  }

  public LocalFontRegistry()
  {
  }

  public LocalFontMetricsBase getFontMetricsBase(final FontIdentifier identifier)
  {
    if ((identifier instanceof LocalFontRecord) == false)
    {
      throw new IllegalStateException();
    }
    final LocalFontRecord record = (LocalFontRecord) identifier;
    return fontMetricsCollection.getMetrics(record.getOriginatingFile());
  }

  public void initialize()
  {
    try
    {
      final ResourceManager resourceManager = new ResourceManager();
      resourceManager.registerDefaults();
      final Resource resource = resourceManager.createDirectly("res://fonts.xml", FontMetricsCollection.class);
      this.fontMetricsCollection = (FontMetricsCollection) resource.getResource();
    }
    catch (ResourceException e)
    {
      throw new IllegalStateException(e);
    }
  }

  public FontCache getSecondLevelCache()
  {
    return internalGetSecondLevelCache();
  }

  /**
   * Tries to find a font family with the given name, looking through all alternative font names if neccessary.
   *
   * @param name
   * @return the font family or null, if there is no such family.
   */
  public FontFamily getFontFamily(final String name)
  {
    final LocalFontFamily fontFamily = fontMetricsCollection.getFontFamily(name);
    if (fontFamily != null)
    {
      return fontFamily;
    }
    final String fallbackName = fontMetricsCollection.getFallbackName();
    if (fallbackName != null && fallbackName.equals(name) == false)
    {
      return getFontFamily(fallbackName);
    }
    return null;
  }

  public String[] getRegisteredFamilies()
  {
    return fontMetricsCollection.getFontFamilies();
  }

  public String[] getAllRegisteredFamilies()
  {
    return fontMetricsCollection.getFontFamilies();
  }

  public FontMetricsFactory createMetricsFactory()
  {
    return new LocalFontMetricsFactory(this);
  }

  public static void main(String[] args)
  {
    ClassicEngineBoot.getInstance().start();
    LocalFontRegistry registry = new LocalFontRegistry();
    registry.initialize();
  }
}
