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


package org.pentaho.reporting.designer.core.editor.preview;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.global.ShowPreviewPaneAction;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewPane;
import org.pentaho.reporting.engine.classic.core.util.Worker;
import org.pentaho.reporting.libraries.base.config.Configuration;

import javax.swing.*;

public class DesignerPreviewPane extends PreviewPane {
  private Action[] previewPaneActions;
  private ReportDesignerContext context;

  public DesignerPreviewPane( final ReportDesignerContext context ) {
    super( false );
    if ( context == null ) {
      throw new NullPointerException();
    }


    this.context = context;
    setToolbarFloatable( false );

    // force a reinit; a true one this time.
    initializeWithoutJob();
  }

  protected Worker createWorker() {
    final Worker worker = super.createWorker();
    worker.setPriority( Thread.MIN_PRIORITY );
    return worker;
  }

  protected Action[] getToolbarPreActions() {
    if ( previewPaneActions == null ) {
      final ShowPreviewPaneAction action = new ShowPreviewPaneAction();
      action.setReportDesignerContext( context );
      previewPaneActions = new Action[] { action };
    }
    return previewPaneActions.clone();
  }

  protected Configuration computeContextConfiguration() {
    return ClassicEngineBoot.getInstance().getGlobalConfig();
  }

  public void setStatusText( final String statusText ) {
    super.setStatusText( statusText );
    context.setStatusText( statusText );
  }

  public void setNumberOfPages( final int numberOfPages ) {
    super.setNumberOfPages( numberOfPages );
    context.setPageNumbers( getPageNumber(), getNumberOfPages() );
  }

  public void setPageNumber( final int pageNumber ) {
    super.setPageNumber( pageNumber );
    context.setPageNumbers( getPageNumber(), getNumberOfPages() );
  }
}
