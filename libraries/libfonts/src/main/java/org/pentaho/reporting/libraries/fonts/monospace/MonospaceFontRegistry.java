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

package org.pentaho.reporting.libraries.fonts.monospace;

import org.pentaho.reporting.libraries.fonts.LibFontBoot;
import org.pentaho.reporting.libraries.fonts.cache.FontCache;
import org.pentaho.reporting.libraries.fonts.registry.FontFamily;
import org.pentaho.reporting.libraries.fonts.registry.FontMetricsFactory;
import org.pentaho.reporting.libraries.fonts.registry.FontRegistry;

import java.util.HashMap;

/**
 * Creation-Date: 13.05.2007, 13:12:04
 *
 * @author Thomas Morgner
 */
public class MonospaceFontRegistry implements FontRegistry {
  private static FontCache secondLevelCache;

  protected static synchronized FontCache internalGetSecondLevelCache() {
    if ( secondLevelCache == null ) {
      secondLevelCache = LibFontBoot.getInstance().createDefaultCache();
    }
    return secondLevelCache;
  }

  private HashMap<String, MonospaceFontFamily> fontFamilies;
  private float lpi;
  private float cpi;
  private MonospaceFontFamily fallback;

  public MonospaceFontRegistry( final float lpi, final float cpi ) {
    this.lpi = lpi;
    this.cpi = cpi;
    this.fontFamilies = new HashMap<String, MonospaceFontFamily>();
    this.fallback = new MonospaceFontFamily( "Monospace", lpi, cpi );
  }

  public FontCache getSecondLevelCache() {
    return internalGetSecondLevelCache();
  }

  public void add( final MonospaceFontFamily family ) {
    this.fontFamilies.put( family.getFamilyName(), family );
  }

  public MonospaceFontFamily getFallback() {
    return fallback;
  }

  public void setFallback( final MonospaceFontFamily fallback ) {
    this.fallback = fallback;
  }

  public void initialize() {

  }

  /**
   * Tries to find a font family with the given name, looking through all alternative font names if neccessary.
   *
   * @param name
   * @return the font family or null, if there is no such family.
   */
  public FontFamily getFontFamily( final String name ) {
    final FontFamily fontFamily = fontFamilies.get( name );
    if ( fontFamily != null ) {
      return fontFamily;
    }
    return fallback;
  }

  public String[] getRegisteredFamilies() {
    return fontFamilies.keySet().toArray( new String[ fontFamilies.size() ] );
  }

  public String[] getAllRegisteredFamilies() {
    return fontFamilies.keySet().toArray( new String[ fontFamilies.size() ] );
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
    return new MonospaceFontMetricsFactory( lpi, cpi );
  }
}
