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

package org.pentaho.reporting.designer.core.actions.global.units;

import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.actions.ToggleStateAction;
import org.pentaho.reporting.designer.core.settings.SettingsListener;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.Unit;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public abstract class SetUnitAction extends AbstractDesignerContextAction
  implements ToggleStateAction, SettingsListener {
  private Unit unit;

  protected SetUnitAction( final Unit unit ) {
    if ( unit == null ) {
      throw new NullPointerException();
    }

    this.unit = unit;
    putValue( Action.NAME, ActionMessages.getString( "SetUnitAction.Text", Integer.valueOf( unit.ordinal() ) ) );
    putValue( Action.SHORT_DESCRIPTION,
      ActionMessages.getString( "SetUnitAction.Description", Integer.valueOf( unit.ordinal() ) ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "SetUnitAction.Text" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "SetUnitAction.Accelerator" ) );

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
    putValue( Action.SELECTED_KEY, ObjectUtilities.equal( unit, WorkspaceSettings.getInstance().getUnit() ) );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    WorkspaceSettings.getInstance().setUnit( unit );
  }
}
