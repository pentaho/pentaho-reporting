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
import org.pentaho.reporting.designer.core.actions.ActionMessages;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Todo: Document Me
 *
 * @author Michael D'Amour
 */
public final class ShowWelcomePaneAction extends AbstractViewStateAction {
  public ShowWelcomePaneAction() {
    putValue( Action.NAME, ActionMessages.getString( "ShowWelcomePaneAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "ShowWelcomePaneAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "ShowWelcomePaneAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "ShowWelcomePaneAction.Accelerator" ) );
  }

  public void actionPerformed( final ActionEvent e ) {
    final ReportDesignerContext context = getReportDesignerContext();
    context.getView().setWelcomeVisible( true );
  }

  protected boolean recomputeEnabled() {
    final ReportDesignerContext context = getReportDesignerContext();
    if ( context == null ) {
      return false;
    }
    return true;
  }
}
