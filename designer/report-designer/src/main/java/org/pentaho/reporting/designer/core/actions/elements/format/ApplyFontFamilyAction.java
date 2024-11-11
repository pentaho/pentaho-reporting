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


package org.pentaho.reporting.designer.core.actions.elements.format;

import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.util.undo.CompoundUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.StyleEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoEntry;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public final class ApplyFontFamilyAction extends AbstractElementSelectionAction {
  private JComboBox comboBox;

  public ApplyFontFamilyAction( final JComboBox comboBox ) {
    this.comboBox = comboBox;
    putValue( Action.NAME, ActionMessages.getString( "ApplyFontFamilyAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "ApplyFontFamilyAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "ApplyFontFamilyAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "ApplyFontFamilyAction.Accelerator" ) );
  }

  protected void selectedElementPropertiesChanged( final ReportModelEvent event ) {
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final Object o = comboBox.getSelectedItem();
    if ( o != null && o instanceof String == false ) {
      return;
    }
    final String font = (String) o;

    final DocumentContextSelectionModel model = getSelectionModel();
    if ( model == null ) {
      return;
    }
    final List<Element> visualElements = model.getSelectedElementsOfType( Element.class );
    final ArrayList<UndoEntry> undos = new ArrayList<UndoEntry>();
    for ( Element visualElement : visualElements ) {
      undos.add( StyleEditUndoEntry.createConditional( visualElement, TextStyleKeys.FONT, font ) );
      visualElement.getStyle().setStyleProperty( TextStyleKeys.FONT, font );
      visualElement.notifyNodePropertiesChanged();
    }
    getActiveContext().getUndo().addChange( ActionMessages.getString( "ApplyFontFamilyAction.UndoName" ),
      new CompoundUndoEntry( undos.toArray( new UndoEntry[ undos.size() ] ) ) );

  }
}
