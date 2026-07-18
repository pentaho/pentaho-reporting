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



package org.pentaho.reporting.engine.classic.core.event.async;

/**
 * Execution status.
 * Final statuses must not change according to the business logic.
 */
public enum AsyncExecutionStatus {

  QUEUED( Boolean.FALSE ),
  WORKING( Boolean.FALSE ),
  CONTENT_AVAILABLE( Boolean.FALSE ),
  //Can be overriden by SCHEDULED status
  FINISHED( Boolean.FALSE ),
  FAILED( Boolean.TRUE ),
  CANCELED( Boolean.TRUE ),
  PRE_SCHEDULED( Boolean.FALSE ),
  SCHEDULED( Boolean.TRUE );

  private final boolean isFinal;

  public boolean isFinal() {
    return isFinal;
  }

  AsyncExecutionStatus( final boolean isFinal ) {
    this.isFinal = isFinal;
  }


}
