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

package org.pentaho.reporting.libraries.fonts.merge;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.fonts.cache.FirstLevelFontCache;
import org.pentaho.reporting.libraries.fonts.registry.FontContext;
import org.pentaho.reporting.libraries.fonts.registry.FontIdentifier;
import org.pentaho.reporting.libraries.fonts.registry.FontKey;
import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;
import org.pentaho.reporting.libraries.fonts.registry.FontMetricsFactory;
import org.pentaho.reporting.libraries.fonts.registry.FontRegistry;
import org.pentaho.reporting.libraries.fonts.registry.FontStorage;

import java.util.HashMap;

/**
 * Creation-Date: 20.07.2007, 19:35:31
 *
 * @author Thomas Morgner
 */
public class CompoundFontStorage implements FontStorage {
  private static final Log logger = LogFactory.getLog( CompoundFontStorage.class );
  private CompoundFontRegistry fontRegistry;
  private HashMap metricsFactories;
  private FirstLevelFontCache firstLevelFontCache;
  private FontKey lookupKey;
  private int hits;
  private int misses;

  public CompoundFontStorage() {
    this.fontRegistry = new CompoundFontRegistry();
    this.metricsFactories = new HashMap();
    this.firstLevelFontCache = new FirstLevelFontCache( fontRegistry.getSecondLevelCache() );
    this.lookupKey = new FontKey();
  }

  public void addRegistry( final FontRegistry registry ) {
    fontRegistry.addRegistry( registry );
  }

  public FontRegistry getFontRegistry() {
    return fontRegistry;
  }

  public FontMetrics getFontMetrics( final FontIdentifier record, final FontContext context ) {
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

    final FontMetrics cachedMetrics = firstLevelFontCache.getFontMetrics( lookupKey );
    if ( cachedMetrics != null ) {
      hits += 1;
      return cachedMetrics;
    }

    final CompoundFontIdentifier cid = (CompoundFontIdentifier) record;
    final FontRegistry registry = cid.getRegistry();
    FontMetricsFactory metricsFactory = (FontMetricsFactory) metricsFactories.get( registry );
    if ( metricsFactory == null ) {
      metricsFactory = registry.createMetricsFactory();
      metricsFactories.put( registry, metricsFactory );
    }

    misses += 1;

    final FontKey key = new FontKey( record, context.isAntiAliased(),
      context.isFractionalMetrics(), context.getFontSize() );
    final FontMetrics metrics = metricsFactory.createMetrics( cid.getIdentifier(), context );
    firstLevelFontCache.putFontMetrics( key, metrics );
    return metrics;
  }

  public void commit() {
    logger.debug( "Font-Storage: hits=" + hits + ", misses=" + misses );
    firstLevelFontCache.commit();
  }
}
