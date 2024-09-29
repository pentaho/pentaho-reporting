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


package org.pentaho.reporting.designer.core.actions.elements;

import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.format.ConditionalVisibilityDialog;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class AddConditionalVisibilityAction extends AbstractElementSelectionAction {
  public AddConditionalVisibilityAction() {
    putValue( Action.NAME, ActionMessages.getString( "AddConditionalVisibilityAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "AddConditionalVisibilityAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "AddConditionalVisibilityAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY,
      ActionMessages.getOptionalKeyStroke( "AddConditionalVisibilityAction.Accelerator" ) );
  }

  protected void selectedElementPropertiesChanged( final ReportModelEvent event ) {
  }

  protected void updateSelection() {
    if ( isSingleElementSelection() == false ) {
      setEnabled( false );
    } else {
      setEnabled( getSelectionModel().getSelectedElement( 0 ) instanceof Element );
    }
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final DocumentContextSelectionModel selectionModel1 = getSelectionModel();
    if ( selectionModel1 == null ) {
      return;
    }

    final List<Element> visualElements = selectionModel1.getSelectedElementsOfType( Element.class );
    if ( visualElements.size() != 1 ) {
      return;
    }


    final Component parent = getReportDesignerContext().getView().getParent();
    final Window window = LibSwingUtil.getWindowAncestor( parent );
    final ConditionalVisibilityDialog dialog;
    if ( window instanceof JDialog ) {
      dialog = new ConditionalVisibilityDialog( (JDialog) window );
    } else if ( window instanceof JFrame ) {
      dialog = new ConditionalVisibilityDialog( (JFrame) window );
    } else {
      dialog = new ConditionalVisibilityDialog();
    }

    final Expression oldExpression = visualElements.get( 0 ).getStyleExpression( ElementStyleKeys.VISIBLE );
    dialog.setReportDesignerContext( getReportDesignerContext() );
    final Expression expression = dialog.performEdit( oldExpression );
    if ( expression != null ) {
      visualElements.get( 0 ).setStyleExpression( ElementStyleKeys.VISIBLE, expression.getInstance() );
    }
  }
}
