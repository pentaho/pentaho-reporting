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
 * Creation-Date: 24.11.2005, 17:08:01
 *
 * @author Thomas Morgner
 */
public class VerticalTextAlign implements Serializable {
  public static final VerticalTextAlign USE_SCRIPT = new VerticalTextAlign( "use-script" );
  public static final VerticalTextAlign BASELINE = new VerticalTextAlign( "baseline" );
  public static final VerticalTextAlign SUB = new VerticalTextAlign( "sub" );
  public static final VerticalTextAlign SUPER = new VerticalTextAlign( "super" );

  public static final VerticalTextAlign TOP = new VerticalTextAlign( "top" );
  public static final VerticalTextAlign TEXT_TOP = new VerticalTextAlign( "text-top" );
  public static final VerticalTextAlign CENTRAL = new VerticalTextAlign( "central" );
  public static final VerticalTextAlign MIDDLE = new VerticalTextAlign( "middle" );
  public static final VerticalTextAlign BOTTOM = new VerticalTextAlign( "bottom" );
  public static final VerticalTextAlign TEXT_BOTTOM = new VerticalTextAlign( "text-bottom" );
  private String id;

  private VerticalTextAlign( final String id ) {
    this.id = id;
  }

  /**
   * Replaces the automatically generated instance with one of the enumeration instances.
   *
   * @return the resolved element
   * @throws java.io.ObjectStreamException
   *           if the element could not be resolved.
   */
  protected Object readResolve() throws ObjectStreamException {
    if ( this.id.equals( VerticalTextAlign.USE_SCRIPT.id ) ) {
      return VerticalTextAlign.USE_SCRIPT;
    }
    if ( this.id.equals( VerticalTextAlign.BASELINE.id ) ) {
      return VerticalTextAlign.BASELINE;
    }
    if ( this.id.equals( VerticalTextAlign.SUPER.id ) ) {
      return VerticalTextAlign.SUPER;
    }
    if ( this.id.equals( VerticalTextAlign.SUB.id ) ) {
      return VerticalTextAlign.SUB;
    }
    if ( this.id.equals( VerticalTextAlign.TOP.id ) ) {
      return VerticalTextAlign.TOP;
    }
    if ( this.id.equals( VerticalTextAlign.TEXT_TOP.id ) ) {
      return VerticalTextAlign.TEXT_TOP;
    }
    if ( this.id.equals( VerticalTextAlign.BOTTOM.id ) ) {
      return VerticalTextAlign.BOTTOM;
    }
    if ( this.id.equals( VerticalTextAlign.TEXT_BOTTOM.id ) ) {
      return VerticalTextAlign.TEXT_BOTTOM;
    }
    if ( this.id.equals( VerticalTextAlign.CENTRAL.id ) ) {
      return VerticalTextAlign.CENTRAL;
    }
    if ( this.id.equals( VerticalTextAlign.MIDDLE.id ) ) {
      return VerticalTextAlign.MIDDLE;
    }
    // unknown element alignment...
    throw new ObjectStreamResolveException();
  }

  public static VerticalTextAlign valueOf( String id ) {
    if ( id == null ) {
      return null;
    }
    if ( id.equals( VerticalTextAlign.USE_SCRIPT.id ) ) {
      return VerticalTextAlign.USE_SCRIPT;
    }
    if ( id.equals( VerticalTextAlign.BASELINE.id ) ) {
      return VerticalTextAlign.BASELINE;
    }
    if ( id.equals( VerticalTextAlign.SUPER.id ) ) {
      return VerticalTextAlign.SUPER;
    }
    if ( id.equals( VerticalTextAlign.SUB.id ) ) {
      return VerticalTextAlign.SUB;
    }
    if ( id.equals( VerticalTextAlign.TOP.id ) ) {
      return VerticalTextAlign.TOP;
    }
    if ( id.equals( VerticalTextAlign.TEXT_TOP.id ) ) {
      return VerticalTextAlign.TEXT_TOP;
    }
    if ( id.equals( VerticalTextAlign.BOTTOM.id ) ) {
      return VerticalTextAlign.BOTTOM;
    }
    if ( id.equals( VerticalTextAlign.TEXT_BOTTOM.id ) ) {
      return VerticalTextAlign.TEXT_BOTTOM;
    }
    if ( id.equals( VerticalTextAlign.CENTRAL.id ) ) {
      return VerticalTextAlign.CENTRAL;
    }
    if ( id.equals( VerticalTextAlign.MIDDLE.id ) ) {
      return VerticalTextAlign.MIDDLE;
    }
    return null;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final VerticalTextAlign that = (VerticalTextAlign) o;

    if ( !id.equals( that.id ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    return id.hashCode();
  }

  /**
   * Returns a string representation of the object. In general, the <code>toString</code> method returns a string that
   * "textually represents" this object. The result should be a concise but informative representation that is easy for
   * a person to read. It is recommended that all subclasses override this method.
   * <p/>
   * The <code>toString</code> method for class <code>Object</code> returns a string consisting of the name of the class
   * of which the object is an instance, the at-sign character `<code>@</code>', and the unsigned hexadecimal
   * representation of the hash code of the object. In other words, this method returns a string equal to the value of:
   * <blockquote>
   * 
   * <pre>
   * getClass().getName() + '@' + Integer.toHexString( hashCode() )
   * </pre>
   * 
   * </blockquote>
   *
   * @return a string representation of the object.
   */
  public String toString() {
    return id;
  }
}
