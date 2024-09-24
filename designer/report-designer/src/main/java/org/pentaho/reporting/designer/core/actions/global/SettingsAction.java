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
import org.pentaho.reporting.designer.core.settings.ui.SettingsDialog;
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
public final class SettingsAction extends AbstractDesignerContextAction {
  public SettingsAction() {
    putValue( Action.NAME, ActionMessages.getString( "SettingsAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "SettingsAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "SettingsAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "SettingsAction.Accelerator" ) );
    putValue( SMALL_ICON, IconLoader.getInstance().getSettingsIcon() );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    if ( e instanceof ConsumableActionEvent ) {
      final ConsumableActionEvent ce = (ConsumableActionEvent) e;
      ce.consume();
    }

    final Window window = LibSwingUtil.getWindowAncestor( getReportDesignerContext().getView().getParent() );
    final SettingsDialog settingsDialog;
    if ( window instanceof Frame ) {
      settingsDialog = new SettingsDialog( (Frame) window );
    } else if ( window instanceof Dialog ) {
      settingsDialog = new SettingsDialog( (Dialog) window );
    } else {
      settingsDialog = new SettingsDialog();
    }

    settingsDialog.pack();
    settingsDialog.performEdit( getReportDesignerContext() );
  }
}
