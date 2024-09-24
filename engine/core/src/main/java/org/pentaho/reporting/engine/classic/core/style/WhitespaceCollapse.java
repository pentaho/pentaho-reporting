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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

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
