/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

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
