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

package org.pentaho.reporting.engine.classic.core.modules.output.fast.validator;

import org.pentaho.reporting.engine.classic.core.AbstractReportPreProcessor;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.states.datarow.DefaultFlowController;

public class ReportDynamicStyleAnalyzerPreProcessor extends AbstractReportPreProcessor {
  public ReportDynamicStyleAnalyzerPreProcessor() {
  }

  public MasterReport performPreProcessing( final MasterReport definition,
                                            final DefaultFlowController flowController )
    throws ReportProcessingException {
    OutputProcessorMetaData meta = flowController.getReportContext().getOutputProcessorMetaData();
    if ( meta.isFeatureSupported( OutputProcessorFeature.FAST_EXPORT ) ) {
      DynamicReportStyleAnalyzer analyzer = new DynamicReportStyleAnalyzer();
      analyzer.compute( definition );
    }
    return definition;
  }

  public SubReport performPreProcessing( final SubReport definition,
                                         final DefaultFlowController flowController ) throws ReportProcessingException {
    OutputProcessorMetaData meta = flowController.getReportContext().getOutputProcessorMetaData();
    if ( meta.isFeatureSupported( OutputProcessorFeature.FAST_EXPORT ) ) {
      DynamicReportStyleAnalyzer analyzer = new DynamicReportStyleAnalyzer();
      analyzer.compute( definition );
    }
    return definition;
  }
}
