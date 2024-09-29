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


package org.pentaho.reporting.engine.classic.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.common.AttributeMap;

/**
 * A attribute map that keeps track of changes.
 *
 * @author Thomas Morgner
 */
public class ReportAttributeMap<T> extends AttributeMap<T> {
  private static final Log logger = LogFactory.getLog( ReportAttributeMap.class );

  public static final ReportAttributeMap EMPTY_MAP = new ReportAttributeMap().createUnmodifiableMap();

  public static <T> ReportAttributeMap<T> emptyMap() {
    return (ReportAttributeMap<T>) EMPTY_MAP;
  }

  private long changeTracker;

  public ReportAttributeMap() {
    this( 0 );
  }

  public ReportAttributeMap( final long changeTracker ) {
    this.changeTracker = changeTracker;
  }

  public ReportAttributeMap( final ReportAttributeMap copy ) {
    super( copy );
    this.changeTracker = copy.changeTracker;
  }

  public ReportAttributeMap<T> createUnmodifiableMap() {
    return new Immutable<>( this );
  }

  public ReportAttributeMap<T> clone() {
    return (ReportAttributeMap<T>) super.clone();
  }

  public long getChangeTracker() {
    return changeTracker;
  }

  public <TS extends T> TS getAttributeTyped( final String namespace, final String attribute, final Class<TS> filter ) {
    T val = getAttribute( namespace, attribute );
    if ( filter.isInstance( val ) ) {
      return filter.cast( val );
    }
    return null;
  }

  @Override
  public T setAttribute( final String namespace, final String attribute, final T value ) {
    final T oldValue = super.setAttribute( namespace, attribute, value );
    if ( oldValue == value ) {
      return oldValue;
    }

    if ( ObjectUtilities.equal( oldValue, value ) == false ) {
      changeTracker += 1;
    }
    return oldValue;
  }

  public boolean isReadOnly() {
    return false;
  }

  @Override
  public void putAll( final AttributeMap<T> attributeMap ) {
    super.putAll( attributeMap );
    changeTracker += 1;
  }

  private static class Immutable<T> extends ReportAttributeMap<T> {

    public Immutable( ReportAttributeMap<T> map ) {
      super( map );
    }

    public final boolean isReadOnly() {
      return true;
    }

    public final T setAttribute( final String namespace, final String attribute, final T value ) {
      throw exception();
    }

    public final void putAll( final AttributeMap<T> attributeMap ) {
      throw exception();
    }

    private UnsupportedOperationException exception() {
      return new UnsupportedOperationException( "This collection is marked as read-only" );
    }

    public ReportAttributeMap<T> clone() {
      // okay, since this instance is immutable
      return this;
    }
  }
}
