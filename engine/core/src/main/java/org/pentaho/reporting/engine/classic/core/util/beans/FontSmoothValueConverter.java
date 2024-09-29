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

import org.pentaho.reporting.engine.classic.core.style.FontSmooth;

/**
 * Creation-Date: 06.09.2007, 14:00:42
 *
 * @author Thomas Morgner
 */
public class FontSmoothValueConverter implements ValueConverter {
  public FontSmoothValueConverter() {
  }

  public String toAttributeValue( final Object o ) throws BeanException {
    if ( o == null ) {
      throw new NullPointerException();
    }
    if ( o instanceof FontSmooth ) {
      return String.valueOf( o );
    } else {
      throw new BeanException( "Invalid value specified for FontSmooth" );
    }

  }

  public Object toPropertyValue( final String o ) throws BeanException {
    if ( o == null ) {
      throw new NullPointerException();
    }
    if ( FontSmooth.ALWAYS.toString().equalsIgnoreCase( o ) ) {
      return FontSmooth.ALWAYS;
    }
    if ( FontSmooth.NEVER.toString().equalsIgnoreCase( o ) ) {
      return FontSmooth.NEVER;
    }
    if ( FontSmooth.AUTO.toString().equalsIgnoreCase( o ) ) {
      return FontSmooth.AUTO;
    }
    throw new BeanException( "Invalid value specified for FontSmooth" );
  }
}
