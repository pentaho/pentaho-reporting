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
import org.pentaho.reporting.engine.classic.core.layout.output.PhysicalPageKey;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.AbstractTableOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableContentProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.csv.helper.CSVOutputProcessorMetaData;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.NameGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Creation-Date: 09.05.2007, 14:36:28
 *
 * @author Thomas Morgner
 */
public class PageableCSVOutputProcessor extends AbstractTableOutputProcessor implements PageableOutputProcessor {
  private List physicalPages;
  private OutputProcessorMetaData metaData;
  private CSVPrinter printer;
  private FlowSelector flowSelector;

  public PageableCSVOutputProcessor() {
    this.physicalPages = new ArrayList();
    this.metaData = new CSVOutputProcessorMetaData( CSVOutputProcessorMetaData.PAGINATION_FULL );
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

  protected void processingPagesFinished() {
    super.processingPagesFinished();
    physicalPages = Collections.unmodifiableList( physicalPages );
  }

  public int getPhysicalPageCount() {
    return physicalPages.size();
  }

  public PhysicalPageKey getPhysicalPage( final int page ) {
    if ( isPaginationFinished() == false ) {
      throw new IllegalStateException();
    }

    return (PhysicalPageKey) physicalPages.get( page );
  }

  protected LogicalPageKey createLogicalPage( final int width, final int height ) {
    final LogicalPageKey key = super.createLogicalPage( width, height );
    for ( int h = 0; h < key.getHeight(); h++ ) {
      for ( int w = 0; w < key.getWidth(); w++ ) {
        physicalPages.add( key.getPage( w, h ) );
      }
    }
    return key;
  }

  public OutputProcessorMetaData getMetaData() {
    return metaData;
  }

  public FlowSelector getFlowSelector() {
    return flowSelector;
  }

  public void setFlowSelector( final FlowSelector flowSelector ) {
    this.flowSelector = flowSelector;
  }

  protected void processTableContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage,
      final TableContentProducer contentProducer ) throws ContentProcessingException {
    printer.print( logicalPage, contentProducer, metaData, false );
  }

  protected void processingContentFinished() {
    if ( isContentGeneratable() == false ) {
      return;
    }

    metaData.commit();
    printer.close();
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
