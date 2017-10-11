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

package org.pentaho.reporting.designer.core.actions.elements;

import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.undo.CompoundUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoEntry;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

public abstract class AbstractLayerAction extends AbstractElementSelectionAction {
  protected AbstractLayerAction() {
  }

  protected void selectedElementPropertiesChanged( final ReportModelEvent event ) {
  }

  protected void updateSelection() {
    setEnabled( isSingleElementSelection() );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final ReportDocumentContext activeContext = getActiveContext();
    if ( activeContext == null ) {
      return;
    }

    final Object[] selectedElements = activeContext.getSelectionModel().getSelectedElements();
    final AbstractReportDefinition report = activeContext.getReportDefinition();

    final ArrayList<UndoEntry> undos = new ArrayList<UndoEntry>();
    if ( collectChange( selectedElements, report, undos ) == false ) {
      // rollback ..
      for ( int i = undos.size() - 1; i >= 0; i-- ) {
        final UndoEntry undoEntry = undos.get( i );
        undoEntry.undo( activeContext );
      }
    } else {
      final UndoEntry[] undoEntries = undos.toArray( new UndoEntry[ undos.size() ] );
      activeContext.getUndo().addChange( ActionMessages.getString( "AbstractLayerAction.UndoName" ),
        new CompoundUndoEntry( undoEntries ) );
    }
    // re-select the elements (moving them causes them to be unselected)
    activeContext.getSelectionModel().setSelectedElements( selectedElements );
  }


  protected abstract boolean collectChange( final Object[] selectedElements,
                                            final AbstractReportDefinition report,
                                            final ArrayList<UndoEntry> undos );

  protected boolean move( final Object element, final AbstractReportDefinition report,
                          final ArrayList<UndoEntry> undos ) {
    try {
      if ( element instanceof Expression ) {
        final UndoEntry undoEntry = moveExpressions( report, element );
        if ( undoEntry == null ) {
          return false;
        }
        undos.add( undoEntry );
      } else if ( element instanceof ParameterDefinitionEntry ) {
        final UndoEntry undoEntry = moveParameters( report, element );
        if ( undoEntry == null ) {
          return false;
        }
        undos.add( undoEntry );
      } else if ( element instanceof DataFactory ) {
        final UndoEntry undoEntry = moveDataFactories( report, element );
        if ( undoEntry == null ) {
          return false;
        }
        undos.add( undoEntry );
      } else if ( element instanceof RelationalGroup ) {
        final UndoEntry undoEntry = moveGroup( (RelationalGroup) element );
        if ( undoEntry == null ) {
          return false;
        }
        undos.add( undoEntry );
      } else if ( element instanceof Element ) {
        final UndoEntry undoEntry = moveVisualElement( report, (Element) element );
        if ( undoEntry == null ) {
          return false;
        }
        undos.add( undoEntry );
      }
    } catch ( Exception ex ) {
      UncaughtExceptionsModel.getInstance().addException( ex );
      return false;
    }
    return true;
  }

  protected abstract UndoEntry moveExpressions( final AbstractReportDefinition report, final Object element );

  protected abstract UndoEntry moveVisualElement( final AbstractReportDefinition report, final Element element );

  protected abstract UndoEntry moveGroup( final RelationalGroup element ) throws CloneNotSupportedException;

  protected abstract UndoEntry moveParameters( final AbstractReportDefinition report, final Object element );

  protected abstract UndoEntry moveDataFactories( final AbstractReportDefinition report,
                                                  final Object element ) throws ReportDataFactoryException;

}
