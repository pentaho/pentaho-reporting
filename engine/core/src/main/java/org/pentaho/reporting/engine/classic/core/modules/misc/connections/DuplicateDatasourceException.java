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
package org.pentaho.reporting.engine.classic.core.modules.misc.connections;

public class DuplicateDatasourceException extends DatasourceMgmtServiceException {
  public DuplicateDatasourceException() {
  }

  public DuplicateDatasourceException( String msg ) {
    super( msg );
  }

  /**
   * @param message
   * @param reas
   */
  public DuplicateDatasourceException( String message, Throwable reas ) {
    super( message, reas );
  }

  /**
   * @param reas
   */
  public DuplicateDatasourceException( Throwable reas ) {
    super( reas );
  }
}
