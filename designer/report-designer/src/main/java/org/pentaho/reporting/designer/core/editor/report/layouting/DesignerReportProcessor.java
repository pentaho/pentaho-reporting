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


package org.pentaho.reporting.designer.core.editor.report.layouting;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.function.OutputFunction;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultOutputFunction;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.StreamReportProcessor;

public class DesignerReportProcessor extends StreamReportProcessor {
  private DesignerOutputProcessor outputProcessor;
  private DesignerRenderComponentFactory componentFactory;

  public DesignerReportProcessor( final MasterReport report,
                                  final DesignerOutputProcessor outputProcessor,
                                  final DesignerRenderComponentFactory componentFactory )
    throws ReportProcessingException {
    super( report, outputProcessor );
    this.outputProcessor = outputProcessor;
    this.componentFactory = componentFactory;
  }

  protected OutputFunction createLayoutManager() {
    final DefaultOutputFunction outputFunction = new DesignerOutputFunction();
    outputFunction.setRenderer( new DesignerRenderer( outputProcessor, componentFactory ) );
    return outputFunction;
  }
}
