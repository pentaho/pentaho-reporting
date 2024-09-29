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


package org.pentaho.reporting.engine.classic.core;

import org.pentaho.reporting.engine.classic.core.parameters.ValidationResult;

public class ReportParameterValidationException extends ReportProcessingException {
  private ValidationResult validationResult;

  /**
   * Creates an exception.
   *
   * @param message
   *          the exception message.
   * @param validationResult
   */
  public ReportParameterValidationException( final String message, final ValidationResult validationResult ) {
    super( message );
    if ( validationResult == null ) {
      throw new NullPointerException();
    }
    this.validationResult = validationResult;
  }

  public ValidationResult getValidationResult() {
    return validationResult;
  }
}
