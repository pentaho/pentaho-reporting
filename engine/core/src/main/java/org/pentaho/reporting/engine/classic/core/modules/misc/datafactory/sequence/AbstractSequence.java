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
