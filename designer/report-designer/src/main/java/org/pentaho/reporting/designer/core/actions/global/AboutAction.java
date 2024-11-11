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
import org.pentaho.reporting.designer.core.util.AboutDialog;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.libraries.designtime.swing.ConsumableActionEvent;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public final class AboutAction extends AbstractDesignerContextAction {
  public AboutAction() {
    putValue( Action.NAME, ActionMessages.getString( "AboutAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "AboutAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "AboutAction.Mnemonic" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getAboutIcon() );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "AboutAction.Accelerator" ) );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final Component parent = getReportDesignerContext().getView().getParent();
    final Window window = LibSwingUtil.getWindowAncestor( parent );
    if ( window instanceof JDialog ) {
      new AboutDialog( (JDialog) window );
    } else if ( window instanceof JFrame ) {
      new AboutDialog( (JFrame) window );
    } else {
      new AboutDialog();
    }

    if ( e instanceof ConsumableActionEvent ) {
      final ConsumableActionEvent ce = (ConsumableActionEvent) e;
      ce.consume();
    }

  }
}
