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


package org.pentaho.reporting.libraries.formula;

import org.pentaho.reporting.libraries.base.util.StringUtils;

import java.util.Locale;
import java.util.MissingResourceException;

public class CustomErrorValue implements ErrorValue {
  private String errorMessage;
  private int errorCode;

  public CustomErrorValue( final String errorMessage ) {
    this.errorMessage = errorMessage;
    this.errorCode = -1;
  }

  public CustomErrorValue( final int errorCode, final String errorMessage ) {
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }

  public String getNamespace() {
    return "http://jfreereport.sourceforge.net/libformula/usererror";
  }

  public int getErrorCode() {
    return errorCode;
  }

  public String getErrorMessage( final Locale locale ) {
    if ( StringUtils.isEmpty( errorMessage ) ) {
      try {
        return new Messages( locale ).strictString( "ErrorValue." + errorCode );
      } catch ( MissingResourceException mre ) {
        return new Messages( locale ).formatMessage( "ErrorValue.Generic", new Integer( errorCode ) );
      }
    }

    return errorMessage;
  }

  public String toString() {
    return getErrorCode() + "-" + getErrorMessage( Locale.getDefault() );
  }
}
