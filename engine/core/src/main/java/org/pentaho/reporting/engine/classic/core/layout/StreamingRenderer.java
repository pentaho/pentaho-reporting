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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.IterativeOutputProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.LayoutPagebreakHandler;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.process.ApplyAutoCommitPageHeaderStep;
import org.pentaho.reporting.engine.classic.core.layout.process.CleanFlowBoxesStep;
import org.pentaho.reporting.engine.classic.core.layout.process.CountBoxesStep;
import org.pentaho.reporting.engine.classic.core.layout.process.FillFlowPagesStep;
import org.pentaho.reporting.engine.classic.core.states.PerformanceMonitorContext;

/**
 * The streaming renderer streams all generated (and layouted) elements to the output processor. The output processor
 * should mark the processed elements by setting the 'dirty' flag to false. Pagebreaks will be ignored, all content ends
 * up in a single stream of data.
 *
 * @author Thomas Morgner
 */
public class StreamingRenderer extends AbstractRenderer {
  private static final Log logger = LogFactory.getLog( StreamingRenderer.class );

  private CountBoxesStep countBoxesStep;
  private CleanFlowBoxesStep cleanBoxesStep;
  private ApplyAutoCommitPageHeaderStep applyAutoCommitPageHeaderStep;
  private FillFlowPagesStep fillPhysicalPagesStep;
  private int floodPrevention;
  private int pageCount;

  public StreamingRenderer( final OutputProcessor outputProcessor ) {
    super( outputProcessor );
    this.countBoxesStep = new CountBoxesStep();
    this.cleanBoxesStep = new CleanFlowBoxesStep();
    this.applyAutoCommitPageHeaderStep = new ApplyAutoCommitPageHeaderStep();
    this.fillPhysicalPagesStep = new FillFlowPagesStep();

    initialize();
  }

  protected boolean isPageFinished() {
    if ( getPageBox().isOpen() ) {
      return false;
    }
    return true;
  }

  public void startReport( final ReportDefinition report, final ProcessingContext processingContext,
      final PerformanceMonitorContext performanceMonitorContext ) {
    pageCount = 0;
    super.startReport( report, processingContext, performanceMonitorContext );
  }

  public void processIncrementalUpdate( final boolean performOutput ) throws ContentProcessingException {

    if ( isDirty() == false ) {
      // Log.debug ("Not dirty, no update needed.");
      return;
    }
    clearDirty();

    floodPrevention += 1;
    if ( floodPrevention < 50 ) { // this is a magic number ..
      return;
    }
    floodPrevention = 0;

    final OutputProcessor outputProcessor = getOutputProcessor();
    if ( outputProcessor instanceof IterativeOutputProcessor == false
        || outputProcessor.getMetaData().isFeatureSupported( OutputProcessorFeature.ITERATIVE_RENDERING ) == false ) {
      // logger.debug ("No incremental system.");
      return;
    }

    // logger.debug("Computing Incremental update.");

    final LogicalPageBox pageBox = getPageBox();
    pageBox.setPageOffset( 0 );
    pageBox.setPageEnd( pageBox.getHeight() );
    // shiftBox(pageBox, true);

    if ( pageBox.isOpen() ) {
      final IterativeOutputProcessor io = (IterativeOutputProcessor) outputProcessor;
      if ( applyAutoCommitPageHeaderStep.compute( pageBox ) ) {
        // logger.debug("Applying Incremental update.");
        io.processIterativeContent( pageBox, performOutput );
        countBoxesStep.process( pageBox );
        cleanBoxesStep.compute( pageBox );
      }
    }
  }

  protected boolean performPagination( final LayoutPagebreakHandler handler, final boolean performOutput )
    throws ContentProcessingException {
    if ( performOutput == false ) {
      return false;
    }

    final OutputProcessor outputProcessor = getOutputProcessor();
    final LogicalPageBox pageBox = getPageBox();

    // This is fixed: The streaming renderers always use the whole page area ..
    pageBox.setPageOffset( 0 );
    pageBox.setPageEnd( pageBox.getHeight() );

    if ( pageBox.isOpen() ) {
      // Not finished and the output target is non-iterative, so we have to wait until everything is done..
      return false;
    }

    // the reporting finally came to an end. Lets process the content.
    // Recover the page-grid, then restart everything from scratch.
    // (We have to recompute, as the pages may be different now, due to changed margins or page definitions)
    final long nextOffset = pageBox.computePageEnd();
    pageBox.setPageEnd( nextOffset );
    final long pageOffset = pageBox.getPageOffset();

    applyAutoCommitPageHeaderStep.commitAll( pageBox );
    if ( outputProcessor.isNeedAlignedPage() ) {
      final LogicalPageBox box = fillPhysicalPagesStep.compute( pageBox, pageOffset, nextOffset );
      logger.debug( "Processing contents for stream. Page-Offset: " + pageOffset + " -> " + nextOffset );

      outputProcessor.processContent( box );
    } else {
      logger.debug( "Processing fast contents for stream. Page-Offset: " + pageOffset + " -> " + nextOffset );
      outputProcessor.processContent( pageBox );
    }

    countBoxesStep.process( pageBox );
    cleanBoxesStep.compute( pageBox );
    debugPrint( pageBox );
    outputProcessor.processingFinished();

    pageCount = 1;
    setPagebreaks( 1 );
    return false;
  }

  public int getPageCount() {
    return pageCount;
  }

  protected void debugPrint( final LogicalPageBox pageBox ) {
    // Log.debug("**** Start Printing Page: " + 1);
    // ModelPrinter.print(pageBox);
    // Log.debug("**** Done  Printing Page: " + 1);
  }

  public void createRollbackInformation() {
    throw new UnsupportedOperationException(
        "Streaming-Renderer do not implement the createRollbackInformation-method." );
  }

  public void applyRollbackInformation() {
    throw new UnsupportedOperationException( "Streaming-Renderer do not implement the applyRollbackInformation method." );
  }

  public void rollback() {
    throw new UnsupportedOperationException( "Streaming-Renderer do not implement the rollback method." );
  }
}
