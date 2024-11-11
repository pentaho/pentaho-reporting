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
public class NextTabAction extends AbstractDesignerContextAction {
  /**
   * Defines an <code>Action</code> object with a default description string and default icon.
   */
  public NextTabAction() {
    putValue( Action.NAME, ActionMessages.getString( "NextTabAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "NextTabAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "NextTabAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "NextTabAction.Accelerator" ) );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final ReportDesignerContext context = getReportDesignerContext();
    final int index = context.findActiveContextIndex();
    if ( index != -1 && index != context.getReportRenderContextCount() - 1 ) {
      context.setActiveDocument( context.getReportRenderContext( index + 1 ) );
    }
  }
}
