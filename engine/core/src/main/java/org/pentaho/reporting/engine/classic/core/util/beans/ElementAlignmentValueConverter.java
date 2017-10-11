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

import org.pentaho.reporting.engine.classic.core.ElementAlignment;

/**
 * Creation-Date: 06.09.2007, 14:00:42
 *
 * @author Thomas Morgner
 */
public class ElementAlignmentValueConverter implements ValueConverter {
  public ElementAlignmentValueConverter() {
  }

  public String toAttributeValue( final Object o ) throws BeanException {
    if ( o == null ) {
      throw new NullPointerException();
    }
    if ( o instanceof ElementAlignment ) {
      return String.valueOf( o ).toLowerCase();
    } else {
      throw new BeanException( "Failed to convert object of type " + o.getClass() + ": Not a ElementAlignment." );
    }
  }

  public Object toPropertyValue( final String o ) throws BeanException {
    if ( o == null ) {
      throw new NullPointerException();
    }
    if ( "left".equalsIgnoreCase( o ) ) {
      return ElementAlignment.LEFT;
    }
    if ( "right".equalsIgnoreCase( o ) ) {
      return ElementAlignment.RIGHT;
    }
    if ( "justify".equalsIgnoreCase( o ) ) {
      return ElementAlignment.JUSTIFY;
    }
    if ( "center".equalsIgnoreCase( o ) ) {
      return ElementAlignment.CENTER;
    }
    if ( "top".equalsIgnoreCase( o ) ) {
      return ElementAlignment.TOP;
    }
    if ( "middle".equalsIgnoreCase( o ) ) {
      return ElementAlignment.MIDDLE;
    }
    if ( "bottom".equalsIgnoreCase( o ) ) {
      return ElementAlignment.BOTTOM;
    }
    throw new BeanException( "Invalid value specified for ElementAlignment" );
  }
}
