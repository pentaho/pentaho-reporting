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

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.crosstab.CreateCrosstabDialog;
import org.pentaho.reporting.designer.core.editor.crosstab.CrosstabEditSupport;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.settings.SettingsListener;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.undo.UndoEntry;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.GroupDataBody;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.SubGroupBody;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.filter.types.CrosstabElementType;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public final class InsertCrosstabGroupAction extends AbstractElementSelectionAction implements SettingsListener {
  private static class InsertGroupOnDetailsUndoEntry implements UndoEntry {
    private InstanceID target;
    private CrosstabGroup newGroup;
    private GroupDataBody oldBody;

    public InsertGroupOnDetailsUndoEntry( final InstanceID target,
                                          final CrosstabGroup newGroup,
                                          final GroupDataBody oldBody ) {
      this.target = target;
      this.newGroup = newGroup;
      this.oldBody = (GroupDataBody) oldBody.derive();
    }

    public void undo( final ReportDocumentContext renderContext ) {
      final RelationalGroup selectedGroup = (RelationalGroup)
        ModelUtility.findElementById( renderContext.getReportDefinition(), target );

      selectedGroup.setBody( (GroupBody) oldBody.derive() );
    }

    public void redo( final ReportDocumentContext renderContext ) {
      final RelationalGroup selectedGroup = (RelationalGroup)
        ModelUtility.findElementById( renderContext.getReportDefinition(), target );
      CrosstabEditSupport.installCrosstabIntoLastGroup( selectedGroup, newGroup );
    }

    public UndoEntry merge( final UndoEntry newEntry ) {
      return null;
    }
  }

  private static final long serialVersionUID = 6766753579037904765L;

  public InsertCrosstabGroupAction() {
    putValue( Action.NAME, ActionMessages.getString( "InsertCrosstabGroupAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "InsertCrosstabGroupAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "InsertCrosstabGroupAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "InsertCrosstabGroupAction.Accelerator" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getGenericSquare() );

    setVisible( WorkspaceSettings.getInstance().isVisible( CrosstabElementType.INSTANCE.getMetaData() ) );
    WorkspaceSettings.getInstance().addSettingsListener( this );
  }

  protected void selectedElementPropertiesChanged( final ReportModelEvent event ) {
  }

  public void actionPerformed( final ActionEvent e ) {
    final ReportDocumentContext activeContext = getActiveContext();
    if ( activeContext == null ) {
      return;
    }

    try {
      final AbstractReportDefinition report = activeContext.getReportDefinition();
      Object selectedElement = report;
      if ( getSelectionModel().getSelectionCount() > 0 ) {
        selectedElement = getSelectionModel().getSelectedElement( 0 );
      }

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

      if ( selectedElement != report &&
        selectedElement instanceof RelationalGroup == false ) {
        return;
      }


      final CrosstabGroup newGroup = dialog.createCrosstab( context, null );
      if ( newGroup == null ) {
        return;
      }

      if ( selectedElement == report ) {
        final Group rootGroup = report.getRootGroup();
        report.setRootGroup( newGroup );
        activeContext.getUndo().addChange( ActionMessages.getString( "InsertCrosstabGroupAction.UndoName" ),
          new CrosstabEditSupport.EditGroupOnReportUndoEntry( rootGroup, newGroup ) );
        return;
      }

      final RelationalGroup selectedGroup = (RelationalGroup) selectedElement;
      final GroupBody bodyElement = selectedGroup.getBody();
      if ( bodyElement instanceof SubGroupBody ) {
        final SubGroupBody subGroupBodyReportElement = (SubGroupBody) bodyElement;
        final Group oldBodyContent = subGroupBodyReportElement.getGroup();

        subGroupBodyReportElement.setGroup( newGroup );

        activeContext.getUndo().addChange( ActionMessages.getString( "InsertGroupAction.UndoName" ),
          new CrosstabEditSupport.EditGroupOnGroupUndoEntry( selectedGroup.getObjectID(), oldBodyContent, newGroup ) );
      } else if ( bodyElement instanceof GroupDataBody ) {
        // we cannot simply insert the group-data body into the crosstab. We need to locate the
        // innermost group and need to place the body there.
        final GroupDataBody oldBody = CrosstabEditSupport.installCrosstabIntoLastGroup( selectedGroup, newGroup );
        getActiveContext().getUndo().addChange( ActionMessages.getString( "InsertGroupAction.UndoName" ),
          new InsertGroupOnDetailsUndoEntry( selectedGroup.getObjectID(), newGroup, oldBody ) );

      }
    } catch ( final Exception ex ) {
      UncaughtExceptionsModel.getInstance().addException( ex );
    }
  }

  protected void updateSelection() {
    if ( isVisible() == false ) {
      setEnabled( false );
      return;
    }

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

    final AbstractReportDefinition report = getActiveContext().getReportDefinition();
    final Object selectedElement = selectionModel.getSelectedElement( 0 );
    if ( selectedElement == report || selectedElement instanceof RelationalGroup ) {
      setEnabled( true );
      return;
    }

    setEnabled( false );
  }

  public void settingsChanged() {
    setVisible( WorkspaceSettings.getInstance().isVisible( CrosstabElementType.INSTANCE.getMetaData() ) );
  }
}
