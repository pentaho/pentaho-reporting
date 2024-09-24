/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.designer.core.editor.report.layouting;

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;

/**
 * Single-instance layouter for handling the layout computation for a single report. This layouts the full report with
 * all bands. The no-data-band will be layouted in parallel to the itemband.
 */
public class ReportLayouter {
  private LogicalPageBox logicalPageBox;
  private ReportDocumentContext reportRenderContext;
  private long lastModCount;
  private DesignerOutputProcessorMetaData metaData;
  private DesignerRenderComponentFactory componentFactory;

  public ReportLayouter( final ReportRenderContext reportRenderContext ) {
    this.reportRenderContext = reportRenderContext;
    this.lastModCount = 0;
  }

  public LogicalPageBox layout() throws ReportProcessingException, ContentProcessingException {
    final MasterReport report = reportRenderContext.getContextRoot();
    if ( logicalPageBox != null && lastModCount == report.getChangeTracker() ) {
      return logicalPageBox;
    }

    if ( componentFactory == null ) {
      componentFactory = new DesignerRenderComponentFactory( getOutputProcessorMetaData() );
    }

    final DesignerOutputProcessor outputProcessor = new DesignerOutputProcessor( getOutputProcessorMetaData() );
    final DesignerReportProcessor reportProcessor =
      new DesignerReportProcessor( report, outputProcessor, componentFactory );
    reportProcessor.processReport();
    this.logicalPageBox = outputProcessor.getLogicalPage();
    lastModCount = report.getChangeTracker();
    return logicalPageBox;
  }

  public synchronized DesignerOutputProcessorMetaData getOutputProcessorMetaData() {
    if ( metaData == null ) {
      final DesignerOutputProcessorMetaData designerOutputProcessorMetaData = new DesignerOutputProcessorMetaData();
      designerOutputProcessorMetaData.initialize( reportRenderContext.getContextRoot().getConfiguration() );
      metaData = designerOutputProcessorMetaData;
    }
    return metaData;
  }
}
