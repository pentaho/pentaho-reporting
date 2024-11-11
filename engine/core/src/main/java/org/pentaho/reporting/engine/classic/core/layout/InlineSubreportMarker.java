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


package org.pentaho.reporting.engine.classic.core.layout;

import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.states.process.SubReportProcessType;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public class InlineSubreportMarker {
  private SubReport subreport;
  private InstanceID insertationPointId;
  private SubReportProcessType processType;

  public InlineSubreportMarker( final SubReport subreport, final InstanceID insertationPointId,
      final SubReportProcessType processType ) {
    if ( subreport == null ) {
      throw new NullPointerException();
    }
    if ( processType == null ) {
      throw new NullPointerException();
    }
    this.subreport = subreport;
    this.insertationPointId = insertationPointId;
    this.processType = processType;
  }

  public SubReport getSubreport() {
    return subreport;
  }

  public InstanceID getInsertationPointId() {
    return insertationPointId;
  }

  public SubReportProcessType getProcessType() {
    return processType;
  }
}
