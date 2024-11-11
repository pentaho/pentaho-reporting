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
 * Creation-Date: 23.11.2005, 12:04:56
 *
 * @author Thomas Morgner
 */
public class CSSCounterFunction implements CSSValue {
  private String identifier;
  private String listStyle;
  private String separator;

  public CSSCounterFunction( final String identifier,
                             final String listStyle,
                             final String separator ) {
    this.identifier = identifier;
    this.listStyle = listStyle;
    this.separator = separator;
  }

  public String getIdentifier() {
    return identifier;
  }

  public String getListStyle() {
    return listStyle;
  }

  public String getSeparator() {
    return separator;
  }

  public String getCSSText() {
    return "counter(" + identifier + ", \"" + separator + "\", " + listStyle + ")";
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
    if ( obj instanceof CSSCounterFunction ) {
      CSSCounterFunction that = (CSSCounterFunction) obj;
      return ( ObjectUtilities.equal( this.identifier, that.identifier )
        && ObjectUtilities.equal( this.listStyle, that.listStyle )
        && ObjectUtilities.equal( this.separator, that.separator ) );
    } else {
      return false;
    }
  }

  public CSSType getType() {
    return CSSGenericType.GENERIC_TYPE;
  }
}
