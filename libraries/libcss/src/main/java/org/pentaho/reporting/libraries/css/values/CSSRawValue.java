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


package org.pentaho.reporting.libraries.css.values;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Creation-Date: 16.04.2006, 15:18:08
 *
 * @author Thomas Morgner
 */
public class CSSRawValue implements CSSValue {
  private Object value;

  public CSSRawValue( Object value ) {
    this.value = value;
  }

  public Object getValue() {
    return value;
  }

  public String getCSSText() {
    // this type has no representation in the outside world.
    return null;
  }

  /**
   * Determines if this instance of the object is equals to another Object
   *
   * @return <code>true</code> if the supplied object is equivalent to this object, <code>false</code> otherwise
   */
  public boolean equals( Object obj ) {
    if ( obj instanceof CSSRawValue ) {
      CSSRawValue that = (CSSRawValue) obj;
      return ( ObjectUtilities.equal( this.value, that.value ) );
    } else {
      return false;
    }
  }

  public CSSType getType() {
    return CSSGenericType.GENERIC_TYPE;
  }
}
