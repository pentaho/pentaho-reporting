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


package org.pentaho.reporting.engine.classic.core.modules.output.table.html;

import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.DisplayAllFlowSelector;
import org.pentaho.reporting.engine.classic.core.layout.output.FlowSelector;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.AbstractTableOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableContentProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.HtmlOutputProcessorMetaData;

/**
 * Creation-Date: 04.05.2007, 16:36:31
 *
 * @author Thomas Morgner
 */
public class FlowHtmlOutputProcessor extends AbstractTableOutputProcessor implements HtmlOutputProcessor {
  private HtmlPrinter printer;
  private OutputProcessorMetaData metaData;
  private FlowSelector flowSelector;

  public FlowHtmlOutputProcessor() {
    this.flowSelector = new DisplayAllFlowSelector();
    this.metaData = new HtmlOutputProcessorMetaData( HtmlOutputProcessorMetaData.PAGINATION_MANUAL );
  }

  public FlowSelector getFlowSelector() {
    return flowSelector;
  }

  public void setFlowSelector( final FlowSelector flowSelector ) {
    this.flowSelector = flowSelector;
  }

  public OutputProcessorMetaData getMetaData() {
    return metaData;
  }

  public HtmlPrinter getPrinter() {
    return printer;
  }

  public void setPrinter( final HtmlPrinter printer ) {
    this.printer = printer;
  }

  protected void processTableContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage,
      final TableContentProducer contentProducer ) throws ContentProcessingException {
    if ( printer == null ) {
      return;
    }

    printer.print( logicalPageKey, logicalPage, contentProducer, metaData, false );
  }

  protected void updateTableContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPageBox,
      final TableContentProducer tableContentProducer, final boolean performOutput ) throws ContentProcessingException {
    if ( printer == null ) {
      return;
    }

    printer.print( logicalPageKey, logicalPageBox, tableContentProducer, metaData, true );
  }

  protected void processingContentFinished() {
    if ( isContentGeneratable() == false ) {
      return;
    }

    this.metaData.commit();
  }

}
