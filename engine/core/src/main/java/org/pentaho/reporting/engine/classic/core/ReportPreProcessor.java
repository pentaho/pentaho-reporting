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

package org.pentaho.reporting.engine.classic.core;

import org.pentaho.reporting.engine.classic.core.states.datarow.DefaultFlowController;

import java.io.Serializable;

public interface ReportPreProcessor extends Cloneable, Serializable {
  public ReportPreProcessor clone();

  public MasterReport performPreDataProcessing( MasterReport definition, DefaultFlowController flowController )
    throws ReportProcessingException;

  public MasterReport performPreProcessing( MasterReport definition, DefaultFlowController flowController )
    throws ReportProcessingException;

  public SubReport performPreDataProcessing( SubReport definition, DefaultFlowController flowController )
    throws ReportProcessingException;

  public SubReport performPreProcessing( SubReport definition, DefaultFlowController flowController )
    throws ReportProcessingException;
}
