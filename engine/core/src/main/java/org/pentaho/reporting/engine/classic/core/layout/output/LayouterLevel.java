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


package org.pentaho.reporting.engine.classic.core.layout.output;

import org.pentaho.reporting.engine.classic.core.ReportDefinition;

/**
 * Creation-Date: Jan 22, 2007, 12:20:04 PM
 *
 * @author Thomas Morgner
 */
public class LayouterLevel {
  private ReportDefinition reportDefinition;
  private int groupIndex;
  private LayoutExpressionRuntime runtime;
  private boolean inItemGroup;

  public LayouterLevel( final ReportDefinition reportDefinition, final int groupIndex,
      final LayoutExpressionRuntime runtime, final boolean inItemGroup ) {
    this.reportDefinition = reportDefinition;
    this.groupIndex = groupIndex;
    this.runtime = runtime;
    this.inItemGroup = inItemGroup;
  }

  public boolean isInItemGroup() {
    return inItemGroup;
  }

  public ReportDefinition getReportDefinition() {
    return reportDefinition;
  }

  public int getGroupIndex() {
    return groupIndex;
  }

  public LayoutExpressionRuntime getRuntime() {
    return runtime;
  }
}
