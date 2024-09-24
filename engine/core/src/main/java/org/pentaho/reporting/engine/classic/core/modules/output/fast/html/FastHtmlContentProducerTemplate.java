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

package org.pentaho.reporting.engine.classic.core.modules.output.fast.html;

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.AbstractContentProducerTemplate;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.FastExportTemplateProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.FormattedDataBuilder;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;
import org.pentaho.reporting.libraries.repository.ContentIOException;

import java.io.IOException;

public class FastHtmlContentProducerTemplate extends AbstractContentProducerTemplate {
  private FastHtmlContentItems contentItems;
  private FastHtmlPrinter htmlPrinter;

  public FastHtmlContentProducerTemplate( final SheetLayout sheetLayout, final FastHtmlContentItems contentItems ) {
    super( sheetLayout );
    this.contentItems = contentItems;
  }

  public void initialize( final ReportDefinition report, final ExpressionRuntime runtime, final boolean pagination ) {
    super.initialize( report, runtime, pagination );
    this.htmlPrinter =
        new FastHtmlPrinter( getSharedSheetLayout(), runtime.getProcessingContext().getResourceManager(), contentItems );
    this.htmlPrinter.init( getMetaData(), report );
  }

  protected void writeContent( final Band band, final ExpressionRuntime runtime,
      final FormattedDataBuilder messageFormatSupport ) throws IOException, ReportProcessingException,
    ContentProcessingException {
    messageFormatSupport.compute( band, runtime, null );
  }

  public void finishReport() throws ReportProcessingException {
    try {
      this.htmlPrinter.close();
    } catch ( IOException e ) {
      throw new ReportProcessingException( "Failed to close report", e );
    } catch ( ContentIOException e ) {
      throw new ReportProcessingException( "Failed to close report", e );
    }
  }

  protected FastExportTemplateProducer createTemplateProducer() {
    return new FastHtmlTemplateProducer( getMetaData(), getSharedSheetLayout(), htmlPrinter );
  }
}
