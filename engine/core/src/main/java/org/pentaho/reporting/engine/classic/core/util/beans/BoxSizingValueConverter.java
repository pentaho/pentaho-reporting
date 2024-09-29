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

import org.pentaho.reporting.engine.classic.core.style.BoxSizing;

/**
 * Creation-Date: 06.09.2007, 14:00:42
 *
 * @author Thomas Morgner
 */
public class BoxSizingValueConverter implements ValueConverter {
  public BoxSizingValueConverter() {
  }

  public String toAttributeValue( final Object o ) throws BeanException {
    if ( o == null ) {
      throw new NullPointerException();
    }
    if ( o instanceof BoxSizing ) {
      return String.valueOf( o );
    } else {
      throw new BeanException( "Failed to convert object of type " + o.getClass() + ": Not a BoxSizing." );
    }
  }

  public Object toPropertyValue( final String s ) throws BeanException {
    if ( s == null ) {
      throw new NullPointerException();
    }
    if ( BoxSizing.BORDER_BOX.toString().equals( s ) ) {
      return BoxSizing.BORDER_BOX;
    }
    if ( BoxSizing.CONTENT_BOX.toString().equals( s ) ) {
      return BoxSizing.CONTENT_BOX;
    }
    throw new BeanException( "Invalid value specified for BoxSizing" );
  }
}
