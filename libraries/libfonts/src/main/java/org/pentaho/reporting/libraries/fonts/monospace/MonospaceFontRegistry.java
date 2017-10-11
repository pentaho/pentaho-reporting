/*
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

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
