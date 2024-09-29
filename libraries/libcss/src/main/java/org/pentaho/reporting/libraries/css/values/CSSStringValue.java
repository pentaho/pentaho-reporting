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

/**
 * Creation-Date: 23.11.2005, 11:50:28
 *
 * @author Thomas Morgner
 */
public class CSSStringValue implements CSSValue {
  private CSSStringType type;
  private String value;

  public CSSStringValue( final CSSStringType type, final String value ) {
    this.type = type;
    this.value = value;
  }

  public CSSType getType() {
    return type;
  }

  public String getValue() {
    return value;
  }

  public String toString() {
    return getCSSText();
  }

  public boolean equals( final Object obj ) {
    if ( obj instanceof CSSStringValue ) {
      CSSStringValue that = (CSSStringValue) obj;
      return ( ObjectUtilities.equal( this.type, that.type ) && ObjectUtilities.equal( this.value, that.value ) );
    }
    return false;
  }

  public String getCSSText() {
    if ( type == CSSStringType.URI ) {
      return "uri(" + value + ")";
    } else {
      return "\"" + value + "\"";
    }
  }
}
