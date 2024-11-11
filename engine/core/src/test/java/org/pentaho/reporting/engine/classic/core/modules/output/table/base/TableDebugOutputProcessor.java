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


package org.pentaho.reporting.engine.classic.core.modules.output.table.base;

import junit.framework.AssertionFailedError;

import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.FlowSelector;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.AllPageFlowSelector;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.layout.model.ResultTable;

public class TableDebugOutputProcessor extends AbstractTableOutputProcessor {
  private OutputProcessorMetaData metaData;
  private FlowSelector flowSelector;
  private SheetLayout layout;
  private LogicalPageBox logicalPage;
  private TableContentProducer contentProducer;

  public TableDebugOutputProcessor( final OutputProcessorMetaData metaData ) {
    this.metaData = metaData;
    this.flowSelector = new AllPageFlowSelector( true );
  }

  protected void updateTableContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPageBox,
      final TableContentProducer tableContentProducer, final boolean performOutput ) throws ContentProcessingException {
    this.logicalPage = logicalPageBox;
    this.contentProducer = tableContentProducer;
    this.layout = contentProducer.getSheetLayout();
  }

  protected void processTableContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage,
      final TableContentProducer contentProducer ) throws ContentProcessingException {
    this.logicalPage = logicalPage;
    this.contentProducer = contentProducer;
    this.layout = contentProducer.getSheetLayout();
  }

  public void validate( final ResultTable resultTable ) {
    try {
      // then add it to the layout-producer ..
      resultTable.validate( logicalPage, layout, contentProducer );
    } catch ( AssertionFailedError afe ) {
      ModelPrinter.INSTANCE.print( logicalPage );
      SheetLayoutPrinter.print( logicalPage, layout, contentProducer );
      throw afe;
    }
  }

  protected FlowSelector getFlowSelector() {
    return flowSelector;
  }

  public OutputProcessorMetaData getMetaData() {
    return metaData;
  }

}
