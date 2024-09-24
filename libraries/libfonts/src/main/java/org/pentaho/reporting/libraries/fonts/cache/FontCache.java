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
