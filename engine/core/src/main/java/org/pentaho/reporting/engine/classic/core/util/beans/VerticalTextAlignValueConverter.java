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
