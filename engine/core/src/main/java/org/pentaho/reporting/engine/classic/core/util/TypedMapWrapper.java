/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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
