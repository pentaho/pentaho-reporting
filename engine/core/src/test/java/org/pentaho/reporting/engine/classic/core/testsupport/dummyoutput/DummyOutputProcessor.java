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

package org.pentaho.reporting.engine.classic.core.testsupport.dummyoutput;

import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.GenericOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;

public class DummyOutputProcessor implements OutputProcessor {
  private OutputProcessorMetaData metaData;
  private boolean finishedPage;

  public DummyOutputProcessor() {
    metaData = new GenericOutputProcessorMetaData();
  }

  public void processingStarted( final ReportDefinition report, final ProcessingContext processingContext ) {
  }

  public OutputProcessorMetaData getMetaData() {
    return metaData;
  }

  public void processContent( final LogicalPageBox pageBox ) throws ContentProcessingException {
  }

  public void processRecomputedContent( final LogicalPageBox pageBox ) throws ContentProcessingException {

  }

  public void processingFinished() {
    finishedPage = true;
  }

  public int getPageCursor() {
    return 0;
  }

  public void setPageCursor( final int pc ) {

  }

  public int getLogicalPageCount() {
    return finishedPage ? 1 : 0;
  }

  public LogicalPageKey getLogicalPage( final int page ) {
    return null;
  }

  public boolean isPaginationFinished() {
    return false;
  }

  public boolean isNeedAlignedPage() {
    return false;
  }

  public int getPhysicalPageCount() {
    return finishedPage ? 1 : 0;
  }
}
