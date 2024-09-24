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

package org.pentaho.reporting.libraries.fonts.cache;

import org.pentaho.reporting.libraries.fonts.registry.FontKey;
import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;

import java.util.HashMap;

/**
 * The first level font cache is always a perfect cache. It holds all fonts used by the current storage in memory. Once
 * finished, the cache-contents are pushed downwards into the second level cache.
 *
 * @author : Thomas Morgner
 */
public class FirstLevelFontCache implements FontCache {
  private HashMap backend;
  private FontCache secondLevelCache;

  public FirstLevelFontCache( final FontCache secondLevelCache ) {
    this.secondLevelCache = secondLevelCache;
    backend = new HashMap();
  }

  public FontMetrics getFontMetrics( final FontKey fontKey ) {
    final FontMetrics metrics = (FontMetrics) backend.get( fontKey );
    if ( metrics != null ) {
      return metrics;
    }
    if ( secondLevelCache == null ) {
      return null;
    }
    final FontMetrics fromSecondLevel = secondLevelCache.getFontMetrics( fontKey );
    if ( fromSecondLevel != null ) {
      backend.put( fontKey.clone(), fromSecondLevel );
      return fromSecondLevel;
    }
    return null;
  }

  public void putFontMetrics( final FontKey key, final FontMetrics fontMetrics ) {
    backend.put( key, fontMetrics );
    if ( secondLevelCache != null ) {
      secondLevelCache.putFontMetrics( key, fontMetrics );
    }
  }

  public void commit() {
    backend.clear();
    secondLevelCache = null;
  }
}
