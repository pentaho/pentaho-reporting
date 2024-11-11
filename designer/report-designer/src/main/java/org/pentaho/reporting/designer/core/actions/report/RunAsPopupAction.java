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


package org.pentaho.reporting.designer.core.actions.report;

import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.util.IconLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Todo: Document Me
 *
 * @author Ezequiel Cuellar
 */
public final class RunAsPopupAction extends AbstractReportContextAction {
  public RunAsPopupAction() {
    putValue( Action.NAME, ActionMessages.getString( "RunAsPopupAction.Text" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getCreateReportIcon() );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "RunAsPopupAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "RunAsPopupAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "RunAsPopupAction.Accelerator" ) );
  }

  public void actionPerformed( final ActionEvent e ) {
    final JPopupMenu menu = getReportDesignerContext().getPopupMenu( "popup-RunAs" ); // $NON-NLS$
    final Object source = e.getSource();
    if ( source instanceof Component ) {
      final Component c = (Component) source;
      menu.show( c, 0, c.getHeight() );
    } else {
      final Component parent = getReportDesignerContext().getView().getParent();
      menu.show( parent, 0, 0 );
    }
  }
}
