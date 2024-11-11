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
import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class PreviousTabAction extends AbstractDesignerContextAction {
  /**
   * Defines an <code>Action</code> object with a default descriptionHe string and default icon.
   */
  public PreviousTabAction() {
    putValue( Action.NAME, ActionMessages.getString( "PreviousTabAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "PreviousTabAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "PreviousTabAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "PreviousTabAction.Accelerator" ) );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final ReportDesignerContext context = getReportDesignerContext();
    final int index = context.findActiveContextIndex();
    if ( index > 0 ) {
      context.setActiveDocument( context.getReportRenderContext( index - 1 ) );
    }
  }
}

