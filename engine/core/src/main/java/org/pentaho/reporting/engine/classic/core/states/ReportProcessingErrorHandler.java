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

/**
 * Creation-Date: 04.07.2007, 13:59:42
 *
 * @author Thomas Morgner
 */
public interface ReportProcessingErrorHandler {
  public void handleError( Exception exception );

  public boolean isErrorOccured();

  public Exception[] getErrors();

  public void clearErrors();
}
