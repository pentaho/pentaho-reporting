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

public class IncompatibleFeatureException extends InvalidReportStateException {
  private int minimumVersionNeeded;

  public IncompatibleFeatureException( final String message, final Throwable ex, final int minimumVersionNeeded ) {
    super( message, ex );
    this.minimumVersionNeeded = minimumVersionNeeded;
  }

  public IncompatibleFeatureException( final String message, final int minimumVersionNeeded ) {
    super( message );
    this.minimumVersionNeeded = minimumVersionNeeded;
  }

  public IncompatibleFeatureException( final int minimumVersionNeeded ) {
    this.minimumVersionNeeded = minimumVersionNeeded;
  }

  public int getMinimumVersionNeeded() {
    return minimumVersionNeeded;
  }
}
