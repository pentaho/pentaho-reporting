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


package org.pentaho.reporting.engine.classic.core.modules.output.fast.csv;

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.AbstractContentProducerTemplate;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.FastExportTemplateProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.FormattedDataBuilder;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;

import java.io.IOException;
import java.io.OutputStream;

public class FastCsvContentProducerTemplate extends AbstractContentProducerTemplate {
  private final OutputStream outputStream;
  private String encoding;

  public FastCsvContentProducerTemplate( final SheetLayout sharedSheetLayout, final OutputStream outputStream,
      final String encoding ) {
    super( sharedSheetLayout );
    this.outputStream = outputStream;
    this.encoding = encoding;
  }

  protected void writeContent( final Band band, final ExpressionRuntime runtime,
      final FormattedDataBuilder messageFormatSupport ) throws IOException, ReportProcessingException,
    ContentProcessingException {
    messageFormatSupport.compute( band, runtime, outputStream );
  }

  protected FastExportTemplateProducer createTemplateProducer() {
    return new CsvTemplateProducer( getMetaData(), getSharedSheetLayout(), encoding );
  }

}
