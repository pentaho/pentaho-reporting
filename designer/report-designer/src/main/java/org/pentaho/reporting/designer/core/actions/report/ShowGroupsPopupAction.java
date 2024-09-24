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

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.util.IconLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public final class ShowGroupsPopupAction extends AbstractReportContextAction {
  public ShowGroupsPopupAction() {
    putValue( Action.NAME, ActionMessages.getString( "ShowGroupsPopupAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "ShowGroupsPopupAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "ShowGroupsPopupAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "ShowGroupsPopupAction.Accelerator" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getGroupIcon() );
  }

  public void actionPerformed( final ActionEvent e ) {
    final ReportDesignerContext reportDesignerContext1 = getReportDesignerContext();
    if ( reportDesignerContext1 == null ) {
      return;
    }

    final JPopupMenu menu = reportDesignerContext1.getPopupMenu( "popup-Groups" );// $NON-NLS$
    final Object source = e.getSource();
    if ( source instanceof Component ) {
      final Component c = (Component) source;
      menu.show( c, 0, c.getHeight() );
    } else {
      final Component parent = reportDesignerContext1.getView().getParent();
      menu.show( parent, 0, 0 );
    }
  }
}
