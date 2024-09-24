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

package org.pentaho.reporting.designer.core.actions.global;

import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.util.ExternalToolLauncher;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExceptionDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public final class VisitForumAction extends AbstractDesignerContextAction {
  private static class LaunchBrowserTask implements Runnable {
    private Component parent;

    private LaunchBrowserTask( final Component parent ) {
      this.parent = parent;
    }

    public void run() {
      try {
        final String url = ActionMessages.getString( "VisitForumAction.URL" );//NON-NLS
        ExternalToolLauncher.openURL( url );
      } catch ( final IOException e ) {
        EventQueue.invokeLater( new ShowErrorMessageTask( parent, e ) );
      }
    }
  }


  private static class ShowErrorMessageTask implements Runnable {
    private Component parent;
    private Exception exception;

    private ShowErrorMessageTask( final Component parent,
                                  final Exception exception ) {
      this.parent = parent;
      this.exception = exception;
    }

    public void run() {
      ExceptionDialog.showExceptionDialog( parent,
        ActionMessages.getString( "VisitForumAction.Error.Title" ),
        ActionMessages.getString( "VisitForumAction.Error.Message" ),
        exception );
    }
  }

  public VisitForumAction() {
    putValue( Action.NAME, ActionMessages.getString( "VisitForumAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "VisitForumAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "VisitForumAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "VisitForumAction.Accelerator" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getVisitOnlineForumIcon() );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final Thread t = new Thread( new LaunchBrowserTask( getReportDesignerContext().getView().getParent() ) );
    t.setDaemon( true );
    t.start();

  }
}
