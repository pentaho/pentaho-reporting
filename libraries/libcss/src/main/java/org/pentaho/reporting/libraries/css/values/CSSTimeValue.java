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
 * Creation-Date: 23.11.2005, 11:44:40
 *
 * @author Thomas Morgner
 */
public class CSSTimeValue extends CSSNumericValue {
  public CSSTimeValue( final CSSTimeType type, final double value ) {
    super( type, value );
  }

  public boolean equals( Object obj ) {
    if ( obj instanceof CSSTimeValue == false ) {
      return false;
    }
    return super.equals( obj );
  }
}
