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

import org.pentaho.reporting.engine.classic.core.style.VerticalTextAlign;

/**
 * Creation-Date: 06.09.2007, 14:00:42
 *
 * @author Thomas Morgner
 */
public class VerticalTextAlignValueConverter implements ValueConverter {
  public VerticalTextAlignValueConverter() {
  }

  public String toAttributeValue( final Object o ) throws BeanException {
    if ( o == null ) {
      throw new NullPointerException();
    }
    if ( o instanceof VerticalTextAlign ) {
      return String.valueOf( o );
    }
    throw new BeanException( "Failed to convert object of type " + o.getClass() + ": Not a VerticalTextAlign." );
  }

  public Object toPropertyValue( final String o ) throws BeanException {
    if ( o == null ) {
      throw new NullPointerException();
    }
    if ( "use-script".equalsIgnoreCase( o ) ) {
      return VerticalTextAlign.USE_SCRIPT;
    }
    if ( "text-bottom".equalsIgnoreCase( o ) ) {
      return VerticalTextAlign.TEXT_BOTTOM;
    }
    if ( "bottom".equalsIgnoreCase( o ) ) {
      return VerticalTextAlign.BOTTOM;
    }
    if ( "text-top".equalsIgnoreCase( o ) ) {
      return VerticalTextAlign.TEXT_TOP;
    }
    if ( "top".equalsIgnoreCase( o ) ) {
      return VerticalTextAlign.TOP;
    }
    if ( "central".equalsIgnoreCase( o ) ) {
      return VerticalTextAlign.CENTRAL;
    }
    if ( "middle".equalsIgnoreCase( o ) ) {
      return VerticalTextAlign.MIDDLE;
    }

    if ( "sub".equalsIgnoreCase( o ) ) {
      return VerticalTextAlign.SUB;
    }
    if ( "super".equalsIgnoreCase( o ) ) {
      return VerticalTextAlign.SUPER;
    }
    if ( "baseline".equalsIgnoreCase( o ) ) {
      return VerticalTextAlign.BASELINE;
    }
    throw new BeanException( "Invalid value encountered for VerticalTextAlign" );
  }
}
