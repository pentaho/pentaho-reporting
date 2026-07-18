/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/

package org.pentaho.reporting.engine.classic.core.modules.misc.connections;

public class NonExistingDatasourceException extends DatasourceMgmtServiceException {
  public NonExistingDatasourceException() {
  }

  public NonExistingDatasourceException( final String message ) {
    super( message );
  }

  public NonExistingDatasourceException( final String message, final Throwable cause ) {
    super( message, cause );
  }

  public NonExistingDatasourceException( final Throwable cause ) {
    super( cause );
  }
}
