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

package org.pentaho.reporting.libraries.fonts.itext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;
import org.pentaho.reporting.libraries.fonts.cache.FirstLevelFontCache;
import org.pentaho.reporting.libraries.fonts.encoding.EncodingRegistry;
import org.pentaho.reporting.libraries.fonts.registry.FontContext;
import org.pentaho.reporting.libraries.fonts.registry.FontIdentifier;
import org.pentaho.reporting.libraries.fonts.registry.FontKey;
import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;
import org.pentaho.reporting.libraries.fonts.registry.FontRegistry;
import org.pentaho.reporting.libraries.fonts.registry.FontStorage;

/**
 * Creation-Date: 22.07.2007, 17:54:43
 *
 * @author Thomas Morgner
 */
public class ITextFontStorage implements FontStorage {
  private static class EncodingFontContextWrapper implements FontContext {
    private FontContext context;
    private String defaultEncoding;

    private EncodingFontContextWrapper( final FontContext context,
                                        final String defaultEncoding ) {
      this.context = context;
      this.defaultEncoding = defaultEncoding;
    }

    public String getEncoding() {
      return defaultEncoding;
    }

    public boolean isEmbedded() {
      return context.isEmbedded();
    }

    public boolean isAntiAliased() {
      return context.isAntiAliased();
    }

    public boolean isFractionalMetrics() {
      return context.isFractionalMetrics();
    }

    public double getFontSize() {
      return context.getFontSize();
    }
  }

  private static class EncodingFontKey extends FontKey {
    private String encoding;

    public EncodingFontKey( final FontIdentifier identifier,
                            final boolean aliased,
                            final boolean fractional,
                            final double fontSize,
                            final String encoding ) {
      super( identifier, aliased, fractional, fontSize );
      this.encoding = encoding;
    }

    public EncodingFontKey() {
    }

    public String getEncoding() {
      return encoding;
    }

    public void setEncoding( final String encoding ) {
      this.encoding = encoding;
    }

    public boolean equals( final Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }
      if ( !super.equals( o ) ) {
        return false;
      }

      final EncodingFontKey that = (EncodingFontKey) o;

      if ( encoding != null ? !encoding.equals( that.encoding ) : that.encoding != null ) {
        return false;
      }

      return true;
    }

    public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + ( encoding != null ? encoding.hashCode() : 0 );
      return result;
    }
  }

  private static final Log logger = LogFactory.getLog( ITextFontStorage.class );
  private ITextFontRegistry registry;
  private ITextFontMetricsFactory metricsFactory;
  private EncodingFontKey lookupKey;
  private FirstLevelFontCache knownMetrics;
  private String defaultEncoding;
  private int hits;
  private int misses;

  public ITextFontStorage( final ITextFontRegistry registry ) {
    this( registry, EncodingRegistry.getPlatformDefaultEncoding() );
  }

  public ITextFontStorage( final ITextFontRegistry registry,
                           final String encoding ) {
    ArgumentNullException.validate( "registry", registry );
    ArgumentNullException.validate( "encoding", encoding );

    this.lookupKey = new EncodingFontKey();
    this.knownMetrics = new FirstLevelFontCache( registry.getSecondLevelCache() );
    this.registry = registry;
    this.defaultEncoding = encoding;
    this.metricsFactory = (ITextFontMetricsFactory) registry.createMetricsFactory();
  }

  public String getDefaultEncoding() {
    return defaultEncoding;
  }

  public void setDefaultEncoding( final String defaultEncoding ) {
    ArgumentNullException.validate( "defaultEncoding", defaultEncoding );

    this.defaultEncoding = defaultEncoding;
  }

  public FontRegistry getFontRegistry() {
    return registry;
  }

  public FontMetrics getFontMetrics( final FontIdentifier rawRecord,
                                     final FontContext context ) {
    if ( rawRecord == null ) {
      throw new NullPointerException();
    }
    if ( context == null ) {
      throw new NullPointerException();
    }

    final String effectiveEncoding;
    final String contextEncoding = context.getEncoding();
    if ( contextEncoding == null ) {
      effectiveEncoding = defaultEncoding;
    } else {
      effectiveEncoding = contextEncoding;
    }

    lookupKey.setAliased( context.isAntiAliased() );
    lookupKey.setFontSize( context.getFontSize() );
    lookupKey.setIdentifier( rawRecord );
    lookupKey.setFractional( context.isFractionalMetrics() );
    lookupKey.setEncoding( effectiveEncoding );

    final FontMetrics cachedMetrics = knownMetrics.getFontMetrics( lookupKey );
    if ( cachedMetrics != null ) {
      hits += 1;
      return cachedMetrics;
    }

    misses += 1;

    final EncodingFontContextWrapper contextWrapper = new EncodingFontContextWrapper( context, effectiveEncoding );
    final FontMetrics metrics = metricsFactory.createMetrics( rawRecord, contextWrapper );
    final EncodingFontKey key = new EncodingFontKey( rawRecord, context.isAntiAliased(),
      context.isFractionalMetrics(), context.getFontSize(), effectiveEncoding );
    knownMetrics.putFontMetrics( key, metrics );
    return metrics;
  }

  public void commit() {
    logger.debug( "Font-Storage: hits=" + hits + ", misses=" + misses );
    metricsFactory.close();
    knownMetrics.commit();
  }
}
