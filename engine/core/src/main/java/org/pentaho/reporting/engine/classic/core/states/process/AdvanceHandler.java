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


package org.pentaho.reporting.engine.classic.core.states.process;

import org.pentaho.reporting.engine.classic.core.ReportProcessingException;

/**
 * This handler does the same as the ReportState.advance() method, but does not create a new state object all the time.
 *
 * @author Thomas Morgner
 */
public interface AdvanceHandler {
  public ProcessState advance( ProcessState state ) throws ReportProcessingException;

  public ProcessState commit( ProcessState state ) throws ReportProcessingException;

  public boolean isFinish();

  public int getEventCode();

  public boolean isRestoreHandler();
}
