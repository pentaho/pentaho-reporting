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

package org.pentaho.reporting.libraries.fonts.merge;

import org.pentaho.reporting.libraries.fonts.LibFontBoot;
import org.pentaho.reporting.libraries.fonts.cache.FontCache;
import org.pentaho.reporting.libraries.fonts.registry.FontFamily;
import org.pentaho.reporting.libraries.fonts.registry.FontMetricsFactory;
import org.pentaho.reporting.libraries.fonts.registry.FontRegistry;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Creation-Date: 20.07.2007, 18:46:04
 *
 * @author Thomas Morgner
 */
public class CompoundFontRegistry implements FontRegistry {
  private static FontCache secondLevelCache;

  protected static synchronized FontCache internalGetSecondLevelCache() {
    if ( secondLevelCache == null ) {
      secondLevelCache = LibFontBoot.getInstance().createDefaultCache();
    }
    return secondLevelCache;
  }

  private ArrayList registries;

  public CompoundFontRegistry() {
    this.registries = new ArrayList();
  }

  public FontCache getSecondLevelCache() {
    return internalGetSecondLevelCache();
  }

  public void addRegistry( final FontRegistry registry ) {
    if ( registry == null ) {
      throw new NullPointerException();
    }
    this.registries.add( registry );
  }

  public void initialize() {
    for ( int i = 0; i < registries.size(); i++ ) {
      final FontRegistry fontRegistry = (FontRegistry) registries.get( i );
      fontRegistry.initialize();
    }
  }

  public FontFamily getFontFamily( final String name ) {
    for ( int i = 0; i < registries.size(); i++ ) {
      final FontRegistry fontRegistry = (FontRegistry) registries.get( i );
      final FontFamily fontFamily = fontRegistry.getFontFamily( name );
      if ( fontFamily != null ) {
        return new CompoundFontFamily( fontFamily, fontRegistry );
      }
    }
    return null;
  }

  public String[] getRegisteredFamilies() {
    final HashSet registeredFamilies = new HashSet();

    for ( int i = 0; i < registries.size(); i++ ) {
      final FontRegistry fontRegistry = (FontRegistry) registries.get( i );
      final String[] fontFamilies = fontRegistry.getRegisteredFamilies();
      final int length = fontFamilies.length;
      for ( int j = 0; j < length; j++ ) {
        final String fontFamily = fontFamilies[ j ];
        registeredFamilies.add( fontFamily );
      }
    }
    return (String[]) registeredFamilies.toArray( new String[ registeredFamilies.size() ] );
  }

  public String[] getAllRegisteredFamilies() {
    final HashSet registeredFamilies = new HashSet();

    final int registryCount = registries.size();
    for ( int i = 0; i < registryCount; i++ ) {
      final FontRegistry fontRegistry = (FontRegistry) registries.get( i );
      final String[] fontFamilies = fontRegistry.getAllRegisteredFamilies();
      final int familyCount = fontFamilies.length;
      for ( int j = 0; j < familyCount; j++ ) {
        final String fontFamily = fontFamilies[ j ];
        registeredFamilies.add( fontFamily );
      }
    }
    return (String[]) registeredFamilies.toArray( new String[ registeredFamilies.size() ] );
  }

  public FontMetricsFactory createMetricsFactory() {
    throw new UnsupportedOperationException( "The CompoundFontRegistry cannot provide font-metrics directly." );
  }
}
