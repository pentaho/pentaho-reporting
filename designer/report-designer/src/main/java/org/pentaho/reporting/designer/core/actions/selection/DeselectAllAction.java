/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
