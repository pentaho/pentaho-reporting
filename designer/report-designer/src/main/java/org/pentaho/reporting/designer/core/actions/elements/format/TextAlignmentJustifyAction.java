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


package org.pentaho.reporting.designer.core.actions.elements.format;

import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.actions.ToggleStateAction;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.util.undo.CompoundUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.StyleEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoEntry;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.designtime.ReportModelEventFilter;
import org.pentaho.reporting.engine.classic.core.designtime.ReportModelEventFilterFactory;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public final class TextAlignmentJustifyAction extends AbstractElementSelectionAction implements ToggleStateAction {
  private ReportModelEventFilter eventFilter;
  private boolean selected;

  public TextAlignmentJustifyAction() {
    putValue( Action.NAME, ActionMessages.getString( "TextAlignmentJustifyAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "TextAlignmentJustifyAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "TextAlignmentJustifyAction.Mnemonic" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getTextAlignJustifyCommand() );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "TextAlignmentJustifyAction.Accelerator" ) );

    eventFilter = new ReportModelEventFilterFactory().createStyleFilter( ElementStyleKeys.ALIGNMENT );
  }

  protected void selectedElementPropertiesChanged( final ReportModelEvent event ) {
    if ( eventFilter.isFilteredEvent( event ) ) {
      updateSelection();
    }
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected( final boolean selected ) {
    final boolean oldValue = this.selected;
    this.selected = selected;
    firePropertyChange( SELECTED, oldValue, selected );
  }

  protected void updateSelection() {
    super.updateSelection();

    final DocumentContextSelectionModel model = getSelectionModel();
    if ( model == null ) {
      return;
    }
    final List<Element> visualElements = model.getSelectedElementsOfType( Element.class );
    if ( visualElements.isEmpty() ) {
      setSelected( false );
      return;
    }

    final Element element = visualElements.get( 0 );
    final ElementStyleSheet styleSheet = element.getStyle();
    setSelected( ElementAlignment.JUSTIFY.equals( styleSheet.getStyleProperty( ElementStyleKeys.ALIGNMENT ) ) );
  }


  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final DocumentContextSelectionModel model = getSelectionModel();
    if ( model == null ) {
      return;
    }
    final List<Element> visualElements = model.getSelectedElementsOfType( Element.class );
    final ArrayList<UndoEntry> undos = new ArrayList<UndoEntry>();
    for ( Element element : visualElements ) {
      final ElementStyleSheet styleSheet = element.getStyle();
      undos
        .add( StyleEditUndoEntry.createConditional( element, ElementStyleKeys.ALIGNMENT, ElementAlignment.JUSTIFY ) );
      styleSheet.setStyleProperty( ElementStyleKeys.ALIGNMENT, ElementAlignment.JUSTIFY );
      element.notifyNodePropertiesChanged();
    }
    getActiveContext().getUndo().addChange( ActionMessages.getString( "TextAlignmentJustifyAction.UndoName" ),
      new CompoundUndoEntry( undos.toArray( new UndoEntry[ undos.size() ] ) ) );
  }
}
