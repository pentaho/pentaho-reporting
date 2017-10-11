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

package org.pentaho.reporting.designer.core.actions.elements.format;

import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.actions.ToggleStateAction;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.util.undo.AttributeEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.CompoundUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoEntry;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.designtime.ReportModelEventFilter;
import org.pentaho.reporting.engine.classic.core.designtime.ReportModelEventFilterFactory;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.ReportDesignerParserModule;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public final class HideElementAction extends AbstractElementSelectionAction implements ToggleStateAction {
  private final ReportModelEventFilter eventFilter;

  public HideElementAction() {
    putValue( Action.SELECTED_KEY, Boolean.FALSE );
    putValue( Action.NAME, ActionMessages.getString( "HideElementAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "HideElementAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "HideElementAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "HideElementAction.Accelerator" ) );

    eventFilter = new ReportModelEventFilterFactory().createAttributeFilter
      ( ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE );
  }

  public boolean isSelected() {
    return Boolean.TRUE.equals( getValue( Action.SELECTED_KEY ) );
  }

  public void setSelected( final boolean selected ) {
    putValue( Action.SELECTED_KEY, selected );
  }

  protected void selectedElementPropertiesChanged( final ReportModelEvent event ) {
    if ( eventFilter.isFilteredEvent( event ) ) {
      updateSelection();
    }
  }

  protected void updateSelection() {
    super.updateSelection();

    final DocumentContextSelectionModel model = getSelectionModel();
    if ( model == null ) {
      return;
    }
    final List<Element> visualElements = model.getSelectedElementsOfType( Element.class );

    boolean selected;
    if ( visualElements.size() == 0 ) {
      selected = false;
    } else {
      selected = true;
      for ( Element visualElement : visualElements ) {
        selected &= ModelUtility.isHideInLayoutGui( visualElement );
      }
    }
    setSelected( selected );
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

    Boolean value = null;
    final ArrayList<UndoEntry> undos = new ArrayList<UndoEntry>();
    for ( Element element : visualElements ) {
      if ( value == null ) {
        if ( ModelUtility.isHideInLayoutGui( element ) ) {
          value = Boolean.FALSE;
        } else {
          value = Boolean.TRUE;
        }
      }

      final Object oldValue = element.getAttribute( ReportDesignerParserModule.NAMESPACE,
        ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE );
      final UndoEntry entry = new AttributeEditUndoEntry( element.getObjectID(), ReportDesignerParserModule.NAMESPACE,
        ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE, oldValue, value );
      undos.add( entry );
      element.setAttribute( ReportDesignerParserModule.NAMESPACE,
        ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE, value );
    }
    getActiveContext().getUndo().addChange( ActionMessages.getString( "HideElementAction.UndoName" ),
      new CompoundUndoEntry( undos.toArray( new UndoEntry[ undos.size() ] ) ) );
  }
}
