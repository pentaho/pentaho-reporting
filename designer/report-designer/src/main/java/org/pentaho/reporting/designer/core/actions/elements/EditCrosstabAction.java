/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.designer.core.actions.elements;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.crosstab.CreateCrosstabDialog;
import org.pentaho.reporting.designer.core.editor.crosstab.CrosstabEditSupport;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.settings.SettingsListener;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.undo.UndoEntry;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.CrosstabElement;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubGroupBody;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.filter.types.CrosstabElementType;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class EditCrosstabAction extends AbstractElementSelectionAction implements SettingsListener {

  private static final long serialVersionUID = 6766753579037904765L;

  public EditCrosstabAction() {
    putValue( Action.NAME, ActionMessages.getString( "EditCrosstabAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "EditCrosstabAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "EditCrosstabAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "EditCrosstabAction.Accelerator" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getGenericSquare() );

    setVisible( WorkspaceSettings.getInstance().isVisible( CrosstabElementType.INSTANCE.getMetaData() ) );
    WorkspaceSettings.getInstance().addSettingsListener( this );
  }

  protected void selectedElementPropertiesChanged( final ReportModelEvent event ) {
  }

  public void actionPerformed( final ActionEvent e ) {
    try {
      final ReportDocumentContext activeContext = getActiveContext();
      if ( activeContext == null ) {
        return;
      }

      final CrosstabGroup selectedCrosstab = findSelectedCrosstab( activeContext );
      if ( selectedCrosstab == null ) {
        return;
      }

      final CrosstabGroup newGroup = performEdit( selectedCrosstab );
      if ( newGroup == null ) {
        return;
      }

      UndoEntry undo = createUndoEntry( selectedCrosstab, newGroup );
      if ( undo != null ) {
        undo.redo( activeContext );
        activeContext.getUndo().addChange( "Edit Crosstab", undo );
      }
    } catch ( final Exception ex ) {
      UncaughtExceptionsModel.getInstance().addException( ex );
    }
  }

  protected CrosstabGroup performEdit( final CrosstabGroup selectedCrosstab ) {
    final CreateCrosstabDialog dialog = createDialog();
    final CrosstabGroup newGroup = dialog.createCrosstab( getReportDesignerContext(), selectedCrosstab );
    if ( newGroup == null ) {
      return null;
    }
    return newGroup;
  }

  protected UndoEntry createUndoEntry( final CrosstabGroup selectedCrosstab, final CrosstabGroup newGroup ) {
    UndoEntry undo;
    Section parentSection = selectedCrosstab.getParentSection();
    if ( parentSection instanceof AbstractReportDefinition ) {
      AbstractReportDefinition r = (AbstractReportDefinition) parentSection;
      undo = new CrosstabEditSupport.EditGroupOnReportUndoEntry( r.getRootGroup(), newGroup );
    } else if ( parentSection instanceof SubGroupBody ) {
      SubGroupBody b = (SubGroupBody) parentSection;
      undo = new CrosstabEditSupport.EditGroupOnGroupUndoEntry( b.getObjectID(), b.getGroup(), newGroup );
    } else {
      undo = null;
    }
    return undo;
  }

  protected CrosstabGroup findSelectedCrosstab( final ReportDocumentContext activeContext ) {
    final AbstractReportDefinition report = activeContext.getReportDefinition();
    Object selectedElement = null;
    if ( report instanceof CrosstabElement ) {
      selectedElement = findCrosstabGroup( report );
    } else {
      if ( getSelectionModel().getSelectionCount() > 0 ) {
        selectedElement = getSelectionModel().getSelectedElement( 0 );
      }
    }
    if ( selectedElement instanceof CrosstabGroup == false ) {
      return null;
    }

    return (CrosstabGroup) selectedElement;
  }

  protected CreateCrosstabDialog createDialog() {
    final ReportDesignerContext context = getReportDesignerContext();
    final Component parent = context.getView().getParent();
    final Window window = LibSwingUtil.getWindowAncestor( parent );
    final CreateCrosstabDialog dialog;
    if ( window instanceof JDialog ) {
      dialog = new CreateCrosstabDialog( (JDialog) window );
    } else if ( window instanceof JFrame ) {
      dialog = new CreateCrosstabDialog( (JFrame) window );
    } else {
      dialog = new CreateCrosstabDialog();
    }
    return dialog;
  }

  private Object findCrosstabGroup( final AbstractReportDefinition report ) {
    Group g = report.getRootGroup();
    while ( g != null ) {
      if ( g instanceof CrosstabGroup ) {
        return g;
      }
      g = g.getBody().getGroup();
    }
    return null;
  }

  protected void updateSelection() {
    if ( isVisible() == false ) {
      setEnabled( false );
      return;
    }

    ReportDocumentContext activeContext = getActiveContext();
    if ( activeContext == null ) {
      return;
    }
    AbstractReportDefinition reportDefinition = activeContext.getReportDefinition();
    if ( reportDefinition instanceof CrosstabElement ) {
      setEnabled( true );
      return;
    }

    final DocumentContextSelectionModel selectionModel = getSelectionModel();
    if ( selectionModel == null ) {
      setEnabled( false );
      return;
    }

    if ( isSingleElementSelection() == false ) {
      // there's more than 1 element selected, disable because
      // we can't know where to insert in this case
      setEnabled( false );
      return;
    }

    final Object selectedElement = selectionModel.getSelectedElement( 0 );
    if ( selectedElement instanceof CrosstabGroup ) {
      setEnabled( true );
      return;
    }

    setEnabled( false );
  }

  public void settingsChanged() {
    setVisible( WorkspaceSettings.getInstance().isVisible( CrosstabElementType.INSTANCE.getMetaData() ) );
  }
}
