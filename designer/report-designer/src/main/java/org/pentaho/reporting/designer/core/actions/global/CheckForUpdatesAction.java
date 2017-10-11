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
import org.pentaho.reporting.designer.core.versionchecker.VersionCheckerUtility;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public final class CheckForUpdatesAction extends AbstractDesignerContextAction {
  public CheckForUpdatesAction() {
    putValue( Action.NAME, ActionMessages.getString( "CheckForUpdatesAction.Text" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "CheckForUpdatesAction.Mnemonic" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "CheckForUpdatesAction.Description" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "CheckForUpdatesAction.Accelerator" ) );

    try {
      Class.forName( "org.pentaho.versionchecker.VersionChecker" );
      setEnabled( true );
    } catch ( Throwable t ) {
      // if we do not have the version checker, fail without any user feedback or logging
      setEnabled( false );
    }
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    try {
      Class.forName( "org.pentaho.versionchecker.VersionChecker" );
      VersionCheckerUtility.checkVersion( getReportDesignerContext().getView().getParent(), true, false );
    } catch ( Throwable t ) {
      // if we do not have the version checker, fail without any user feedback or logging
    }

  }
}
