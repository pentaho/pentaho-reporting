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

import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressListener;
import org.pentaho.reporting.libraries.base.config.Configuration;

public interface ReportProcessor {
  public void addReportProgressListener( final ReportProgressListener l );

  public void removeReportProgressListener( final ReportProgressListener l );

  public boolean isHandleInterruptedState();

  public void setHandleInterruptedState( final boolean handleInterruptedState );

  public void processReport() throws ReportProcessingException;

  public void close();

  public PageState processPage( final PageState state, final boolean performOutput ) throws ReportProcessingException;

  public Configuration getConfiguration();
}
