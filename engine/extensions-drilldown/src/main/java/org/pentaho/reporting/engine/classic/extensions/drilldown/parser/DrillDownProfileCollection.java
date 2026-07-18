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



package org.pentaho.reporting.engine.classic.extensions.drilldown.parser;

import org.pentaho.reporting.engine.classic.extensions.drilldown.DrillDownProfile;

import java.io.Serializable;

public class DrillDownProfileCollection implements Serializable {
  private DrillDownProfile[] data;

  public DrillDownProfileCollection( final DrillDownProfile[] data ) {
    this.data = data.clone();
  }

  public DrillDownProfile[] getData() {
    return data.clone();
  }
}
