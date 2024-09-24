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
