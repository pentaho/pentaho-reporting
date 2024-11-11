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

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.ReportDesignerView;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.actions.ToggleStateAction;
import org.pentaho.reporting.designer.core.util.IconLoader;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Todo: Document Me
 *
 * @author Ezequiel Cuellar
 */
public final class TogglePropertiesToolWindowAction extends AbstractToolWindowStateAction implements ToggleStateAction {
  private boolean selected;

  public TogglePropertiesToolWindowAction() {
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getGenericSquareDisabled() );
    putValue( Action.NAME, ActionMessages.getString( "TogglePropertiesToolWindowAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "TogglePropertiesToolWindowAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "TogglePropertiesToolWindowAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY,
      ActionMessages.getOptionalKeyStroke( "TogglePropertiesToolWindowAction.Accelerator" ) );
    putValue( Action.SELECTED_KEY, Boolean.FALSE );
  }

  public boolean isSelected() {
    return Boolean.TRUE.equals( getValue( Action.SELECTED_KEY ) );
  }

  public void setSelected( final boolean selected ) {
    putValue( Action.SELECTED_KEY, selected );
  }

  public void actionPerformed( final ActionEvent e ) {
    final ReportDesignerContext reportDesignerContext1 = getReportDesignerContext();
    if ( reportDesignerContext1 == null ) {
      return;
    }
    reportDesignerContext1.getView().setPropertiesEditorVisible(
      reportDesignerContext1.getView().isPropertiesEditorVisible() == false );
  }

  protected String getPropertyName() {
    return ReportDesignerView.STRUCTURE_VISIBLE_PROPERTY;
  }

  protected boolean recomputeEnabled() {
    final ReportDesignerContext reportDesignerContext1 = getReportDesignerContext();
    if ( reportDesignerContext1 == null ) {
      return false;
    }
    setSelected( true );
    return true;
  }
}
