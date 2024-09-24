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

package org.pentaho.reporting.libraries.resourceloader;

import java.io.Serializable;

public class ParameterKey implements Serializable {
  private String name;
  private transient int hashKey;
  private static final long serialVersionUID = 4574332935778105499L;

  /**
   * Constructor is package-protected so that no third-party can create subclasses.
   *
   * @param name
   */
  ParameterKey( final String name ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    this.name = name;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final ParameterKey that = (ParameterKey) o;

    if ( !name.equals( that.name ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    if ( hashKey == 0 ) {
      hashKey = name.hashCode();
    }
    return hashKey;
  }

  public String getName() {
    return name;
  }

  public String toString() {
    return getClass().getName() + "{name=" + getName() + "}";
  }
}
