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

/**
 * A font cache is a two-level structure.
 * <p/>
 * FontMetrics that are currently in use are held completely in memory (so that at a given time only one font-metric
 * object exists). This way, the font-metrics object can employ internal caches for kerning, charwidth and baselines
 * without having to spend ages with synchronization between the local copy and the global cache.
 * <p/>
 * Once the font-metrics are no longer used, they are pushed down to the second-level cache, and may be invalidated or
 * removed later. Fonts are transfered to the second-level cache when the font-storage implementation is closed.
 *
 * @author : Thomas Morgner
 */
public interface FontCache {
  public FontMetrics getFontMetrics( final FontKey fontKey );

  public void putFontMetrics( final FontKey key, final FontMetrics fontMetrics );

  public void commit();
}
