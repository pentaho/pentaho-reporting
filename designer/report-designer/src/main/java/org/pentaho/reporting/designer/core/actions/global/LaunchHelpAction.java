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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.util.ExternalToolLauncher;
import org.pentaho.reporting.libraries.base.config.Configuration;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public final class LaunchHelpAction extends AbstractDesignerContextAction {
  private static final Log log = LogFactory.getLog( LaunchHelpAction.class );

  public LaunchHelpAction() {
    putValue( Action.NAME, ActionMessages.getString( "LaunchHelpAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "LaunchHelpAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "LaunchHelpAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "LaunchHelpAction.Accelerator" ) );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final Configuration config = ReportDesignerBoot.getInstance().getGlobalConfig();
    final String docUrl =
      config.getConfigProperty( "org.pentaho.reporting.designer.core.documentation.report_designer_user_guide" );

    try {
      ExternalToolLauncher.openURL( docUrl );
    } catch ( IOException ex ) {
      log.warn( "Could not load " + docUrl, ex ); // NON-NLS
    }
  }
}
