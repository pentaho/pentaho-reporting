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

import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.actions.ToggleStateAction;
import org.pentaho.reporting.designer.core.settings.SettingsListener;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public final class SnapToElementsAction extends AbstractDesignerContextAction
  implements ToggleStateAction, SettingsListener {
  public SnapToElementsAction() {
    putValue( Action.NAME, ActionMessages.getString( "SnapToElementsAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "SnapToElementsAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "SnapToElementsAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "SnapToElementsAction.Accelerator" ) );

    WorkspaceSettings.getInstance().addSettingsListener( this );
    settingsChanged();
  }

  public boolean isSelected() {
    return Boolean.TRUE.equals( getValue( Action.SELECTED_KEY ) );
  }

  public void setSelected( final boolean selected ) {
    putValue( Action.SELECTED_KEY, selected );
  }

  public void settingsChanged() {
    putValue( Action.SELECTED_KEY, WorkspaceSettings.getInstance().isSnapToElements() );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final WorkspaceSettings applicationSettings = WorkspaceSettings.getInstance();
    applicationSettings.setSnapToElements( !applicationSettings.isSnapToElements() );
  }
}
