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

import org.pentaho.reporting.libraries.base.util.LFUMap;
import org.pentaho.reporting.libraries.fonts.registry.FontKey;
import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;

public class LeastFrequentlyUsedCache implements FontCache {
  private LFUMap<FontKey, FontMetrics> map;

  public LeastFrequentlyUsedCache( final int cacheSize ) {
    // having at least 3 entries saves me a lot of coding and thus gives more performance ..
    this.map = new LFUMap<FontKey, FontMetrics>( cacheSize );
  }

  public synchronized FontMetrics getFontMetrics( final FontKey fontKey ) {
    return map.get( fontKey );
  }

  public synchronized void putFontMetrics( final FontKey key, final FontMetrics metrics ) {
    map.put( key, metrics );
  }

  public void commit() {
    // no op, as we have no deeper level.
  }
}
