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

package org.pentaho.reporting.engine.classic.core.modules.output.table.base;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.function.OutputFunction;
import org.pentaho.reporting.engine.classic.core.layout.FlowRenderer;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractReportProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultOutputFunction;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessor;

/**
 * Creation-Date: 03.05.2007, 10:30:45
 *
 * @author Thomas Morgner
 */
public class FlowReportProcessor extends AbstractReportProcessor {
  public FlowReportProcessor( final MasterReport report, final OutputProcessor outputProcessor )
    throws ReportProcessingException {
    super( report, outputProcessor );
  }

  /**
   * Returns the layout manager. If the key is <code>null</code>, an instance of the <code>SimplePageLayouter</code>
   * class is returned.
   *
   * @return the page layouter.
   */
  protected OutputFunction createLayoutManager() {
    final DefaultOutputFunction outputFunction = new DefaultOutputFunction();
    outputFunction.setRenderer( new FlowRenderer( getOutputProcessor() ) );
    return outputFunction;
  }
}
