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


package org.pentaho.reporting.engine.classic.core.modules.output.table.xls;

import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.DisplayAllFlowSelector;
import org.pentaho.reporting.engine.classic.core.layout.output.FlowSelector;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.AbstractTableOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableContentProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper.ExcelOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper.ExcelPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper.ExcelTableContentProducer;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.InputStream;
import java.io.OutputStream;

public class FlowExcelOutputProcessor extends AbstractTableOutputProcessor {
  private OutputProcessorMetaData metaData;
  private FlowSelector flowSelector;
  private ExcelPrinter printer;

  public FlowExcelOutputProcessor( final Configuration config, final OutputStream outputStream,
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

    this.metaData = new ExcelOutputProcessorMetaData( ExcelOutputProcessorMetaData.PAGINATION_MANUAL );
    this.flowSelector = new DisplayAllFlowSelector();

    this.printer = new ExcelPrinter( outputStream, resourceManager );
  }

  public boolean isUseXlsxFormat() {
    return printer.isUseXlsxFormat();
  }

  public void setUseXlsxFormat( final boolean useXlsxFormat ) {
    printer.setUseXlsxFormat( useXlsxFormat );
  }

  public void setMaxSheetRowCount( int rowCount ) {
    if ( metaData instanceof AbstractOutputProcessorMetaData ) {
      AbstractOutputProcessorMetaData meta = (AbstractOutputProcessorMetaData) metaData;
      meta.addNumericFeature( OutputProcessorFeature.SHEET_ROW_LIMIT, rowCount );
    }
  }

  public InputStream getTemplateInputStream() {
    return printer.getTemplateInputStream();
  }

  public void setTemplateInputStream( final InputStream templateInputStream ) {
    printer.setTemplateInputStream( templateInputStream );
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
    if ( !this.printer.isInitialized() ) {
      this.printer.init( metaData );
    }

    printer.print( logicalPageKey, logicalPage, contentProducer, false );
  }

  protected void updateTableContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPageBox,
      final TableContentProducer tableContentProducer, final boolean performOutput ) throws ContentProcessingException {
    if ( !this.printer.isInitialized() ) {
      this.printer.init( metaData );
    }

    printer.print( logicalPageKey, logicalPageBox, tableContentProducer, true );
  }

  protected void processingContentFinished() {
    if ( isContentGeneratable() == false ) {
      return;
    }
    if ( !this.printer.isInitialized() ) {
      this.printer.init( metaData );
    }

    this.metaData.commit();
    this.printer.close();
  }

  protected TableContentProducer createTableContentProducer( final SheetLayout layout ) {
    return new ExcelTableContentProducer( layout, getMetaData() );
  }
}
