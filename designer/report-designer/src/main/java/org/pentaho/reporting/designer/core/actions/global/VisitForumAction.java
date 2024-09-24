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
