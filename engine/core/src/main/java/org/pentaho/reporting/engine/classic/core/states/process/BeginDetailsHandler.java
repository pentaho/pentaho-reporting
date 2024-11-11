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


package org.pentaho.reporting.engine.classic.core.states.process;

import org.pentaho.reporting.engine.classic.core.NoDataBand;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.NoDataBandType;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;

/**
 * Creation-Date: 03.07.2007, 13:57:49
 *
 * @author Thomas Morgner
 */
public class BeginDetailsHandler implements AdvanceHandler {
  public static final BeginDetailsHandler HANDLER = new BeginDetailsHandler();

  private BeginDetailsHandler() {
  }

  public int getEventCode() {
    return ReportEvent.ITEMS_STARTED;
  }

  public ProcessState advance( final ProcessState state ) throws ReportProcessingException {
    final ProcessState next = state.deriveForAdvance();
    // if there is no data in the report's data source, this now prints the No-Data-Band.
    next.fireReportEvent();

    final OutputProcessorMetaData outputProcessorMetaData =
        next.getFlowController().getReportContext().getOutputProcessorMetaData();
    if ( outputProcessorMetaData.isFeatureSupported( OutputProcessorFeature.DESIGNTIME ) || next.getNumberOfRows() == 0 ) {
      final NoDataBand childs = next.getReport().getNoDataBand();
      if ( childs != null ) {
        return InlineSubreportProcessor.processInline( next, childs );
      }
    }
    return next;
  }

  public ProcessState commit( final ProcessState next ) throws ReportProcessingException {
    next.setInItemGroup( true );
    final int numberOfRows = next.getNumberOfRows();
    if ( numberOfRows > 0 ) {
      next.setAdvanceHandler( ProcessDetailsHandler.HANDLER );
      return next;
    }

    next.setAdvanceHandler( EndDetailsHandler.HANDLER );

    final ReportElement[] childs = next.getReport().getChildElementsByType( NoDataBandType.INSTANCE );
    if ( childs.length > 0 ) {
      return InlineSubreportProcessor.processBandedSubReports( next, (RootLevelBand) childs[0] );
    }
    return next;
  }

  public boolean isFinish() {
    return false;
  }

  public boolean isRestoreHandler() {
    return false;
  }
}
