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
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.undo.EditGroupUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoEntry;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.GroupDataBody;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.SubGroupBody;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

import javax.swing.*;
import java.awt.event.ActionEvent;

public final class InsertGroupAction extends AbstractElementSelectionAction {
  private static class InsertGroupOnReportUndoEntry implements UndoEntry {
    private Group newRootGroup;
    private Group oldRootGroup;

    private InsertGroupOnReportUndoEntry( final Group oldRootGroup, final Group newRootGroup ) {
      this.oldRootGroup = oldRootGroup;
      this.newRootGroup = newRootGroup;
    }

    public void undo( final ReportDocumentContext renderContext ) {
      final AbstractReportDefinition report = renderContext.getReportDefinition();
      report.setRootGroup( oldRootGroup );
    }

    public void redo( final ReportDocumentContext renderContext ) {
      final AbstractReportDefinition report = renderContext.getReportDefinition();
      final SubGroupBody body = new SubGroupBody();
      newRootGroup.setBody( body );
      report.setRootGroup( newRootGroup );
      body.setGroup( oldRootGroup );
    }

    public UndoEntry merge( final UndoEntry newEntry ) {
      return null;
    }
  }

  private static class InsertGroupOnGroupUndoEntry implements UndoEntry {
    private InstanceID target;
    private Group newRootGroup;
    private Group oldRootGroup;

    private InsertGroupOnGroupUndoEntry( final InstanceID target, final Group oldRootGroup, final Group newRootGroup ) {
      this.target = target;
      this.oldRootGroup = oldRootGroup;
      this.newRootGroup = newRootGroup;
    }

    public void undo( final ReportDocumentContext renderContext ) {
      final RelationalGroup selectedGroup = (RelationalGroup)
        ModelUtility.findElementById( renderContext.getReportDefinition(), target );
      final GroupBody bodyElement = selectedGroup.getBody();
      if ( bodyElement instanceof SubGroupBody == false ) {
        throw new IllegalStateException();
      }

      final SubGroupBody subGroupBodyReportElement = (SubGroupBody) bodyElement;
      subGroupBodyReportElement.setGroup( oldRootGroup );
    }

    public void redo( final ReportDocumentContext renderContext ) {
      final RelationalGroup selectedGroup = (RelationalGroup)
        ModelUtility.findElementById( renderContext.getReportDefinition(), target );

      final GroupBody bodyElement = selectedGroup.getBody();
      if ( bodyElement instanceof SubGroupBody == false ) {
        throw new IllegalStateException();
      }

      final SubGroupBody subGroupBodyReportElement = (SubGroupBody) bodyElement;
      final Group oldBodyContent = subGroupBodyReportElement.getGroup();

      final SubGroupBody body = new SubGroupBody();
      newRootGroup.setBody( body );
      subGroupBodyReportElement.setGroup( newRootGroup );
      body.setGroup( oldBodyContent );
    }

    public UndoEntry merge( final UndoEntry newEntry ) {
      return null;
    }
  }

  private class InsertGroupOnDetailsUndoEntry implements UndoEntry {
    private InstanceID target;
    private RelationalGroup newGroup;

    public InsertGroupOnDetailsUndoEntry( final InstanceID target,
                                          final RelationalGroup newGroup ) {
      this.target = target;
      this.newGroup = newGroup;
    }

    public void undo( final ReportDocumentContext renderContext ) {
      final RelationalGroup selectedGroup = (RelationalGroup)
        ModelUtility.findElementById( renderContext.getReportDefinition(), target );

      final GroupBody bodyElement = selectedGroup.getBody();
      if ( bodyElement instanceof SubGroupBody == false ) {
        throw new IllegalStateException();
      }
      final SubGroupBody sgb = (SubGroupBody) bodyElement;
      final GroupBody maybeDataBody = sgb.getGroup().getBody();
      if ( maybeDataBody instanceof GroupDataBody == false ) {
        throw new IllegalStateException();
      }

      selectedGroup.setBody( maybeDataBody );
    }

    public void redo( final ReportDocumentContext renderContext ) {
      final RelationalGroup selectedGroup = (RelationalGroup)
        ModelUtility.findElementById( renderContext.getReportDefinition(), target );

      final GroupBody bodyElement = selectedGroup.getBody();
      if ( bodyElement instanceof GroupDataBody == false ) {
        throw new IllegalStateException();
      }

      final GroupDataBody oldBody = (GroupDataBody) bodyElement;
      selectedGroup.setBody( new SubGroupBody( newGroup ) );
      newGroup.setBody( oldBody );
    }

