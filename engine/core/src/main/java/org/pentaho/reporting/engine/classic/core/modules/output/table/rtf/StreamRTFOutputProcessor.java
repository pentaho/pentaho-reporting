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


package org.pentaho.reporting.engine.classic.core.modules.output.table.rtf;

import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.DisplayAllFlowSelector;
import org.pentaho.reporting.engine.classic.core.layout.output.FlowSelector;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.AbstractTableOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableContentProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.helper.RTFOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.helper.RTFPrinter;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.OutputStream;

/**
 * Creation-Date: 09.05.2007, 14:36:28
 *
 * @author Thomas Morgner
 */
public class StreamRTFOutputProcessor extends AbstractTableOutputProcessor {
  private RTFOutputProcessorMetaData metaData;
  private FlowSelector flowSelector;
  private RTFPrinter printer;

  public StreamRTFOutputProcessor( final Configuration config, final OutputStream outputStream,
      final ResourceManager resourceManager ) {
    if ( config == null ) {
      throw new NullPointerException();
    }
    if ( outputStream == null ) {
      throw new NullPointerException();
    }
    if ( resourceManager == null ) {
      throw new NullPointerException();
    }

    this.metaData = new RTFOutputProcessorMetaData( RTFOutputProcessorMetaData.PAGINATION_NONE );
    this.flowSelector = new DisplayAllFlowSelector();

    this.printer = new RTFPrinter();
    this.printer.init( config, outputStream, resourceManager );
  }

  public OutputProcessorMetaData getMetaData() {
    return metaData;
  }

  protected FlowSelector getFlowSelector() {
    return flowSelector;
  }

  protected void processTableContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage,
      final TableContentProducer contentProducer ) throws ContentProcessingException {
    printer.print( logicalPageKey, logicalPage, contentProducer, metaData, false );
  }

  protected void updateTableContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPageBox,
      final TableContentProducer tableContentProducer, final boolean performOutput ) throws ContentProcessingException {
    printer.print( logicalPageKey, logicalPageBox, tableContentProducer, metaData, true );
  }

  protected void processingContentFinished() {
    if ( isContentGeneratable() == false ) {
      return;
    }

    this.printer.close();
    this.metaData.commit();
  }

}
