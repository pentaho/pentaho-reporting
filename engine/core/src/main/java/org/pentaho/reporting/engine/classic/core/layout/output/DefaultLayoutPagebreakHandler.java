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


package org.pentaho.reporting.engine.classic.core.layout.output;

import org.pentaho.reporting.engine.classic.core.states.ReportState;

/**
 * Creation-Date: 22.04.2007, 13:47:41
 *
 * @author Thomas Morgner
 */
public class DefaultLayoutPagebreakHandler implements LayoutPagebreakHandler, Cloneable {
  private ReportState reportState;

  public DefaultLayoutPagebreakHandler( final ReportState reportState ) {
    this.reportState = reportState;
  }

  public DefaultLayoutPagebreakHandler() {
  }

  public ReportState getReportState() {
    return reportState;
  }

  public void setReportState( final ReportState reportState ) {
    this.reportState = reportState;
  }

  public void pageFinished() {
    if ( reportState == null ) {
      throw new IllegalStateException(
          "A Report-State must be given. If you dont have a report state, then you're doing something wrong." );
    }
    reportState.firePageFinishedEvent( false );
  }

  public void pageStarted() {
    if ( reportState == null ) {
      throw new IllegalStateException(
          "A Report-State must be given. If you dont have a report state, then you're doing something wrong." );
    }
    reportState.firePageStartedEvent( reportState.getEventCode() );
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
