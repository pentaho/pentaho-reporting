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


package org.pentaho.reporting.engine.classic.core.layout.output;

import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;

/**
 * The output-processor receives the layouted content and is responsible for translating the received content into the
 * specific output format. The output processor also keeps track of the number of received pages and their physical
 * layout.
 *
 * @author Thomas Morgner
 */
public interface OutputProcessor {
  public void processingStarted( final ReportDefinition report, final ProcessingContext processingContext );

  public OutputProcessorMetaData getMetaData();

  /**
   * A call-back that passes a layouted pagebox to the output processor.
   *
   * @param pageBox
   */
  public void processContent( final LogicalPageBox pageBox ) throws ContentProcessingException;

  public void processRecomputedContent( final LogicalPageBox pageBox ) throws ContentProcessingException;

  /**
   * A call-back to indicate that the processing of the current process-run has been finished.
   */
  public void processingFinished();

  public int getPageCursor();

  public void setPageCursor( int pc );

  public int getLogicalPageCount();

  public LogicalPageKey getLogicalPage( int page );

  /**
   * Checks whether the 'processingFinished' event had been received at least once.
   *
   * @return
   */
  public boolean isPaginationFinished();

  public boolean isNeedAlignedPage();

  public int getPhysicalPageCount();
}
