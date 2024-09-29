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


package org.pentaho.reporting.libraries.css.values;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.resourceloader.Resource;

public class CSSResourceValue implements CSSValue {
  private Resource value;
  private CSSValue parent;

  public CSSResourceValue( final Resource value ) {
    this.value = value;
  }

  public CSSResourceValue( final CSSValue parent, final Resource value ) {
    this.parent = parent;
    this.value = value;
  }

  public CSSValue getParent() {
    return parent;
  }

  public Resource getValue() {
    return value;
  }

  public CSSType getType() {
    return CSSGenericType.GENERIC_TYPE;
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
    if ( obj instanceof CSSResourceValue ) {
      CSSResourceValue that = (CSSResourceValue) obj;
      return ( ObjectUtilities.equal( this.parent, that.parent ) && ObjectUtilities.equal( this.value, that.value ) );
    } else {
      return false;
    }
  }
}
