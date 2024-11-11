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


package org.pentaho.reporting.engine.classic.core.util;

import org.pentaho.reporting.libraries.base.util.ArgumentNullException;

import java.util.Map;

public class TypedMapWrapper<K, V> {
  private Map<K, V> backend;

  public TypedMapWrapper( final Map<K, V> backend ) {
    ArgumentNullException.validate( "backend", backend );

    this.backend = backend;
  }

  public <T> T get( K key, Class<T> context ) {
    Object o = backend.get( key );
    if ( context.isInstance( o ) ) {
      return context.cast( o );
    }
    return null;
  }

  public <T> T get( K key, T defaultValue, Class<T> context ) {
    Object o = backend.get( key );
    if ( context.isInstance( o ) ) {
      return context.cast( o );
    }
    return defaultValue;
  }

  public Boolean exists( final K key ) {
    return backend.containsKey( key );
  }
}
