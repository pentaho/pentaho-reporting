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
