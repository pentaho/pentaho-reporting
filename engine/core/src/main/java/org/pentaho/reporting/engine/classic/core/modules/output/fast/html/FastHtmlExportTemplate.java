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


package org.pentaho.reporting.engine.classic.core.modules.output.fast.html;

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.FastExportTemplate;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.FastSheetLayoutProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;

public class FastHtmlExportTemplate implements FastExportTemplate {
  private FastHtmlContentItems contentItems;
  private SheetLayout sharedSheetLayout;
  private FastExportTemplate processor;

  public FastHtmlExportTemplate( final FastHtmlContentItems contentItems ) {
    this.contentItems = contentItems;
  }

  public void initialize( final ReportDefinition report, final ExpressionRuntime runtime, final boolean pagination ) {
    OutputProcessorMetaData metaData = runtime.getProcessingContext().getOutputProcessorMetaData();
    if ( pagination ) {
      this.sharedSheetLayout = new SheetLayout( metaData );
      this.processor = new FastSheetLayoutProducer( sharedSheetLayout );
      this.processor.initialize( report, runtime, pagination );
    } else {
      this.processor = new FastHtmlContentProducerTemplate( sharedSheetLayout, contentItems );
      this.processor.initialize( report, runtime, pagination );
    }
  }

  public void write( final Band band, final ExpressionRuntime runtime ) throws InvalidReportStateException {
    try {
      this.processor.write( band, runtime );
    } catch ( InvalidReportStateException re ) {
      throw re;
    } catch ( Exception e ) {
      throw new InvalidReportStateException( "Other failure", e );
    }
  }

  public void finishReport() throws ReportProcessingException {
    this.processor.finishReport();
  }
}
