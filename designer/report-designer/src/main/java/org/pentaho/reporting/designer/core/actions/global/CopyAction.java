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

import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.util.dnd.ClipboardManager;
import org.pentaho.reporting.designer.core.util.dnd.InsertationUtil;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class CopyAction extends AbstractElementSelectionAction {
  public CopyAction() {
    putValue( Action.NAME, ActionMessages.getString( "CopyAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "CopyAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "CopyAction.Mnemonic" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getCopyIcon() );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "CopyAction.Accelerator" ) );
  }

  protected void selectedElementPropertiesChanged( final ReportModelEvent event ) {
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final DocumentContextSelectionModel selectionModel1 = getSelectionModel();
    if ( selectionModel1 == null ) {
      return;
    }

    final ReportDocumentContext activeContext = getActiveContext();
    final Object[] selectedElements = selectionModel1.getSelectedElements();
    if ( selectedElements.length == 0 ) {
      return;
    }

    final ArrayList<Object> preparedElements = new ArrayList<Object>( selectedElements.length );
    for ( int i = 0; i < selectedElements.length; i++ ) {
      final Object selectedElement = selectedElements[ i ];
      final Object preparedElement = InsertationUtil.prepareForCopy( activeContext, selectedElement );
      if ( preparedElement != null ) {
        preparedElements.add( preparedElement );
      }
    }

    ClipboardManager.getManager().setContents( preparedElements.toArray() );
  }


}
