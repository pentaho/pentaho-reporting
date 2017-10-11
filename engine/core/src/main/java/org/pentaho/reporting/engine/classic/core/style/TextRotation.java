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

package org.pentaho.reporting.engine.classic.core.style;

import org.pentaho.reporting.engine.classic.core.util.ObjectStreamResolveException;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.text.MessageFormat;

public class TextRotation implements Serializable {
  public static final TextRotation D_90 = new TextRotation( "90" );
  public static final TextRotation D_270 = new TextRotation( "-90" );
  private static final String CSS =
    "transform: rotate({0}deg); -ms-transform: rotate({0}deg); -webkit-transform: rotate({0}deg); {1}";
  private static final String CSS_POS = "white-space: nowrap; {0}";

  private static final String CSS_90 = "transform-origin: right bottom;";
  private static final String CSS_270 = "transform-origin: left bottom;";


  private String type;

  private TextRotation( final String type ) {
    this.type = type;
  }

  public static TextRotation getInstance( final short degree ) {
    if ( degree > 0 ) {
      return D_90;
    } else if ( degree < 0 ) {
      return D_270;
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

    final TextRotation that = (TextRotation) o;

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

  public short getNumericValue() {
    if ( this.type.equals( TextRotation.D_90.type ) ) {
      return 90;
    }

    if ( this.type.equals( TextRotation.D_270.type ) ) {
      return -90;
    }

    return 0;
  }

  public String getCss() {
    return MessageFormat.format( CSS, -this.getNumericValue(),
      MessageFormat.format( CSS_POS, getNumericValue() > 0 ? CSS_90 : CSS_270 ) );
  }

  /**
   * Replaces the automatically generated instance with one of the enumeration instances.
   *
   * @return the resolved element
   * @throws ObjectStreamException if the element could not be resolved.
   * @noinspection UNUSED_SYMBOL
   */
  protected Object readResolve() throws ObjectStreamException {
    if ( this.type.equals( TextRotation.D_90.type ) ) {
      return TextRotation.D_90;
    }

    if ( this.type.equals( TextRotation.D_270.type ) ) {
      return TextRotation.D_270;
    }
    // unknown element alignment...
    throw new ObjectStreamResolveException();
  }

}
