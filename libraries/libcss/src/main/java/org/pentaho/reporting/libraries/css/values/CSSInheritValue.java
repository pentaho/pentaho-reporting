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
 * Creation-Date: 26.11.2005, 18:31:40
 *
 * @author Thomas Morgner
 */
public class CSSInheritValue implements CSSValue {
  private static CSSInheritValue instance;

  public static synchronized CSSInheritValue getInstance() {
    if ( instance == null ) {
      instance = new CSSInheritValue();
    }
    return instance;
  }

  private CSSInheritValue() {

  }

  public String getCSSText() {
    return "inherit";
  }

  public String toString() {
    return getCSSText();
  }

  public CSSType getType() {
    return CSSGenericType.GENERIC_TYPE;
  }
}
