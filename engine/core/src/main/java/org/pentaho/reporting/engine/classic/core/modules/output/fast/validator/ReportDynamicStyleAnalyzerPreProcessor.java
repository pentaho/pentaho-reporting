/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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
