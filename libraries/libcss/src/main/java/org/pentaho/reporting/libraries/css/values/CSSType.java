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
 * Creation-Date: 23.11.2005, 11:51:52
 *
 * @author Thomas Morgner
 */
public abstract class CSSType {
  private String type;

  protected CSSType( final String type ) {
    if ( type == null ) {
      throw new NullPointerException();
    }
    this.type = type;
  }

  public String getType() {
    return type;
  }


  public String toString() {
    return "CSSType{" +
      "type='" + type + "'" +
      "}";
  }

  /**
   * Determines if this instance of the object is equals to another Object
   *
   * @return <code>true</code> if the supplied object is equivalent to this object, <code>false</code> otherwise
   */
  public boolean equals( Object obj ) {
    if ( obj instanceof CSSType ) {
      CSSType that = (CSSType) obj;
      return ObjectUtilities.equal( this.type, that.type );
    } else {
      return false;
    }
  }

  public int hashCode() {
    return type.hashCode();
  }
}
