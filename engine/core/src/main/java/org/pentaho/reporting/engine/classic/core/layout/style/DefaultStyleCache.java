/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.layout.style;

import java.util.List;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.base.util.LFUMap;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class DefaultStyleCache implements StyleCache {
  private static class CacheKey {
    private InstanceID instanceId;
    private String styleClass;

    protected CacheKey() {
    }

    protected CacheKey( final InstanceID instanceId, final String styleClass ) {
      if ( instanceId == null ) {
        throw new NullPointerException();
      }
      if ( styleClass == null ) {
        throw new NullPointerException();
      }
      this.instanceId = instanceId;
      this.styleClass = styleClass;
    }

    public Object getInstanceId() {
      return instanceId;
    }

    public void setInstanceId( final InstanceID instanceId ) {
      if ( instanceId == null ) {
        throw new NullPointerException();
      }
      this.instanceId = instanceId;
    }

    public String getStyleClass() {
      return styleClass;
    }

    public void setStyleClass( final String styleClass ) {
      this.styleClass = styleClass;
    }

    public boolean equals( final Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }

      final CacheKey cacheKey = (CacheKey) o;

      if ( !instanceId.equals( cacheKey.instanceId ) ) {
        return false;
      }
      if ( !styleClass.equals( cacheKey.styleClass ) ) {
        return false;
      }

      return true;
    }

    public int hashCode() {
      int result = instanceId.hashCode();
      result = 31 * result + styleClass.hashCode();
      return result;
    }

    public String toString() {
      return "CacheKey{"
        + "instanceId=" + instanceId
        + ", styleClass='" + styleClass + '\''
        + '}';
    }
  }

  private static class CacheCarrier {
    private long changeTracker;
    private SimpleStyleSheet styleSheet;

    protected CacheCarrier( final long changeTracker, final SimpleStyleSheet styleSheet ) {
      this.changeTracker = changeTracker;
      this.styleSheet = styleSheet;
    }

    public long getChangeTracker() {
      return changeTracker;
    }

    public SimpleStyleSheet getStyleSheet() {
      return styleSheet;
    }
  }

  private LFUMap<CacheKey, CacheCarrier> cache;
  private CacheKey lookupKey;
  private List<StyleKey> validateKeys;
  private boolean validateCache;
  private int cacheHits;
  private int cacheMiss;
  private String name;

  public DefaultStyleCache( String name ) {
    this();
    this.name = name;
  }

  public DefaultStyleCache() {

    this.lookupKey = new CacheKey();
    this.cache = new LFUMap<CacheKey, CacheCarrier>( 500 );
    this.validateCache =
        "true".equals( ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.layout.style.ValidateStyleCacheResults" ) );
  }

  public SimpleStyleSheet getStyleSheet( final StyleSheet parent ) {
    final InstanceID id = parent.getId();
    final String styleClass = parent.getClass().getName();
    lookupKey.setStyleClass( styleClass );
    lookupKey.setInstanceId( id );
    final CacheCarrier cc = cache.get( lookupKey );
    if ( cc == null ) {
      final CacheKey key = new CacheKey( id, styleClass );
      final SimpleStyleSheet styleSheet = new SimpleStyleSheet( parent );
      cache.put( key, new CacheCarrier( parent.getChangeTracker(), styleSheet ) );
      cacheMiss += 1;
      return styleSheet;
    }

    if ( cc.getChangeTracker() != parent.getChangeTracker() ) {
      final CacheKey key = new CacheKey( id, styleClass );
      final SimpleStyleSheet styleSheet = new SimpleStyleSheet( parent );
      cache.put( key, new CacheCarrier( parent.getChangeTracker(), styleSheet ) );
      cacheMiss += 1;
      return styleSheet;
    }

    validateCacheResult( parent, cc.getStyleSheet() );
    cacheHits += 1;
    return cc.getStyleSheet();
  }

  private void validateCacheResult( final StyleSheet s1, final StyleSheet s2 ) {
    if ( validateCache == false ) {
      return;
    }

    if ( validateKeys == null ) {
      validateKeys = StyleKey.getDefinedStyleKeysList();
    }

    for ( final StyleKey validateKey : validateKeys ) {
      final Object o1 = s1.getStyleProperty( validateKey );
      final Object o2 = s2.getStyleProperty( validateKey );
      if ( ObjectUtilities.equal( o1, o2 ) ) {
        continue;
      }

      throw new InvalidReportStateException( "Cache-Failure on " + s1.getId() + " " + validateKey + " "
        + o1 + " vs " + o2 + " [" + s1.getChangeTracker() + "; " + s2.getChangeTracker() + "]" );
    }
  }

  public String printPerformanceStats() {
    final int total = cacheHits + cacheMiss;
    return ( "StyleCache: " + name + " "
      + "Total=" + total
      + " Hits=" + cacheHits + " (" + ( 100f * cacheHits / Math.max( 1, total ) ) + "%)"
      + " Miss=" + cacheMiss + " (" + ( 100f * cacheMiss / Math.max( 1, total ) ) + "%)" );
  }
}
