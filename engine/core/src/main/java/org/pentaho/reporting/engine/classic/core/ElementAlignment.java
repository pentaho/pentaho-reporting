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

package org.pentaho.reporting.engine.classic.core;

import org.pentaho.reporting.engine.classic.core.util.ObjectStreamResolveException;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Represents the alignment of an element.
 *
 * @author Thomas Morgner
 */
public final class ElementAlignment implements Serializable {
  /**
   * A constant for left alignment.
   */
  public static final ElementAlignment LEFT = new ElementAlignment( "LEFT" );

  /**
   * A constant for center alignment (horizontal).
   */
  public static final ElementAlignment CENTER = new ElementAlignment( "CENTER" );

  /**
   * A constant for right alignment.
   */
  public static final ElementAlignment RIGHT = new ElementAlignment( "RIGHT" );
  public static final ElementAlignment JUSTIFY = new ElementAlignment( "JUSTIFY" );

  /**
   * A constant for top alignment.
   */
  public static final ElementAlignment TOP = new ElementAlignment( "TOP" );

  /**
   * A constant for middle alignment (vertical).
   */
  public static final ElementAlignment MIDDLE = new ElementAlignment( "MIDDLE" );

  /**
   * A constant for bottom alignment.
   */
  public static final ElementAlignment BOTTOM = new ElementAlignment( "BOTTOM" );

  /**
   * The alignment name.
   */
  private final String myName; // for debug only
  /**
   * A cached hashcode.
   */
  private final int hashCode;

  /**
   * Creates a new alignment object. Since this constructor is private, you cannot create new alignment objects, you can
   * only use the predefined constants.
   *
   * @param name
   *          the alignment name.
   */
  private ElementAlignment( final String name ) {
    myName = name;
    hashCode = myName.hashCode();
  }

  /**
   * Returns the alignment name.
   *
   * @return the alignment name.
   */
  public String toString() {
    return myName;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( !( o instanceof ElementAlignment ) ) {
      return false;
    }

    final ElementAlignment alignment = (ElementAlignment) o;
    if ( !myName.equals( alignment.myName ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    return hashCode;
  }

  /**
   * Replaces the automatically generated instance with one of the enumeration instances.
   *
   * @return the resolved element
   * @throws ObjectStreamException
   *           if the element could not be resolved.
   */
  private Object readResolve() throws ObjectStreamException {
    if ( this.myName.equals( ElementAlignment.LEFT.myName ) ) {
      return ElementAlignment.LEFT;
    }
    if ( this.myName.equals( ElementAlignment.RIGHT.myName ) ) {
      return ElementAlignment.RIGHT;
    }
    if ( this.myName.equals( ElementAlignment.CENTER.myName ) ) {
      return ElementAlignment.CENTER;
    }
    if ( this.myName.equals( ElementAlignment.TOP.myName ) ) {
      return ElementAlignment.TOP;
    }
    if ( this.myName.equals( ElementAlignment.BOTTOM.myName ) ) {
      return ElementAlignment.BOTTOM;
    }
    if ( this.myName.equals( ElementAlignment.MIDDLE.myName ) ) {
      return ElementAlignment.MIDDLE;
    }
    if ( this.myName.equals( ElementAlignment.JUSTIFY.myName ) ) {
      return ElementAlignment.JUSTIFY;
    }
    // unknown element alignment...
    throw new ObjectStreamResolveException();
  }
}
