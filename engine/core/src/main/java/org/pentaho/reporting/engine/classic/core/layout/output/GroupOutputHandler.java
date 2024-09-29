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
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;

public interface GroupOutputHandler {
  public void groupStarted( final DefaultOutputFunction outputFunction, ReportEvent event )
    throws ReportProcessingException;

  public void itemsStarted( final DefaultOutputFunction outputFunction, ReportEvent event )
    throws ReportProcessingException;

  public void itemsAdvanced( final DefaultOutputFunction outputFunction, ReportEvent event )
    throws ReportProcessingException;

  public void itemsFinished( final DefaultOutputFunction outputFunction, ReportEvent event )
    throws ReportProcessingException;

  public void groupFinished( final DefaultOutputFunction outputFunction, ReportEvent event )
    throws ReportProcessingException;

  public void groupBodyFinished( final DefaultOutputFunction outputFunction, ReportEvent event )
    throws ReportProcessingException;

  public void summaryRowStart( DefaultOutputFunction outputFunction, ReportEvent event )
    throws ReportProcessingException;

  public void summaryRowEnd( DefaultOutputFunction outputFunction, ReportEvent event ) throws ReportProcessingException;

  public void summaryRow( DefaultOutputFunction outputFunction, ReportEvent event ) throws ReportProcessingException;
}
