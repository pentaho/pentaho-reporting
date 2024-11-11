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


package org.pentaho.reporting.libraries.docbundle.metadata;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

public class UserDefinedAttribute implements Serializable {
  private String name;
  private Object value;

  public UserDefinedAttribute( final String name, final String value ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    if ( value == null ) {
      throw new NullPointerException();
    }
    this.name = name;
    this.value = value;
  }

  public UserDefinedAttribute( final String name, final Date value ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    if ( value == null ) {
      throw new NullPointerException();
    }
    this.name = name;
    this.value = value;
  }

  public UserDefinedAttribute( final String name, final Time value ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    if ( value == null ) {
      throw new NullPointerException();
    }
    this.name = name;
    this.value = value;
  }

  public UserDefinedAttribute( final String name, final Boolean value ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    if ( value == null ) {
      throw new NullPointerException();
    }
    this.name = name;
    this.value = value;
  }

  public UserDefinedAttribute( final String name, final Number value ) {
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

  public void setName( final String name ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    this.name = name;
  }

  public Object getValue() {
    return value;
  }
}
