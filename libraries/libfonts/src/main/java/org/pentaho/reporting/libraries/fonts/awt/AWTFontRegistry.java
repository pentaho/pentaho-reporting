/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.libraries.fonts.awt;

import org.pentaho.reporting.libraries.fonts.LibFontBoot;
import org.pentaho.reporting.libraries.fonts.cache.FontCache;
import org.pentaho.reporting.libraries.fonts.registry.FontFamily;
import org.pentaho.reporting.libraries.fonts.registry.FontMetricsFactory;
import org.pentaho.reporting.libraries.fonts.registry.FontRegistry;

import java.awt.*;
import java.util.HashMap;

/**
 * A very simple font registry wrapping around the AWT font classes.
 *
 * @author Thomas Morgner
 */
public class AWTFontRegistry implements FontRegistry {
  private static FontCache secondLevelCache;

  protected static synchronized FontCache internalGetSecondLevelCache() {
    if ( secondLevelCache == null ) {
      secondLevelCache = LibFontBoot.getInstance().createDefaultCache();
    }
    return secondLevelCache;
  }

  private HashMap<String, AWTFontFamily> fontFamilyCache;

  public AWTFontRegistry() {
    fontFamilyCache = new HashMap<String, AWTFontFamily>();
  }

  public void initialize() {
  }

  public FontCache getSecondLevelCache() {
    return internalGetSecondLevelCache();
  }

  /**
   * Creates a new font metrics factory. That factory is specific to a certain font registry and is not required to
   * handle font records from foreign font registries.
   * <p/>
   * A font metrics factory should never be used on its own. It should be embedded into and used by a FontStorage
   * implementation.
   *
   * @return
   */
  public FontMetricsFactory createMetricsFactory() {
    return new AWTFontMetricsFactory();
  }

  public FontFamily getFontFamily( final String name ) {
    final AWTFontFamily fontFamily = fontFamilyCache.get( name );
    if ( fontFamily != null ) {
      return fontFamily;
    }
    final AWTFontFamily awtFontFamily = new AWTFontFamily( name );
    fontFamilyCache.put( name, awtFontFamily );
    return awtFontFamily;
  }

  public String[] getRegisteredFamilies() {
    final GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
    return genv.getAvailableFontFamilyNames();
  }

  public String[] getAllRegisteredFamilies() {
    return getRegisteredFamilies();
  }
}
