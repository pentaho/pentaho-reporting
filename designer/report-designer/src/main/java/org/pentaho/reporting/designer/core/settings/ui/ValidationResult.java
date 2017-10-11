/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
