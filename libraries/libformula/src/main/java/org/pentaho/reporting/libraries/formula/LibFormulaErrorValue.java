/*
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula;

import java.util.Locale;
import java.util.MissingResourceException;

public class LibFormulaErrorValue implements ErrorValue {
  public static final int ERROR_REFERENCE_NOT_RESOLVABLE = 499;

  /**
   * A parse error
   */
  public static final int ERROR_INVALID_CHARACTER = 501;
  /**
   * Function name is invalid error code
   */
  public static final int ERROR_INVALID_FUNCTION = 505;
  /**
   * Function name is invalid error
   */
  public static final LibFormulaErrorValue ERROR_INVALID_FUNCTION_VALUE =
    new LibFormulaErrorValue( ERROR_INVALID_FUNCTION );
  /**
   * Parameter types are invalid error code
   */
  public static final int ERROR_INVALID_ARGUMENT = 502;
  /**
   * Parameter types are invalid error
   */
  public static final LibFormulaErrorValue ERROR_INVALID_ARGUMENT_VALUE =
    new LibFormulaErrorValue( ERROR_INVALID_ARGUMENT );
  /**
   * Parameter types are invalid error code
   */
  public static final int ERROR_INVALID_AUTO_ARGUMENT = 666;
  /**
   * Parameter types are invalid error
   */
  public static final LibFormulaErrorValue ERROR_INVALID_AUTO_ARGUMENT_VALUE =
    new LibFormulaErrorValue( ERROR_INVALID_AUTO_ARGUMENT );

  public static final int ERROR_ILLEGAL_ARRAY = 667;

  public static final LibFormulaErrorValue ERROR_ILLEGAL_ARRAY_VALUE = new LibFormulaErrorValue( ERROR_ILLEGAL_ARRAY );
  /**
   * Number arithmetic error code
   */
  public static final int ERROR_ARITHMETIC = 503;
  /**
   * Number arithmetic error
   */
  public static final LibFormulaErrorValue ERROR_ARITHMETIC_VALUE = new LibFormulaErrorValue( ERROR_ARITHMETIC );
  /**
   * Invalid number of arguments error code
   */
  public static final int ERROR_ARGUMENTS = 1;
  /**
   * Invalid number of arguments error
   */
  public static final LibFormulaErrorValue ERROR_ARGUMENTS_VALUE = new LibFormulaErrorValue( ERROR_ARGUMENTS );
  /**
   * Occurence not found error code
   */
  public static final int ERROR_NOT_FOUND = 504;
  /**
   * Occurence not found error
   */
  public static final LibFormulaErrorValue ERROR_NOT_FOUND_VALUE = new LibFormulaErrorValue( ERROR_NOT_FOUND );
  /**
   * NA error code
   */
  public static final int ERROR_NA = 522;
  /**
   * NA error
   */
  public static final LibFormulaErrorValue ERROR_NA_VALUE = new LibFormulaErrorValue( ERROR_NA );
  /**
   * Unexpected error code
   */
  public static final int ERROR_UNEXPECTED = 0;
  /**
   * Unexpected error
   */
  public static final LibFormulaErrorValue ERROR_UNEXPECTED_VALUE = new LibFormulaErrorValue( ERROR_UNEXPECTED );

  public static final int ERROR_MISSING_VARIABLE = 511;
  public static final ErrorValue ERROR_MISSING_ARGUMENT_VALUE = new LibFormulaErrorValue( ERROR_MISSING_VARIABLE );

  private int errorCode;
  private static final long serialVersionUID = 5945536244711597636L;

  public LibFormulaErrorValue( final int errorCode ) {
    this.errorCode = errorCode;
  }

  public String getNamespace() {
    return "http://jfreereport.sourceforge.net/libformula";
  }

  public int getErrorCode() {
    return errorCode;
  }

  public String getErrorMessage( final Locale locale ) {
    try {
      return new Messages( locale ).strictString( "ErrorValue." + errorCode );
    } catch ( MissingResourceException mre ) {
      return new Messages( locale ).formatMessage( "ErrorValue.Generic", new Integer( errorCode ) );
    }
  }


  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( !( o instanceof LibFormulaErrorValue ) ) {
      return false;
    }

    final LibFormulaErrorValue that = (LibFormulaErrorValue) o;

    if ( errorCode != that.errorCode ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    return errorCode;
  }

  public String toString() {
    return "LibFormulaErrorValue{" +
      "errorCode=" + errorCode +
      ", errorMessage=" + getErrorMessage( Locale.getDefault() ) +
      '}';
  }
}
