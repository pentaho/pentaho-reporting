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

/**
 * A numeric constant indicating an value that must be resolved during the layouting process.
 *
 * @author Thomas Morgner
 */
public final class CSSAutoValue implements CSSValue {
  private static CSSAutoValue instance;

  public static synchronized CSSAutoValue getInstance() {
    if ( instance == null ) {
      instance = new CSSAutoValue();
    }
    return instance;
  }

  private CSSAutoValue() {
  }

  public String getCSSText() {
    return "auto";
  }


  public String toString() {
    return getCSSText();
  }

  public CSSType getType() {
    return CSSGenericType.GENERIC_TYPE;
  }
}
