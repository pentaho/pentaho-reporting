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


package org.pentaho.reporting.designer.core.inspections;

import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;

public class NoOpInspectionRunner implements InspectionRunner {
  public static final InspectionRunner INSTANCE = new NoOpInspectionRunner();

  protected NoOpInspectionRunner() {
  }

  public void startTimer() {

  }

  public void dispose() {

  }

  public void addInspectionListener( final InspectionResultListener listener ) {

  }

  public void removeInspectionListener( final InspectionResultListener listener ) {

  }

  public void nodeChanged( final ReportModelEvent event ) {

  }
}
