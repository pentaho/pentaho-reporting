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


package org.pentaho.reporting.designer.core.editor.report.layouting;

import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractOutputProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;

public class DesignerOutputProcessor extends AbstractOutputProcessor {
  private OutputProcessorMetaData metadata;
  private LogicalPageBox logicalPage;

  public DesignerOutputProcessor() {
    this( new DesignerOutputProcessorMetaData() );
  }

  public DesignerOutputProcessor( final DesignerOutputProcessorMetaData metaData ) {
    if ( metaData == null ) {
      throw new NullPointerException();
    }
    this.metadata = metaData;
  }

  public boolean isNeedAlignedPage() {
    // this guarantees that we get a copy of the logical page. 
    return true;
  }

  protected void processPageContent( final LogicalPageKey logicalPageKey,
                                     final LogicalPageBox logicalPage ) throws ContentProcessingException {
    this.logicalPage = logicalPage;
  }

  public OutputProcessorMetaData getMetaData() {
    return metadata;
  }

  public LogicalPageBox getLogicalPage() {
    return logicalPage;
  }
}
