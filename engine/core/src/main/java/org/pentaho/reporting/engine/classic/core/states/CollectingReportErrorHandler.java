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

package org.pentaho.reporting.engine.classic.core.states;

import java.util.ArrayList;

/**
 * Creation-Date: 04.07.2007, 14:01:43
 *
 * @author Thomas Morgner
 */
public class CollectingReportErrorHandler implements ReportProcessingErrorHandler {
  private ArrayList errorList;

  public CollectingReportErrorHandler() {
  }

  public void handleError( final Exception exception ) {
    if ( errorList == null ) {
      errorList = new ArrayList();
    }
    errorList.add( exception );
  }

  public boolean isErrorOccured() {
    if ( errorList == null ) {
      return false;
    }
    return errorList.isEmpty() == false;
  }

  public Exception[] getErrors() {
    return (Exception[]) errorList.toArray( new Exception[errorList.size()] );
  }

  public void clearErrors() {
    if ( errorList == null ) {
      return;
    }
    errorList.clear();
  }
}
