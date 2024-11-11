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


package org.pentaho.reporting.engine.classic.core.style;

import org.pentaho.reporting.engine.classic.core.util.ObjectStreamResolveException;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Creation-Date: 30.10.2005, 19:37:35
 *
 * @author Thomas Morgner
 */
public class WhitespaceCollapse implements Serializable {
  public static final WhitespaceCollapse COLLAPSE = new WhitespaceCollapse( "collapse" );
  public static final WhitespaceCollapse PRESERVE = new WhitespaceCollapse( "preserve" );
  public static final WhitespaceCollapse DISCARD = new WhitespaceCollapse( "discard" );
  public static final WhitespaceCollapse PRESERVE_BREAKS = new WhitespaceCollapse( "preserve-breaks" );
  private String type;

  private WhitespaceCollapse( final String type ) {
    this.type = type;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final WhitespaceCollapse that = (WhitespaceCollapse) o;

    if ( type != null ? !type.equals( that.type ) : that.type != null ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    return ( type != null ? type.hashCode() : 0 );
  }

  public String toString() {
    return type;
  }

  /**
   * Replaces the automatically generated instance with one of the enumeration instances.
   *
   * @return the resolved element
   * @throws java.io.ObjectStreamException
   *           if the element could not be resolved.
   * @noinspection UNUSED_SYMBOL
   */
  protected Object readResolve() throws ObjectStreamException {
    if ( this.type.equals( WhitespaceCollapse.COLLAPSE.type ) ) {
      return WhitespaceCollapse.COLLAPSE;
    }
    if ( this.type.equals( WhitespaceCollapse.PRESERVE.type ) ) {
      return WhitespaceCollapse.PRESERVE;
    }
    if ( this.type.equals( WhitespaceCollapse.DISCARD.type ) ) {
      return WhitespaceCollapse.DISCARD;
    }
    if ( this.type.equals( WhitespaceCollapse.PRESERVE_BREAKS.type ) ) {
      return WhitespaceCollapse.PRESERVE_BREAKS;
    }
    // unknown element alignment...
    throw new ObjectStreamResolveException();
  }

}
