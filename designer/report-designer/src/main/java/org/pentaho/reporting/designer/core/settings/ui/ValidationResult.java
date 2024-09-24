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

package org.pentaho.reporting.designer.core.settings.ui;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * User: Martin Date: 01.03.2006 Time: 18:13:42
 */
public class ValidationResult {
  private ArrayList<ValidationMessage> validationMessages;

  public ValidationResult() {
    validationMessages = new ArrayList<ValidationMessage>();
  }

  public void addValidationMessage( final ValidationMessage validationMessage ) {
    if ( validationMessage == null ) {
      throw new NullPointerException();
    }
    validationMessages.add( validationMessage );
  }

  public ValidationMessage[] getValidationMessages( final ValidationMessage.Severity[] severities ) {
    final ValidationMessage.Severity[] sortedSeverities = severities.clone();
    Arrays.sort( sortedSeverities );

    final ArrayList<ValidationMessage> filteredValidationMessages = new ArrayList<ValidationMessage>();
    for ( final ValidationMessage.Severity severity : severities ) {
      for ( final ValidationMessage validationMessage : validationMessages ) {
        if ( validationMessage.getSeverity().equals( severity ) ) {
          filteredValidationMessages.add( validationMessage );
        }
      }
    }

    return filteredValidationMessages.toArray( new ValidationMessage[ filteredValidationMessages.size() ] );
  }
}
