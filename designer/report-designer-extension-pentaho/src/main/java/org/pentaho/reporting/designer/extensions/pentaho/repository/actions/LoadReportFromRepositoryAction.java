/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.designer.extensions.pentaho.repository.actions;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;

import java.awt.event.ActionEvent;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.Action;
import javax.swing.SwingUtilities;

public final class LoadReportFromRepositoryAction extends AbstractDesignerContextAction {

  @FunctionalInterface
  public interface OpenTaskFactory {
    Runnable create( ReportDesignerContext context );
  }

  private static final AtomicReference<OpenTaskFactory> openTaskFactory = new AtomicReference<>();

  public static void setOpenTaskFactory( final OpenTaskFactory factory ) {
    openTaskFactory.set( factory );
  }

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
    if ( reportDesignerContext == null ) {
      return;
    }

    // Route through EE tasks when configured; otherwise use the standard flow as in CE
    final OpenTaskFactory factory = openTaskFactory.get();
    if ( factory != null ) {
      final Runnable eeTask = factory.create( reportDesignerContext );
      if ( eeTask != null ) {
        SwingUtilities.invokeLater( eeTask );
        return;
      }
    }

    final OpenFileFromRepositoryTask openFileFromRepositoryTask =
        new OpenFileFromRepositoryTask( reportDesignerContext, reportDesignerContext.getView().getParent() );

    final LoginTask loginTask =
        new LoginTask( reportDesignerContext, reportDesignerContext.getView().getParent(),
            openFileFromRepositoryTask, null, false );
    SwingUtilities.invokeLater( loginTask );
  }
}
