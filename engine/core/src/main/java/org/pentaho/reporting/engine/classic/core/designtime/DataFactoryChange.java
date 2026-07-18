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



package org.pentaho.reporting.engine.classic.core.designtime;

import org.pentaho.reporting.engine.classic.core.DataFactory;

public class DataFactoryChange implements Change {
  private DataFactory oldValue;
  private DataFactory newValue;

  public DataFactoryChange( final DataFactory oldValue, final DataFactory newValue ) {
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  public DataFactory getOldValue() {
    return oldValue;
  }

  public DataFactory getNewValue() {
    return newValue;
  }
}
