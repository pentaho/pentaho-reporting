package org.pentaho.reporting.engine.classic.core.states.process;

import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.libraries.base.util.DebugLog;

/**
 * This handler deferrs the event progression by one "advance" call, so that we can hopefully clean up the
 * pages and generate some page-events.
 *
 * @author Thomas Morgner.
 */
public class RestartOnNewPageHandler implements AdvanceHandler
{
  private AdvanceHandler handler;

  public RestartOnNewPageHandler(final AdvanceHandler handler)
  {
    if (handler == null)
    {
      throw new NullPointerException();
    }
    this.handler = handler;
  }

  public ProcessState advance(final ProcessState state) throws ReportProcessingException
  {
    final ProcessState newState = state.deriveForAdvance();
    newState.getFlowController().getMasterRow().refresh();
    newState.getLayoutProcess().restart(newState);
    return newState;
  }

  public ProcessState commit(final ProcessState state) throws ReportProcessingException
  {
    state.setAdvanceHandler(this.handler);
    return state;
  }

  public boolean isFinish()
  {
    return handler.isFinish();
  }

  public int getEventCode()
  {
    return handler.getEventCode();
  }

  public static ProcessState create (final ProcessState state) throws ReportProcessingException
  {
    if (state.isArtifcialState())
    {
      return state;
    }
    if (state.getAdvanceHandler() instanceof PendingPagesHandler)
    {
      return state.deriveForAdvance();
    }
    if (state.getAdvanceHandler() instanceof RestartOnNewPageHandler)
    {
      return state.deriveForAdvance();
    }
    if (state.getAdvanceHandler() instanceof ReportDoneHandler)
    {
      return state;
    }
    if (state.getAdvanceHandler() instanceof BeginReportHandler)
    {
      return state;
    }
    final ProcessState newstate = state.deriveForAdvance();
    newstate.setAdvanceHandler(new RestartOnNewPageHandler(newstate.getAdvanceHandler()));
    return newstate;
  }

  public boolean isRestoreHandler()
  {
    return true;
  }
}
