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



package org.pentaho.reporting.engine.classic.extensions.drilldown;

public class ParameterEntry {
  private String parameterName;
  private Object parameterValue;

  public ParameterEntry( final String parameterName, final Object parameterValue ) {
    this.parameterName = parameterName;
    this.parameterValue = parameterValue;
  }

  public String getParameterName() {
    return parameterName;
  }

  public Object getParameterValue() {
    return parameterValue;
  }
}
