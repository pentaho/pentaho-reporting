/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.metadata;

public enum MaturityLevel implements Comparable<MaturityLevel> {
  Development( true ), Snapshot( true ), Community( false ), Limited( false ), Production( false );

  private boolean experimental;

  MaturityLevel( final boolean experimental ) {
    this.experimental = experimental;
  }

  public boolean isExperimental() {
    return experimental;
  }

  public boolean isMature( final MaturityLevel ml ) {
    return ml.ordinal() >= this.ordinal();
  }
}
