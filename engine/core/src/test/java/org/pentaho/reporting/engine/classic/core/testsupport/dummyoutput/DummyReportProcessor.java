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


package org.pentaho.reporting.engine.classic.core.testsupport.dummyoutput;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.function.OutputFunction;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractReportProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultOutputFunction;

/**
 * Warning: Only works for simple reports. No crosstab or subreports allowed or you see crashes.
 */
public class DummyReportProcessor extends AbstractReportProcessor {
  public DummyReportProcessor( final MasterReport report ) throws ReportProcessingException {
    super( report, new DummyOutputProcessor() );
  }

  protected OutputFunction createLayoutManager() {
    final DefaultOutputFunction outputFunction = new DefaultOutputFunction();
    outputFunction.setRenderer( new DummyRenderer( getOutputProcessor() ) );
    return outputFunction;
  }
}
