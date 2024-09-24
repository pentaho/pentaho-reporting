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
