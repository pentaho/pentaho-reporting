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
