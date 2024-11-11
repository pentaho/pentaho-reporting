/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
