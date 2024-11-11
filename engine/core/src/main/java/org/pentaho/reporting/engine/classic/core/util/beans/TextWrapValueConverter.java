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

import org.pentaho.reporting.engine.classic.core.style.TextWrap;

public class TextWrapValueConverter implements ValueConverter {
  public TextWrapValueConverter() {
  }

  public String toAttributeValue( final Object o ) throws BeanException {
    if ( o instanceof TextWrap ) {
      return String.valueOf( o );
    } else {
      throw new BeanException( "Failed to convert object of type " + o.getClass() + ": Not a TextWrap." );
    }
  }

  public Object toPropertyValue( final String o ) throws BeanException {
    if ( o == null ) {
      throw new NullPointerException();
    }

    if ( TextWrap.NONE.toString().equalsIgnoreCase( o ) ) {
      return TextWrap.NONE;
    }
    if ( TextWrap.WRAP.toString().equalsIgnoreCase( o ) ) {
      return TextWrap.WRAP;
    }
    throw new BeanException( "Invalid value specified for TextWrap" );
  }
}
