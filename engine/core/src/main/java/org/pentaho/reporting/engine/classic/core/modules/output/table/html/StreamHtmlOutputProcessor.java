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
import org.pentaho.reporting.libraries.base.config.Configuration;

/**
 * Creation-Date: 04.05.2007, 16:36:40
 *
 * @author Thomas Morgner
 */
public class StreamHtmlOutputProcessor extends AbstractTableOutputProcessor implements HtmlOutputProcessor {
  private HtmlPrinter printer;
  private OutputProcessorMetaData metaData;
  private FlowSelector flowSelector;

  public StreamHtmlOutputProcessor( final Configuration configuration ) {
    if ( configuration == null ) {
      throw new NullPointerException();
    }

    this.metaData = new HtmlOutputProcessorMetaData( HtmlOutputProcessorMetaData.PAGINATION_NONE );
    this.flowSelector = new DisplayAllFlowSelector();
  }

  protected FlowSelector getFlowSelector() {
    return flowSelector;
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

    printer.print( logicalPageKey, logicalPageBox, tableContentProducer, metaData, true, false );
  }

  protected void processingContentFinished() {
    if ( isContentGeneratable() == false ) {
      return;
    }

    this.metaData.commit();
  }
}
