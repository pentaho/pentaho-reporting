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

package org.pentaho.reporting.engine.classic.core.states.process;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportPreProcessor;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.states.StateUtilities;
import org.pentaho.reporting.engine.classic.core.states.datarow.DefaultFlowController;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaDefinition;

class MasterReportProcessPreprocessor {
  private final DefaultFlowController startFlowController;
  private DefaultFlowController flowController;
  private ReportPreProcessor[] processors;
  private boolean designtime;

  public MasterReportProcessPreprocessor( final DefaultFlowController startFlowController ) {
    this.startFlowController = startFlowController;
    final OutputProcessorMetaData md = startFlowController.getReportContext().getOutputProcessorMetaData();
    this.designtime = md.isFeatureSupported( OutputProcessorFeature.DESIGNTIME );
  }

  public DefaultFlowController getFlowController() {
    return flowController;
  }

  public MasterReport invokePreDataProcessing( final MasterReport report ) throws ReportProcessingException {
    flowController = startFlowController;
    processors = StateUtilities.getAllPreProcessors( report, designtime );
    DataSchemaDefinition fullDefinition = report.getDataSchemaDefinition();
    MasterReport fullReport = report;
    for ( int i = 0; i < processors.length; i++ ) {
      final ReportPreProcessor processor = processors[i];
      fullReport = processor.performPreDataProcessing( fullReport, flowController );
      if ( fullReport.getDataSchemaDefinition() != fullDefinition ) {
        fullDefinition = fullReport.getDataSchemaDefinition();
        flowController = flowController.updateDataSchema( fullDefinition );
      }
    }
    return fullReport;
  }

  public MasterReport invokePreProcessing( final MasterReport report ) throws ReportProcessingException {
    flowController = startFlowController;

    processors = StateUtilities.getAllPreProcessors( report, designtime );
    DataSchemaDefinition fullDefinition = report.getDataSchemaDefinition();
    MasterReport fullReport = report;
    for ( int i = 0; i < processors.length; i++ ) {
      final ReportPreProcessor processor = processors[i];
      fullReport = processor.performPreProcessing( fullReport, flowController );
      if ( fullReport.getDataSchemaDefinition() != fullDefinition ) {
        fullDefinition = fullReport.getDataSchemaDefinition();
        flowController = flowController.updateDataSchema( fullDefinition );
      }
    }
    return fullReport;
  }
}
