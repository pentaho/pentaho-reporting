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
 * Creation-Date: 23.11.2005, 11:45:22
 *
 * @author Thomas Morgner
 */
public class CSSStringType extends CSSType {
  public static final CSSStringType STRING = new CSSStringType( "STRING" );
  public static final CSSStringType URI = new CSSStringType( "URI" );

  private CSSStringType( String name ) {
    super( name );
  }

  public boolean equals( Object obj ) {
    return ( obj instanceof CSSStringType && super.equals( obj ) );
  }
}
