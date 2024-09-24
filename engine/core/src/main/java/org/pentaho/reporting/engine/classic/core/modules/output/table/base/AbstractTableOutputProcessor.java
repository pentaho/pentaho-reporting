/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001 - 2024 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.base;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractOutputProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.FlowSelector;
import org.pentaho.reporting.engine.classic.core.layout.output.IterativeOutputProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.libraries.base.util.MemoryUsageMessage;
import org.pentaho.reporting.libraries.formatting.FastMessageFormat;

import java.util.ArrayList;

/**
 * The Table-Output processor uses the pagination stage to build a list of table-layouts.
 *
 * @author Thomas Morgner
 */
public abstract class AbstractTableOutputProcessor extends AbstractOutputProcessor implements IterativeOutputProcessor {
  private static final Log logger = LogFactory.getLog( AbstractTableOutputProcessor.class );

  public static final OutputProcessorFeature.BooleanOutputProcessorFeature STRICT_LAYOUT =
      new OutputProcessorFeature.BooleanOutputProcessorFeature( "strict-layout" );
  public static final OutputProcessorFeature.BooleanOutputProcessorFeature TREAT_ELLIPSE_AS_RECTANGLE =
      new OutputProcessorFeature.BooleanOutputProcessorFeature( "treat-ellipse-as-rectangle" );
  public static final OutputProcessorFeature.BooleanOutputProcessorFeature SHAPES_CONTENT =
      new OutputProcessorFeature.BooleanOutputProcessorFeature( "shape-content" );
  public static final OutputProcessorFeature.BooleanOutputProcessorFeature ROTATED_TEXT_AS_IMAGES =
    new OutputProcessorFeature.BooleanOutputProcessorFeature( "rotated-text-as-images" );
  public static final OutputProcessorFeature.BooleanOutputProcessorFeature BASE64_IMAGES =
    new OutputProcessorFeature.BooleanOutputProcessorFeature( "base64-images" );

  private ArrayList<TableLayoutProducer> sheetLayouts;
  private TableLayoutProducer currentLayout;
  private TableContentProducer currentContent;
  private long lastTime;

  protected AbstractTableOutputProcessor() {
    this.sheetLayouts = new ArrayList<TableLayoutProducer>();
  }

  public void processingStarted( final ReportDefinition report, final ProcessingContext processingContext ) {
    super.processingStarted( report, processingContext );
    lastTime = System.currentTimeMillis();
  }

  public boolean isNeedAlignedPage() {
    return getMetaData().isFeatureSupported( OutputProcessorFeature.UNALIGNED_PAGEBANDS ) == false;
  }

  protected final void processPaginationContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage ) {
    if ( currentLayout == null ) {
      currentLayout = new TableLayoutProducer( getMetaData() );
    }
    currentLayout.update( logicalPage, false );
    currentLayout.pageCompleted();

    // ModelPrinter.print(logicalPage);
    final long rowCount = currentLayout.getLayout().getRowCount();
    logPerformance( "Pagination done: ", rowCount, true );

    sheetLayouts.add( currentLayout );
    currentLayout = null;
  }

  protected final void processPageContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage )
    throws ContentProcessingException {
    // this one is only called after the pagination is complete. In that case we have a valid table.
    final FlowSelector tableInterceptor = getFlowSelector();
    if ( tableInterceptor == null ) {
      return;
    }

    if ( tableInterceptor.isLogicalPageAccepted( logicalPageKey ) == false ) {
      return;
    }

    if ( currentContent == null ) {
      final int pageCursor = getPageCursor();
      final TableLayoutProducer sheetLayout = sheetLayouts.get( pageCursor );
      currentContent = createTableContentProducer( sheetLayout.getLayout() );
    }

    currentContent.compute( logicalPage, false );
    // ModelPrinter.print(logicalPage);
    processTableContent( logicalPageKey, logicalPage, currentContent );
    currentContent.clearFinishedBoxes();

    final long rowCount = currentContent.getContentRowCount();
    logPerformance( "Content done: ", rowCount, true );

    currentContent = null;
  }

  protected abstract void processTableContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage,
      final TableContentProducer contentProducer ) throws ContentProcessingException;

  public final void processIterativeContent( final LogicalPageBox logicalPageBox, final boolean performOutput )
    throws ContentProcessingException {
    if ( isContentGeneratable() == false ) {
      // In pagination mode.
      if ( currentLayout == null ) {
        currentLayout = new TableLayoutProducer( getMetaData() );
      }
      currentLayout.update( logicalPageBox, true );

      final long rowCount = currentLayout.getLayout().getRowCount();
      logPerformance( "Still Iterating: ", rowCount, false );
    } else {
      // In content generation mode.
      final int pageCursor = getPageCursor();
      final LogicalPageKey logicalPageKey = getLogicalPage( pageCursor );
      final FlowSelector tableInterceptor = getFlowSelector();
      if ( tableInterceptor == null ) {
        return;
      }

      if ( tableInterceptor.isLogicalPageAccepted( logicalPageKey ) == false ) {
        return;
      }

      if ( currentContent == null ) {
        final TableLayoutProducer sheetLayout = sheetLayouts.get( pageCursor );
        currentContent = createTableContentProducer( sheetLayout.getLayout() );
      }

      currentContent.compute( logicalPageBox, true );
      updateTableContent( logicalPageKey, logicalPageBox, currentContent, performOutput );
      currentContent.clearFinishedBoxes();

      final long rowCount = currentContent.getContentRowCount();
      logPerformance( "Still Iterating: ", rowCount, false );
    }
  }

  private void logPerformance( final String message, final long rowCount, final boolean force ) {
    final long time = System.currentTimeMillis();
    final double deltaTime = time - lastTime;
    if ( force || deltaTime > 30000 ) {
      lastTime = time;
      if ( logger.isDebugEnabled() ) {
        final double rowsPerSec = ( rowCount * 1000.0 / deltaTime );

        final FastMessageFormat messageFormat =
            new FastMessageFormat(
                "{0} - Rows: {1} - Time: {2,number,0.000}sec - Throughput: ({3,number,0.000} rows/sec) " );
        logger.debug( new MemoryUsageMessage( messageFormat.format( new Object[] { message, rowCount,
          deltaTime / 1000.0, rowsPerSec } ) ) );
      }
    }
  }

  protected TableContentProducer createTableContentProducer( final SheetLayout layout ) {
    return new TableContentProducer( layout, getMetaData() );
  }

  protected void updateTableContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPageBox,
      final TableContentProducer tableContentProducer, final boolean performOutput ) throws ContentProcessingException {
    throw new UnsupportedOperationException(
        "This output processor does not implement the iterative content processing protocol." );
  }

  protected abstract FlowSelector getFlowSelector();
}
