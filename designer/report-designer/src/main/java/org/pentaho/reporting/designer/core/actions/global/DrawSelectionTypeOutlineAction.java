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
import org.pentaho.reporting.designer.core.actions.ToggleStateAction;
import org.pentaho.reporting.designer.core.settings.SettingsListener;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.DrawSelectionType;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public final class DrawSelectionTypeOutlineAction extends AbstractDesignerContextAction
  implements ToggleStateAction, SettingsListener {
  public DrawSelectionTypeOutlineAction() {
    putValue( Action.NAME, ActionMessages.getString( "DrawSelectionTypeOutlineAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "DrawSelectionTypeOutlineAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "DrawSelectionTypeOutlineAction.Mnemonic" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getDrawSelectionTypeOutlineIcon() );
    putValue( Action.ACCELERATOR_KEY,
      ActionMessages.getOptionalKeyStroke( "DrawSelectionTypeOutlineAction.Accelerator" ) );

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
    putValue( Action.SELECTED_KEY, ObjectUtilities.equal
      ( DrawSelectionType.OUTLINE, WorkspaceSettings.getInstance().getDrawSelectionType() ) );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    WorkspaceSettings.getInstance().setDrawSelectionType( DrawSelectionType.OUTLINE );
  }
}
