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


package org.pentaho.reporting.engine.classic.core.testsupport;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.function.OutputFunction;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractReportProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultOutputFunction;

public class DebugReportProcessor extends AbstractReportProcessor {
  private DebugRenderer renderer;

  public DebugReportProcessor( final MasterReport report, final DebugRenderer renderer )
    throws ReportProcessingException {
    super( report, renderer.getOutputProcessor() );
    this.renderer = renderer;
  }

  /**
   * Returns the layout manager. If the key is <code>null</code>, an instance of the <code>SimplePageLayouter</code>
   * class is returned.
   *
   * @return the page layouter.
   */
  protected OutputFunction createLayoutManager() {
    final DefaultOutputFunction pageableOutputFunction = new DefaultOutputFunction();
    pageableOutputFunction.setRenderer( renderer );
    return pageableOutputFunction;
  }

}
