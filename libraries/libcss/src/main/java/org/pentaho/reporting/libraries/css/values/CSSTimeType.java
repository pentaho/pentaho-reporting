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

/**
 * Creation-Date: 23.11.2005, 11:45:22
 *
 * @author Thomas Morgner
 */
public class CSSTimeType extends CSSNumericType {
  public static final CSSTimeType SECONDS = new CSSTimeType( "s" );
  public static final CSSTimeType MILLISECONDS = new CSSTimeType( "ms" );

  private CSSTimeType( String name ) {
    super( name, false, true );
  }

  public boolean equals( Object obj ) {
    return ( obj instanceof CSSTimeType && super.equals( obj ) );
  }

  public int hashCode() {
    return super.hashCode();
  }
}
