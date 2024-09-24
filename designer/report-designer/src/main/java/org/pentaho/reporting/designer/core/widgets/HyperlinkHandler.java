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

package org.pentaho.reporting.designer.core.widgets;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.util.ExternalToolLauncher;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExceptionDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class HyperlinkHandler extends MouseAdapter {
  private String url;
  private Component parent;

  public HyperlinkHandler( final String url, final Component parent ) {
    this.url = url;
    this.parent = parent;
  }

  public void mouseClicked( final MouseEvent e ) {
    try {
      ExternalToolLauncher.openURL( url );
    } catch ( IOException ioe ) {
      SwingUtilities.invokeLater( new ShowErrorMessageTask( parent, ioe ) );
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
        Messages.getString( "HyperlinkHandler.Error.Title" ),
        Messages.getString( "HyperlinkHandler.errBrowser", exception.getLocalizedMessage() ),
        exception );
    }
  }

}
