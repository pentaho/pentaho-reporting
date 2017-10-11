/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence;

import java.util.HashMap;

public abstract class AbstractSequence implements Sequence {
  private HashMap<String, Object> parameter;

  protected AbstractSequence() {
    parameter = new HashMap<String, Object>();
  }

  public void setParameter( final String name, final Object value ) {
    parameter.put( name, value );
  }

  public Object getParameter( final String name ) {
    return parameter.get( name );
  }

  public Object clone() {
    try {
      final AbstractSequence clone = (AbstractSequence) super.clone();
      clone.parameter = (HashMap<String, Object>) parameter.clone();
      return clone;
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException( e );
    }
  }

  public Object getParameter( final String name, final Object defaultValue ) {
    final Object o = parameter.get( name );
    if ( o != null ) {
      return o;
    }
    return defaultValue;
  }

  public <T> T getTypedParameter( final String name, final Class<T> type ) {
    final Object o = parameter.get( name );
    if ( type.isInstance( o ) ) {
      return (T) o;
    }
    return null;
  }

  public <T> T getTypedParameter( final String name, final Class<T> type, final T defaultValue ) {
    final T o = getTypedParameter( name, type );
    if ( o != null ) {
      return o;
    }
    return defaultValue;
  }

}
