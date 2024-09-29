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


package org.pentaho.reporting.engine.classic.extensions.modules.mailer;

import org.pentaho.reporting.engine.classic.core.parameters.ParameterContext;

public class StaticHeader implements MailHeader {
  private String name;
  private String value;

  public StaticHeader( final String name, final String value ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    if ( value == null ) {
      throw new NullPointerException();
    }
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public String getValue( final ParameterContext context ) {
    return value;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final StaticHeader header = (StaticHeader) o;

    if ( !name.equals( header.name ) ) {
      return false;
    }
    if ( !value.equals( header.value ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + value.hashCode();
    return result;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
