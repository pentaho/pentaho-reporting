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
 * Copyright (c) 2001 - 2023 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.states;

import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.PerformanceTags;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.PageEventListener;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.OutputFunction;
import org.pentaho.reporting.engine.classic.core.function.StructureFunction;
import org.pentaho.reporting.engine.classic.core.states.datarow.ExpressionEventHelper;
import org.pentaho.reporting.engine.classic.core.states.datarow.InlineDataRowRuntime;
import org.pentaho.reporting.engine.classic.core.states.datarow.LevelStorage;
import org.pentaho.reporting.engine.classic.core.states.datarow.OutputFunctionLevelStorage;
import org.pentaho.reporting.libraries.base.util.PerformanceLoggingStopWatch;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class InitialLayoutProcess extends ExpressionEventHelper implements LayoutProcess {
  private class CloseListener implements ChangeListener {
    public void stateChanged( final ChangeEvent e ) {
      stopWatch.close();
    }
  }

  private static final StructureFunction[] EMPTY_FUNCTIONS = new StructureFunction[0];

  private InlineDataRowRuntime inlineDataRowRuntime;
  private OutputFunction outputFunction;
  private boolean outputFunctionIsPageListener;
  private PerformanceLoggingStopWatch stopWatch;
  private PerformanceMonitorContext monitorContext;

  public InitialLayoutProcess( final OutputFunction outputFunction, final PerformanceMonitorContext monitorContext ) {
    if ( outputFunction == null ) {
      throw new NullPointerException();
    }

    this.outputFunction = outputFunction;
    this.outputFunctionIsPageListener = ( outputFunction instanceof PageEventListener );
    this.monitorContext = monitorContext;
    this.monitorContext.addChangeListener( new CloseListener() );
    this.stopWatch = monitorContext.createStopWatch( PerformanceTags.REPORT_LAYOUT_GENERATE );
  }

  public LayoutProcess getParent() {
    return null;
  }

  public boolean isPageListener() {
    return outputFunctionIsPageListener;
  }

  public OutputFunction getOutputFunction() {
    return outputFunction;
  }

  public void restart( final ReportState state ) throws ReportProcessingException {
    try {
      stopWatch.start();
      if ( inlineDataRowRuntime == null ) {
        inlineDataRowRuntime = new InlineDataRowRuntime();
      }
      inlineDataRowRuntime.setState( state );

      final ExpressionRuntime oldRuntime;
      final OutputFunction outputFunction = getOutputFunction();
      if ( outputFunction != null ) {
        oldRuntime = outputFunction.getRuntime();
        outputFunction.setRuntime( inlineDataRowRuntime );
      } else {
        oldRuntime = null;
      }

      try {
        if ( outputFunction != null ) {
          outputFunction.restart( state );
        }
      } finally {
        if ( outputFunction != null ) {
          outputFunction.setRuntime( oldRuntime );
        }
      }
    } finally {
      stopWatch.stop( true );
    }
  }

  public StructureFunction[] getCollectionFunctions() {
    return EMPTY_FUNCTIONS;
  }

  public LayoutProcess deriveForStorage() {
    try {
      final InitialLayoutProcess lp = (InitialLayoutProcess) super.clone();
      lp.inlineDataRowRuntime = null;
      lp.outputFunction = outputFunction.deriveForStorage();
      return lp;
    } catch ( final CloneNotSupportedException e ) {
      throw new IllegalStateException();
    }
  }

  public LayoutProcess deriveForPagebreak() {
    try {
      final InitialLayoutProcess lp = (InitialLayoutProcess) super.clone();
      lp.inlineDataRowRuntime = null;
      lp.outputFunction = outputFunction.deriveForPagebreak();
      return lp;
    } catch ( final CloneNotSupportedException e ) {
      throw new IllegalStateException();
    }
  }

  public Object clone() {
    try {
      final InitialLayoutProcess lp = (InitialLayoutProcess) super.clone();
      lp.inlineDataRowRuntime = null;
      lp.outputFunction = (OutputFunction) outputFunction.clone();
      return lp;
    } catch ( CloneNotSupportedException cne ) {
      throw new IllegalStateException( cne );
    }
  }

  protected int getRunLevelCount() {
    return 1;
  }

  protected LevelStorage getRunLevel( final int index ) {
    if ( index != 0 ) {
      throw new IndexOutOfBoundsException();
    }
    return new OutputFunctionLevelStorage( LayoutProcess.LEVEL_PAGINATE, outputFunction, outputFunctionIsPageListener );
  }

  protected ExpressionRuntime getRuntime() {
    return inlineDataRowRuntime;
  }

  public void fireReportEvent( final ReportEvent event ) {
    try {
      stopWatch.start();

      if ( inlineDataRowRuntime == null ) {
        inlineDataRowRuntime = new InlineDataRowRuntime();
      }
      final ReportState state = inlineDataRowRuntime.getState();
      inlineDataRowRuntime.setState( event.getState() );

      try {
        final int pageEventMask = ReportEvent.PAGE_STARTED | ReportEvent.PAGE_FINISHED;
        if ( ( event.getType() & pageEventMask ) == 0
            && ( ( event.getType() & ReportEvent.GROUP_BODY_FINISHED ) == ReportEvent.GROUP_BODY_FINISHED ) ) {
          fireGroupBodyFinishedEvent( event );
        } else {
          super.fireReportEvent( event );
        }
      } catch ( InvalidReportStateException exception ) {
        throw exception;
      } catch ( Throwable t ) {
        throw new InvalidReportStateException( "Failed to fire report event for sub-layout-process", t );
      } finally {
        inlineDataRowRuntime.setState( state );
      }
    } finally {
      stopWatch.stop( true );
    }
  }

  private void fireGroupBodyFinishedEvent( final ReportEvent event ) {
    if ( event.getLevel() != LayoutProcess.LEVEL_PAGINATE ) {
      return;
    }

    final ExpressionRuntime runtime = getRuntime();
    final OutputFunction expression = getOutputFunction();

    final boolean deepTraversing = event.isDeepTraversing();
    if ( deepTraversing && expression.isDeepTraversing() == false ) {
      return;
    }

    final ExpressionRuntime oldRuntime = expression.getRuntime();
    expression.setRuntime( runtime );
    try {
      expression.groupBodyFinished( event );
      super.reportEvent = event;
      evaluateSingleExpression( expression );
    } catch ( InvalidReportStateException rse ) {
      throw rse;
    } catch ( Exception ex ) {
      evaluateToNull( expression );
    }

    expression.setRuntime( oldRuntime );
  }

  public void close() {
    stopWatch.close();
  }
}
