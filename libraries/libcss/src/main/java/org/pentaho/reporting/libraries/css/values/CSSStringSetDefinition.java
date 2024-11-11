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
 * Creation-Date: 01.12.2005, 18:21:53
 *
 * @author Thomas Morgner
 */
public class CSSStringSetDefinition implements CSSValue {
  private String identifier;
  private CSSValue value;

  public CSSStringSetDefinition( final String identifier, final CSSValue value ) {
    this.identifier = identifier;
    this.value = value;
  }

  public String getIdentifier() {
    return identifier;
  }

  public CSSValue getValue() {
    return value;
  }

  public String getCSSText() {
    return identifier + " " + value;
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
    if ( obj instanceof CSSStringSetDefinition ) {
      CSSStringSetDefinition that = (CSSStringSetDefinition) obj;
      return ( ObjectUtilities.equal( this.identifier, that.identifier ) && ObjectUtilities
        .equal( this.value, that.value ) );
    } else {
      return false;
    }
  }

  public CSSType getType() {
    return CSSGenericType.GENERIC_TYPE;
  }
}
