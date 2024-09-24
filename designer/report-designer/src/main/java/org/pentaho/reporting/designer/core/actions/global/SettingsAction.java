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
