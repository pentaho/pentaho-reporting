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

import org.pentaho.reporting.engine.classic.core.style.TextRotation;

public class TextRotationValueConverter implements ValueConverter {
  public TextRotationValueConverter() {
  }

  public String toAttributeValue( final Object o ) throws BeanException {
    if ( o instanceof TextRotation ) {
      return String.valueOf( o );
    } else {
      throw new BeanException( "Failed to convert object of type " + o.getClass() + ": Not a TextRotation." );
    }
  }

  public Object toPropertyValue( final String o ) throws BeanException {
    if ( o == null ) {
      return null;
    }

    if ( TextRotation.D_90.toString().equalsIgnoreCase( o ) ) {
      return TextRotation.D_90;
    }

    if ( TextRotation.D_270.toString().equalsIgnoreCase( o ) ) {
      return TextRotation.D_270;
    }
    throw new BeanException( "Invalid value specified for TextRotation" );
  }
}
