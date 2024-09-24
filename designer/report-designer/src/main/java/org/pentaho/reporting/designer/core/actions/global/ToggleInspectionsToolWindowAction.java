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

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.ReportDesignerView;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.actions.ToggleStateAction;
import org.pentaho.reporting.designer.core.util.IconLoader;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Michael D'Amour
 */
public final class ToggleInspectionsToolWindowAction extends AbstractToolWindowStateAction
  implements ToggleStateAction {
  public ToggleInspectionsToolWindowAction() {
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getMessagesIcon() );
    putValue( Action.NAME, ActionMessages.getString( "ToggleInspectionsToolWindowAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "ToggleInspectionsToolWindowAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "ToggleInspectionsToolWindowAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY,
      ActionMessages.getOptionalKeyStroke( "ToggleInspectionsToolWindowAction.Accelerator" ) );
    putValue( Action.SELECTED_KEY, Boolean.FALSE );
  }

  public boolean isSelected() {
    return Boolean.TRUE.equals( getValue( Action.SELECTED_KEY ) );
  }

  public void setSelected( final boolean selected ) {
    putValue( Action.SELECTED_KEY, selected );
  }

  public void actionPerformed( final ActionEvent e ) {
    final ReportDesignerContext reportDesignerContext = getReportDesignerContext();
    if ( reportDesignerContext == null ) {
      return;
    }
    reportDesignerContext.getView().setMessagesVisible( reportDesignerContext.getView().isMessagesVisible() == false );
  }

  protected String getPropertyName() {
    return ReportDesignerView.MESSAGES_VISIBLE_PROPERTY;
  }

  protected boolean recomputeEnabled() {
    final ReportDesignerContext reportDesignerContext = getReportDesignerContext();
    if ( reportDesignerContext == null ) {
      return false;
    }
    setSelected( reportDesignerContext.getView().isMessagesVisible() );
    return true;
  }
}
