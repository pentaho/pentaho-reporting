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
