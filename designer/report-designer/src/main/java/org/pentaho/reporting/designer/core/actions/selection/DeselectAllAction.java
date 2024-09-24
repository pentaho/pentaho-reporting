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

package org.pentaho.reporting.designer.core.actions.selection;

import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.util.IconLoader;

import javax.swing.*;
import javax.swing.FocusManager;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public final class DeselectAllAction extends AbstractReportContextAction {
  public DeselectAllAction() {
    putValue( Action.NAME, ActionMessages.getString( "DeselectAllAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "DeselectAllAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "DeselectAllAction.Mnemonic" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getDeselectAllIcon() );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "DeselectAllAction.Accelerator" ) );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    // this depends on who has the focus right now.
    final Component owner = FocusManager.getCurrentManager().getPermanentFocusOwner();
    if ( owner instanceof JTextComponent ) {
      final JTextComponent textComponent = (JTextComponent) owner;
      textComponent.select( textComponent.getCaretPosition(), textComponent.getCaretPosition() );
    } else if ( owner instanceof TextComponent ) {
      final TextComponent textComponent = (TextComponent) owner;
      textComponent.select( textComponent.getCaretPosition(), textComponent.getCaretPosition() );
    } else if ( owner instanceof JTable ) {
      final JTable table = (JTable) owner;
      table.clearSelection();
    } else if ( owner instanceof JList ) {
      final JList list = (JList) owner;
      list.getSelectionModel().clearSelection();
      list.repaint();
    } else if ( owner instanceof JTree ) {
      final JTree list = (JTree) owner;
      list.clearSelection();
      list.repaint();
    } else {
      final ReportDocumentContext activeContext = getActiveContext();
      if ( activeContext != null ) {
        activeContext.getSelectionModel().clearSelection();
      }
    }
  }
}
