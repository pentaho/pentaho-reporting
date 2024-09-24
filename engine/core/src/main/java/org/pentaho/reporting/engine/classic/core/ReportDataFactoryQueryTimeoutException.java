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

public class ReportDataFactoryQueryTimeoutException extends ReportDataFactoryException {

  /**
   * A report data factory exception is thrown whenever querying a datasource failed for timeout exception
   */
  public ReportDataFactoryQueryTimeoutException() {
    super( "Statement cancelled due to timeout or client request" );
  }

  public ReportDataFactoryQueryTimeoutException( String message ) {
    super( message );
  }

  /**
   *
   */
  private static final long serialVersionUID = -3461480070128356838L;

}
