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
import org.pentaho.reporting.designer.core.util.IconLoader;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public final class ToggleFieldSelectorAction extends AbstractDesignerContextAction
  implements ToggleStateAction, SettingsListener {
  public ToggleFieldSelectorAction() {
    putValue( Action.NAME, ActionMessages.getString( ( "ToggleFieldSelectorAction.Text" ) ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "ToggleFieldSelectorAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "ToggleFieldSelectorAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "ToggleFieldSelectorAction.Accelerator" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getPropertiesDataSetIcon() );

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
    putValue( Action.SELECTED_KEY, WorkspaceSettings.getInstance().isFieldSelectorVisible() );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final boolean snap = WorkspaceSettings.getInstance().isFieldSelectorVisible();
    WorkspaceSettings.getInstance().setFieldSelectorVisible( !snap );
    getReportDesignerContext().getView().setFieldSelectorVisible( !snap );
  }
}
