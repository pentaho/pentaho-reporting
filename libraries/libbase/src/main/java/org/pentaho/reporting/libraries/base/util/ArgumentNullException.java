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

package org.pentaho.reporting.libraries.base.util;

public class ArgumentNullException extends IllegalArgumentException {
  private String field;

  public ArgumentNullException( final String field ) {
    super( String.format( "Argument '%s' is <null>", field ) );  // NON-NLS
    this.field = field;
  }

  public String getField() {
    return field;
  }

  public static void validate( final String field, final Object o ) {
    if ( o == null ) {
      throw new ArgumentNullException( field );
    }
  }
}
