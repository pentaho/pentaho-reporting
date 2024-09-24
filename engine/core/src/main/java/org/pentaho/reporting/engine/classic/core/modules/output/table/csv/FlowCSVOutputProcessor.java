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

package org.pentaho.reporting.engine.classic.core.modules.output.table.csv;

import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.DisplayAllFlowSelector;
import org.pentaho.reporting.engine.classic.core.layout.output.FlowSelector;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.AbstractTableOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableContentProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.csv.helper.CSVOutputProcessorMetaData;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.NameGenerator;

/**
 * Creation-Date: 09.05.2007, 14:36:28
 *
 * @author Thomas Morgner
 */
public class FlowCSVOutputProcessor extends AbstractTableOutputProcessor {
  private OutputProcessorMetaData metaData;
  private FlowSelector flowSelector;
  private CSVPrinter printer;

  public FlowCSVOutputProcessor( final Configuration config ) {
    if ( config == null ) {
      throw new NullPointerException();
    }

    this.metaData = new CSVOutputProcessorMetaData( CSVOutputProcessorMetaData.PAGINATION_MANUAL );
    this.flowSelector = new DisplayAllFlowSelector();
    this.printer = new CSVPrinter();
  }

  public void processingStarted( final ReportDefinition report, final ProcessingContext processingContext ) {
    super.processingStarted( report, processingContext );
    this.printer.initialize( processingContext.getConfiguration() );
  }

  public String getEncoding() {
    return printer.getEncoding();
  }

  public void setEncoding( final String encoding ) {
    if ( encoding == null ) {
      throw new NullPointerException();
    }

    printer.setEncoding( encoding );
  }

  public OutputProcessorMetaData getMetaData() {
    return metaData;
  }

  public void setFlowSelector( final FlowSelector flowSelector ) {
    this.flowSelector = flowSelector;
  }

  public FlowSelector getFlowSelector() {
    return flowSelector;
  }

  protected void processTableContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage,
      final TableContentProducer contentProducer ) throws ContentProcessingException {
    printer.print( logicalPage, contentProducer, metaData, false );
  }

  protected void updateTableContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPageBox,
      final TableContentProducer tableContentProducer, final boolean performOutput ) throws ContentProcessingException {
    printer.print( logicalPageBox, tableContentProducer, metaData, true );
  }

  protected void processingContentFinished() {
    if ( isContentGeneratable() == false ) {
      return;
    }

    this.printer.close();
    this.metaData.commit();
  }

  public ContentLocation getContentLocation() {
    return printer.getContentLocation();
  }

  public void setContentLocation( final ContentLocation contentLocation ) {
    printer.setContentLocation( contentLocation );
  }

  public NameGenerator getContentNameGenerator() {
    return printer.getContentNameGenerator();
  }

  public void setContentNameGenerator( final NameGenerator contentNameGenerator ) {
    printer.setContentNameGenerator( contentNameGenerator );
  }

}