    public UndoEntry merge( final UndoEntry newEntry ) {
      return null;
    }
  }

  public InsertGroupAction() {
    putValue( Action.NAME, ActionMessages.getString( "InsertGroupAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "InsertGroupAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "InsertGroupAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "InsertGroupAction.Accelerator" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getGenericSquare() );
  }

  protected void selectedElementPropertiesChanged( final ReportModelEvent event ) {
  }

  public void actionPerformed( final ActionEvent e ) {
    final ReportDocumentContext activeContext = getActiveContext();
    if ( activeContext == null ) {
      return;
    }

    final RelationalGroup newGroup = new RelationalGroup();
    final EditGroupUndoEntry groupUndoEntry =
      EditGroupAction.performEditGroup( getReportDesignerContext(), newGroup, true );
    if ( groupUndoEntry == null ) {
      return;
    }

    // apply the data from the EditGroupAction ..
    newGroup.setName( groupUndoEntry.getNewName() );
    newGroup.setFieldsArray( groupUndoEntry.getNewFields() );

    try {
      Object selectedElement = activeContext.getReportDefinition();
      if ( getSelectionModel().getSelectionCount() > 0 ) {
        selectedElement = getSelectionModel().getSelectedElement( 0 );
      }
      if ( selectedElement == activeContext.getReportDefinition() ) {
        // execution order is important here.
        // first unlink the old root-group by setting a new one ...
        final AbstractReportDefinition report = (AbstractReportDefinition) selectedElement;
        final Group rootGroup = report.getRootGroup();

        final SubGroupBody body = new SubGroupBody();
        newGroup.setBody( body );
        report.setRootGroup( newGroup );

        // *then* you can set the old-root to the newly inserted group ..
        body.setGroup( rootGroup );

        activeContext.getUndo().addChange( ActionMessages.getString( "InsertGroupAction.UndoName" ),
          new InsertGroupOnReportUndoEntry( rootGroup, newGroup ) );
        return;
      }

      if ( selectedElement instanceof RelationalGroup == false ) {
        return;
      }
      final RelationalGroup selectedGroup = (RelationalGroup) selectedElement;

      final GroupBody bodyElement = selectedGroup.getBody();
      if ( bodyElement instanceof SubGroupBody ) {
        final SubGroupBody subGroupBodyReportElement = (SubGroupBody) bodyElement;
        final Group oldBodyContent = subGroupBodyReportElement.getGroup();

        final SubGroupBody body = new SubGroupBody();
        newGroup.setBody( body );
        subGroupBodyReportElement.setGroup( newGroup );
        body.setGroup( oldBodyContent );

        activeContext.getUndo().addChange( ActionMessages.getString( "InsertGroupAction.UndoName" ),
          new InsertGroupOnGroupUndoEntry( selectedGroup.getObjectID(), oldBodyContent, newGroup ) );
      } else if ( bodyElement instanceof GroupDataBody ) {
        final GroupDataBody oldBody = (GroupDataBody) bodyElement;
        selectedGroup.setBody( new SubGroupBody( newGroup ) );
        newGroup.setBody( oldBody );
        activeContext.getUndo().addChange( ActionMessages.getString( "InsertGroupAction.UndoName" ),
          new InsertGroupOnDetailsUndoEntry( selectedGroup.getObjectID(), newGroup ) );
      }
    } catch ( Exception ex ) {
      UncaughtExceptionsModel.getInstance().addException( ex );
    }
  }

  protected void updateSelection() {
    final DocumentContextSelectionModel selectionModel = getSelectionModel();
    if ( selectionModel == null ) {
      setEnabled( false );
      return;
    }
    if ( selectionModel.getSelectionCount() == 0 ) {
      // there's nothing selected, we can safely add a new group
      // at the report level (AbstractReportDefinition)
      setEnabled( true );
      return;
    }
    if ( isSingleElementSelection() == false ) {
      // there's more than 1 element selected, disable because
      // we can't know where to insert in this case
      setEnabled( false );
      return;
    }

    final Object selectedElement = selectionModel.getSelectedElement( 0 );
    if ( selectedElement == getActiveContext().getReportDefinition() || selectedElement instanceof RelationalGroup ) {
      // if the selectedElement is the report-definition or a relational group
      // then we can safely insert to those
      setEnabled( true );
      return;
    }

    setEnabled( false );
  }

}
