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

import org.pentaho.reporting.engine.classic.core.layout.RenderComponentFactory;
import org.pentaho.reporting.engine.classic.core.layout.StreamingRenderer;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;

public class DesignerRenderer extends StreamingRenderer {
  private DesignerRenderComponentFactory designerRenderComponentFactory;

  public DesignerRenderer( final DesignerOutputProcessor outputProcessor,
                           final DesignerRenderComponentFactory designerRenderComponentFactory ) {
    super( outputProcessor );
    this.designerRenderComponentFactory = designerRenderComponentFactory;
  }

  protected RenderComponentFactory createComponentFactory() {
    if ( designerRenderComponentFactory == null ) {
      designerRenderComponentFactory = new DesignerRenderComponentFactory( getMetaData() );
    }
    return designerRenderComponentFactory;
  }

  public LogicalPageBox getPageBox() {
    return super.getPageBox();
  }

  /**
   * Override so that we do not perform any intermediate layouting. This speeds up the layout process for complex
   * reports. We do a single, complete layout run at the end of the report processing.
   *
   * @return the layout result.
   * @throws ContentProcessingException
   */
  public LayoutResult validatePages() throws ContentProcessingException {
    final LogicalPageBox pageBox = getPageBox();
    if ( pageBox == null ) {
      return LayoutResult.LAYOUT_UNVALIDATABLE;
    }

    if ( pageBox.isOpen() ) {
      return LayoutResult.LAYOUT_UNVALIDATABLE;
    }

    return super.validatePages();
  }

  public void createRollbackInformation() {
    // intentionally a No-Op
  }

  public void applyRollbackInformation() {
    // intentionally a No-Op
  }

  public void rollback() {
    // intentionally a No-Op
  }
}
