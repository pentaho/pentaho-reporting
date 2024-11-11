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

import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabOtherGroupBody;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroupBody;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.StructureFunction;
import org.pentaho.reporting.engine.classic.core.states.crosstab.CrosstabProcessorFunction;
import org.pentaho.reporting.engine.classic.core.states.crosstab.CrosstabSpecification;
import org.pentaho.reporting.engine.classic.core.states.crosstab.DummyCrosstabSpecification;
import org.pentaho.reporting.engine.classic.core.states.datarow.DefaultFlowController;

public class BeginCrosstabHandler implements AdvanceHandler {
  public static final BeginCrosstabHandler HANDLER = new BeginCrosstabHandler();

  private BeginCrosstabHandler() {
  }

  public ProcessState advance( final ProcessState state ) throws ReportProcessingException {
    final ProcessState next = state.deriveForAdvance();
    next.enterGroup();

    final CrosstabSpecification cs = findCrosstabSpecification( next );
    final DefaultFlowController controller = next.getFlowController().startCrosstabMode( cs );
    next.setFlowController( controller );

    next.fireReportEvent();
    next.enterPresentationGroup();
    return next;
  }

  private CrosstabSpecification findCrosstabSpecification( final ProcessState next ) throws ReportProcessingException {
    final StructureFunction[] functions = next.getLayoutProcess().getCollectionFunctions();
    for ( int i = 0; i < functions.length; i++ ) {
      final StructureFunction function = functions[i];
      if ( function instanceof CrosstabProcessorFunction ) {
        final CrosstabSpecification cs = (CrosstabSpecification) function.getValue();
        if ( cs == null ) {
          return new DummyCrosstabSpecification( next.getProcessKey() );
        }
        return cs;
      }
    }
    throw new ReportProcessingException( "Failed to locate crosstab-spec, cannot continue." );
  }

  public ProcessState commit( final ProcessState next ) throws ReportProcessingException {
    final CrosstabGroup group = (CrosstabGroup) next.getReport().getGroup( next.getCurrentGroupIndex() );

    final GroupBody body = group.getBody();
    if ( body instanceof CrosstabRowGroupBody ) {
      next.setAdvanceHandler( BeginCrosstabRowAxisHandler.HANDLER );
    } else if ( body instanceof CrosstabOtherGroupBody ) {
      next.setAdvanceHandler( BeginCrosstabOtherAxisHandler.HANDLER );
    } else {
      throw new IllegalStateException( "This report is totally messed up!" );
    }

    return next;
  }

  public boolean isFinish() {
    return false;
  }

  public int getEventCode() {
    return ReportEvent.CROSSTABBING_TABLE | ReportEvent.GROUP_STARTED;
  }

  public boolean isRestoreHandler() {
    return false;
  }
}
