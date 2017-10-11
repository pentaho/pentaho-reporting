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

package org.pentaho.reporting.libraries.fonts.registry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.fonts.cache.FirstLevelFontCache;

/**
 * Creation-Date: 15.12.2005, 18:07:53
 *
 * @author Thomas Morgner
 */
public class DefaultFontStorage implements FontStorage {
  private static final Log logger = LogFactory.getLog( DefaultFontStorage.class );
  private FirstLevelFontCache knownMetrics;
  private FontRegistry registry;
  private FontMetricsFactory metricsFactory;
  private FontKey lookupKey;
  private int hits;
  private int misses;

  public DefaultFontStorage( final FontRegistry registry ) {
    this.knownMetrics = new FirstLevelFontCache( registry.getSecondLevelCache() );
    this.registry = registry;
    this.metricsFactory = registry.createMetricsFactory();
    this.lookupKey = new FontKey();
  }

  public FontRegistry getFontRegistry() {
    return registry;
  }

  public FontMetrics getFontMetrics( final FontIdentifier record,
                                     final FontContext context ) {
    if ( record == null ) {
      throw new NullPointerException();
    }
    if ( context == null ) {
      throw new NullPointerException();
    }

    lookupKey.setAliased( context.isAntiAliased() );
    lookupKey.setFontSize( context.getFontSize() );
    lookupKey.setIdentifier( record );
    lookupKey.setFractional( context.isFractionalMetrics() );

    final FontMetrics cachedMetrics = knownMetrics.getFontMetrics( lookupKey );
    if ( cachedMetrics != null ) {
      hits += 1;
      return cachedMetrics;
    }

    misses += 1;
    final FontKey key = new FontKey( record, context.isAntiAliased(),
      context.isFractionalMetrics(), context.getFontSize() );
    final FontMetrics metrics = metricsFactory.createMetrics( record, context );
    knownMetrics.putFontMetrics( key, metrics );
    return metrics;
  }

  public void commit() {
    logger.debug( "Font-Storage: hits=" + hits + ", misses=" + misses );
    knownMetrics.commit();
  }
}
