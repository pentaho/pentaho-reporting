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

package org.pentaho.reporting.engine.classic.core.util.beans;

import org.pentaho.reporting.engine.classic.core.style.BorderStyle;

/**
 * Creation-Date: 06.09.2007, 14:00:42
 *
 * @author Thomas Morgner
 */
public class BorderStyleValueConverter implements ValueConverter {
  public BorderStyleValueConverter() {
  }

  public String toAttributeValue( final Object o ) throws BeanException {
    if ( o == null ) {
      throw new NullPointerException();
    }
    if ( o instanceof BorderStyle ) {
      return String.valueOf( o );
    } else {
      throw new BeanException( "Failed to convert object of type " + o.getClass() + ": Not a BorderStyle." );
    }
  }

  public Object toPropertyValue( final String o ) throws BeanException {
    if ( o == null ) {
      throw new NullPointerException();
    }
    if ( "dashed".equalsIgnoreCase( o ) ) {
      return BorderStyle.DASHED;
    }
    if ( "dot-dash".equalsIgnoreCase( o ) ) {
      return BorderStyle.DOT_DASH;
    }
    if ( "dot-dot-dash".equalsIgnoreCase( o ) ) {
      return BorderStyle.DOT_DOT_DASH;
    }
    if ( "dotted".equalsIgnoreCase( o ) ) {
      return BorderStyle.DOTTED;
    }
    if ( "double".equalsIgnoreCase( o ) ) {
      return BorderStyle.DOUBLE;
    }
    if ( "groove".equalsIgnoreCase( o ) ) {
      return BorderStyle.GROOVE;
    }
    if ( "hidden".equalsIgnoreCase( o ) ) {
      return BorderStyle.HIDDEN;
    }
    if ( "inset".equalsIgnoreCase( o ) ) {
      return BorderStyle.INSET;
    }
    if ( "outset".equalsIgnoreCase( o ) ) {
      return BorderStyle.OUTSET;
    }
    if ( "none".equalsIgnoreCase( o ) ) {
      return BorderStyle.NONE;
    }
    if ( "ridge".equalsIgnoreCase( o ) ) {
      return BorderStyle.RIDGE;
    }
    if ( "solid".equalsIgnoreCase( o ) ) {
      return BorderStyle.SOLID;
    }
    if ( "wave".equalsIgnoreCase( o ) ) {
      return BorderStyle.WAVE;
    }
    throw new BeanException( "Invalid value specified for BorderStyle" );
  }
}
