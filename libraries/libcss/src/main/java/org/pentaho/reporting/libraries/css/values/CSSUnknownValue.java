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
 * Creation-Date: 23.11.2005, 11:59:26
 *
 * @author Thomas Morgner
 */
public class CSSUnknownValue implements CSSValue {
  private String cssText;

  public CSSUnknownValue( final String cssText ) {
    this.cssText = cssText;
  }

  public String getCSSText() {
    return cssText;
  }

  public String toString() {
    return getCSSText();
  }

  /**
   * Determines if this instance of the object is equals to another Object
   *
   * @return <code>true</code> if the supplied object is equivalent to this object, <code>false</code> otherwise
   */
  public boolean equals( Object obj ) {
    if ( obj instanceof CSSUnknownValue ) {
      CSSUnknownValue that = (CSSUnknownValue) obj;
      return ObjectUtilities.equal( this.cssText, that.cssText );
    } else {
      return false;
    }
  }

  public CSSType getType() {
    return CSSGenericType.GENERIC_TYPE;
  }

}
