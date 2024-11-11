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

import org.pentaho.reporting.engine.classic.core.style.WhitespaceCollapse;

/**
 * Creation-Date: 06.09.2007, 14:00:42
 *
 * @author Thomas Morgner
 */
public class WhitespaceCollapseValueConverter implements ValueConverter {
  public WhitespaceCollapseValueConverter() {
  }

  public String toAttributeValue( final Object o ) throws BeanException {
    if ( o == null ) {
      throw new NullPointerException();
    }
    if ( o instanceof WhitespaceCollapse ) {
      return String.valueOf( o );
    }

    throw new BeanException( "Failed to convert object of type " + o.getClass() + ": Not a WhitespaceCollapse." );
  }

  public Object toPropertyValue( final String o ) throws BeanException {
    if ( o == null ) {
      throw new NullPointerException();
    }
    if ( "discard".equalsIgnoreCase( o ) ) {
      return WhitespaceCollapse.DISCARD;
    }
    if ( "collapse".equalsIgnoreCase( o ) ) {
      return WhitespaceCollapse.COLLAPSE;
    }
    if ( "preserve".equalsIgnoreCase( o ) ) {
      return WhitespaceCollapse.PRESERVE;
    }
    if ( "preserve-breaks".equalsIgnoreCase( o ) ) {
      return WhitespaceCollapse.PRESERVE_BREAKS;
    }
    throw new BeanException( "Invalid value encountered for VerticalTextAlign" );
  }
}
