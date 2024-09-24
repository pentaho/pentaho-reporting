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

package org.pentaho.reporting.designer.extensions.pentaho.repository.actions;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.SwingUtilities;

public final class LoadReportFromRepositoryAction extends AbstractDesignerContextAction {
  public LoadReportFromRepositoryAction() {
    putValue( Action.NAME, Messages.getInstance().getString( "LoadReportFromRepositoryAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, Messages.getInstance().getString( "LoadReportFromRepositoryAction.Description" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getOpenIcon() );
    putValue( Action.ACCELERATOR_KEY, Messages.getInstance().getOptionalKeyStroke(
        "LoadReportFromRepositoryAction.Accelerator" ) );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final ReportDesignerContext reportDesignerContext = getReportDesignerContext();
    final OpenFileFromRepositoryTask openFileFromRepositoryTask =
        new OpenFileFromRepositoryTask( reportDesignerContext, reportDesignerContext.getView().getParent() );
    final LoginTask loginTask =
        new LoginTask( reportDesignerContext, reportDesignerContext.getView().getParent(), openFileFromRepositoryTask );

    SwingUtilities.invokeLater( loginTask );
  }
}
