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

import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.StructureFunction;
import org.pentaho.reporting.engine.classic.core.states.FunctionStorageKey;
import org.pentaho.reporting.engine.classic.core.states.LayoutProcess;
import org.pentaho.reporting.engine.classic.core.states.ReportState;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.states.datarow.DefaultFlowController;
import org.pentaho.reporting.engine.classic.core.states.datarow.ExpressionDataRow;
import org.pentaho.reporting.engine.classic.core.states.datarow.MasterDataRow;

/**
 * Creation-Date: 03.07.2007, 13:57:49
 *
 * @author Thomas Morgner
 */
public class ReportDoneHandler implements AdvanceHandler {
  public static final AdvanceHandler HANDLER = new ReportDoneHandler();

  private ReportDoneHandler() {
  }

  public int getEventCode() {
    return ReportEvent.REPORT_DONE;
  }

  public ProcessState advance( final ProcessState state ) throws ReportProcessingException {
    final ProcessState next = state.deriveForAdvance();
    next.fireReportEvent();
    if ( next.isSubReportEvent() && next.getLevel() == LayoutProcess.LEVEL_PAGINATE ) {
      next.firePageFinishedEvent( true );
    }
    return next;
  }

  public ProcessState commit( final ProcessState state ) throws ReportProcessingException {
    // better clone twice than to face the subtle errors that crawl out here..
    final ProcessState next = state.deriveForAdvance();
    final DefaultFlowController flowController = next.getFlowController();
    final MasterDataRow masterRow = flowController.getMasterRow();
    final ExpressionDataRow expressionDataRow = masterRow.getExpressionDataRow();
    final Expression[] expressions = expressionDataRow.getExpressions();

    if ( next.isSubReportEvent() ) {
      next.setAdvanceHandler( EndSubReportHandler.HANDLER );
    } else {
      next.setAdvanceHandler( EndReportHandler.HANDLER );
    }
    final ReportStateKey parentStateKey;
    final ReportState parentState = next.getParentSubReportState();
    if ( parentState == null ) {
      parentStateKey = null;
    } else {
      parentStateKey = parentState.getProcessKey();
    }
    final FunctionStorageKey functionStorageKey = FunctionStorageKey.createKey( parentStateKey, next.getReport() );
    next.getFunctionStorage().store( functionStorageKey, expressions, expressionDataRow.getColumnCount() );
    final StructureFunction[] structureFunctions = next.getLayoutProcess().getCollectionFunctions();
    next.getStructureFunctionStorage().store( functionStorageKey, structureFunctions, structureFunctions.length );

    final DefaultFlowController pfc = flowController.performClearExportedParameters();
    final DefaultFlowController efc = pfc.deactivateExpressions();
    if ( next.isSubReportEvent() ) {
      final DefaultFlowController qfcSr = efc.performReturnFromSubreport();
      next.setFlowController( qfcSr );
    } else {
      final DefaultFlowController qfc = efc.performReturnFromQuery();
      next.setFlowController( qfc );
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
