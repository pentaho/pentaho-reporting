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


package org.pentaho.reporting.libraries.formula.function;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Creation-Date: 05.11.2006, 14:31:22
 *
 * @author Thomas Morgner
 */
public class AbstractFunctionCategory implements FunctionCategory {
  private String bundleName;

  protected AbstractFunctionCategory( final String bundleName ) {
    this.bundleName = bundleName;
  }

  protected ResourceBundle getBundle( final Locale locale ) {
    return ResourceBundle.getBundle( bundleName, locale );
  }

  public String getDisplayName( final Locale locale ) {
    return getBundle( locale ).getString( "display-name" );
  }

  public String getDescription( final Locale locale ) {
    return getBundle( locale ).getString( "description" );
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    return !( o == null || getClass() != o.getClass() );
  }

  public int hashCode() {
    return getClass().hashCode();
  }
}
