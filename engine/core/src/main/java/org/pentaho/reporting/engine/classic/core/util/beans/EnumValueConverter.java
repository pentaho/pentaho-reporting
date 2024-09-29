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

public class EnumValueConverter implements ValueConverter {
  private Class enumClass;

  public EnumValueConverter( final Class enumClass ) {
    this.enumClass = enumClass;
  }

  public String toAttributeValue( final Object o ) throws BeanException {
    if ( o instanceof Enum == false ) {
      throw new BeanException();
    }
    final Enum e = (Enum) o;
    return e.name();
  }

  public Object toPropertyValue( final String s ) throws BeanException {
    try {
      return Enum.valueOf( enumClass, s );
    } catch ( Exception e ) {
      throw new BeanException( "Failed to convert enum from string " + s, e );
    }
  }
}
