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
import org.pentaho.reporting.designer.core.editor.report.RootBandRenderComponent;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.Section;

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
public final class SelectAllAction extends AbstractReportContextAction {
  public SelectAllAction() {
    putValue( Action.NAME, ActionMessages.getString( "SelectAllAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "SelectAllAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "SelectAllAction.Text" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getSelectAllIcon() );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "SelectAllAction.Accelerator" ) );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    // this depends on who has the focus right now.
    final Component owner = FocusManager.getCurrentManager().getPermanentFocusOwner();
    if ( owner instanceof JTextComponent ) {
      final JTextComponent textComponent = (JTextComponent) owner;
      textComponent.selectAll();
    } else if ( owner instanceof TextComponent ) {
      final TextComponent textComponent = (TextComponent) owner;
      textComponent.selectAll();
    } else if ( owner instanceof JTable ) {
      final JTable table = (JTable) owner;
      table.selectAll();
    } else if ( owner instanceof JList ) {
      final JList list = (JList) owner;
      list.getSelectionModel().setSelectionInterval( 0, list.getModel().getSize() );
      list.repaint();
    } else if ( owner instanceof JTree ) {
      final JTree list = (JTree) owner;
      final int[] indices = new int[ list.getRowCount() ];
      for ( int i = 0; i < indices.length; i++ ) {
        indices[ i ] = i;
      }
      list.setSelectionRows( indices );
      list.repaint();
    } else if ( owner instanceof RootBandRenderComponent ) {
      final RootBandRenderComponent rc = (RootBandRenderComponent) owner;
      final Section reportElement = rc.getRendererRoot().getElement();
      final DocumentContextSelectionModel selectionModel = getActiveContext().getSelectionModel();
      selectRecursively( selectionModel, reportElement );
      if ( reportElement instanceof RootLevelBand ) {
        final RootLevelBand re = (RootLevelBand) reportElement;
        final int count = re.getSubReportCount();
        for ( int i = 0; i < count; i++ ) {
          selectionModel.add( re.getSubReport( i ) );
        }
      }
    }
  }

  private void selectRecursively( final DocumentContextSelectionModel selectionModel, final Section band ) {
    if ( band instanceof RootLevelBand == false ) {
      selectionModel.add( band );
    }
    final int count = band.getElementCount();
    for ( int i = 0; i < count; i++ ) {
      final Element element = band.getElement( i );
      if ( element instanceof Band ) {
        selectRecursively( selectionModel, (Band) element );
      } else {
        selectionModel.add( element );
      }
    }
  }
}
