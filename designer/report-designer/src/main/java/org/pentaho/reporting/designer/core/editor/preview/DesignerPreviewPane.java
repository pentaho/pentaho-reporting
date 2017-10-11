/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
