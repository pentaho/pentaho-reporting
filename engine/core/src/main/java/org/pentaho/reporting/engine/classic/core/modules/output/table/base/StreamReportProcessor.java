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
import org.pentaho.reporting.engine.classic.core.layout.StreamingRenderer;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractReportProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultOutputFunction;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;

/**
 * Creation-Date: 03.05.2007, 10:30:02
 *
 * @author Thomas Morgner
 */
public class StreamReportProcessor extends AbstractReportProcessor {
  public StreamReportProcessor( final MasterReport report, final OutputProcessor outputProcessor )
    throws ReportProcessingException {
    super( report, outputProcessor );
    if ( outputProcessor.getMetaData().isFeatureSupported( OutputProcessorFeature.PAGEBREAKS ) ) {
      throw new ReportProcessingException(
          "Streaming report processors cannot be used in conjunction with pageable-outputs" );
    }
  }

  /**
   * Returns the layout manager. If the key is <code>null</code>, an instance of the <code>SimplePageLayouter</code>
   * class is returned.
   *
   * @return the page layouter.
   * @throws ReportProcessingException
   *           if there is a processing error.
   */
  protected OutputFunction createLayoutManager() {
    final DefaultOutputFunction outputFunction = new DefaultOutputFunction();
    outputFunction.setRenderer( new StreamingRenderer( getOutputProcessor() ) );
    return outputFunction;
  }
}
