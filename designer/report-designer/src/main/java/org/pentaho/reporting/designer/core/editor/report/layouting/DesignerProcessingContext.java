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

package org.pentaho.reporting.designer.core.editor.report.layouting;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultProcessingContext;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;

public class DesignerProcessingContext extends DefaultProcessingContext {
  private ResourceKey contentBase;

  public DesignerProcessingContext( final MasterReport masterReport ) throws ReportProcessingException {
    super( masterReport );
    setOutputProcessorMetaData( new DesignerOutputProcessorMetaData() );
  }

  /**
   * This constructor exists for test-case use only. If you use this to process a real report, most of the settings of
   * the report will be ignored and your export will not work as expected.
   */
  public DesignerProcessingContext() {
    setOutputProcessorMetaData( new DesignerOutputProcessorMetaData() );
  }

  public ResourceKey getContentBase() {
    return contentBase;
  }

  public void setContentBase( final ResourceKey contentBase ) {
    this.contentBase = contentBase;
  }
}
