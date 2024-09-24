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
import org.pentaho.reporting.designer.core.util.AboutDialog;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.libraries.designtime.swing.ConsumableActionEvent;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public final class AboutAction extends AbstractDesignerContextAction {
  public AboutAction() {
    putValue( Action.NAME, ActionMessages.getString( "AboutAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "AboutAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "AboutAction.Mnemonic" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getAboutIcon() );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "AboutAction.Accelerator" ) );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final Component parent = getReportDesignerContext().getView().getParent();
    final Window window = LibSwingUtil.getWindowAncestor( parent );
    if ( window instanceof JDialog ) {
      new AboutDialog( (JDialog) window );
    } else if ( window instanceof JFrame ) {
      new AboutDialog( (JFrame) window );
    } else {
      new AboutDialog();
    }

    if ( e instanceof ConsumableActionEvent ) {
      final ConsumableActionEvent ce = (ConsumableActionEvent) e;
      ce.consume();
    }

  }
}
