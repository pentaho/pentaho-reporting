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

package org.pentaho.reporting.designer.core.actions.report;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.editor.parameters.DataFactoryWrapper;
import org.pentaho.reporting.designer.core.editor.parameters.ParameterDialog;
import org.pentaho.reporting.designer.core.editor.parameters.SubReportParameterDialog;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.undo.CompoundUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.DataSourceEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.ParameterEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoEntry;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.parameters.ModifiableReportParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class EditParametersAction extends AbstractElementSelectionAction {
  public EditParametersAction() {
    configureForMaster();
  }

  private void configureForSubreport() {
    putValue( Action.NAME, ActionMessages.getString( "EditParametersAction.SubReport.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "EditParametersAction.SubReport.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "EditParametersAction.SubReport.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY,
      ActionMessages.getOptionalKeyStroke( "EditParametersAction.SubReport.Accelerator" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getParameterIcon() );
  }

  private void configureForMaster() {
    putValue( Action.NAME, ActionMessages.getString( "EditParametersAction.MasterReport.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "EditParametersAction.MasterReport.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "EditParametersAction.MasterReport.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY,
      ActionMessages.getOptionalKeyStroke( "EditParametersAction.MasterReport.Accelerator" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getParameterIcon() );
  }

  protected void selectedElementPropertiesChanged( final ReportModelEvent event ) {
  }

  protected void updateActiveContext( final ReportRenderContext oldContext, final ReportRenderContext newContext ) {
    super.updateActiveContext( oldContext, newContext );
    if ( newContext == null ) {
      configureForMaster();
      return;
    }
    final AbstractReportDefinition definition = newContext.getReportDefinition();
    if ( definition instanceof SubReport ) {
      configureForSubreport();
    } else {
      configureForMaster();
    }
  }

  public void actionPerformed( final ActionEvent e ) {
    final ReportDocumentContext activeContext = getActiveContext();
    if ( activeContext == null ) {
      return;
    }

    if ( activeContext.getReportDefinition() instanceof SubReport ) {
      performEditSubReportParameters( getReportDesignerContext() );
      return;
    }

    final Object[] selectedElements = getSelectionModel().getSelectedElements();
    for ( int i = 0; i < selectedElements.length; i++ ) {
      final Object theElement = selectedElements[ i ];
      if ( theElement instanceof ParameterDefinitionEntry ) {
        try {
          performEditMasterReportParameters( getReportDesignerContext(), (ParameterDefinitionEntry) theElement );
        } catch ( ReportDataFactoryException theExc ) {
          UncaughtExceptionsModel.getInstance().addException( theExc );
        }
        break;
      }
    }
  }

  public static void performEditSubReportParameters( final ReportDesignerContext context ) {
    final ReportDocumentContext activeContext = context.getActiveContext();
    if ( activeContext == null ) {
      throw new NullPointerException();
    }
    final AbstractReportDefinition definition = activeContext.getReportDefinition();
    if ( definition instanceof SubReport == false ) {
      throw new IllegalStateException();
    }
    final SubReport subReport = (SubReport) definition;

    final Component parent = context.getView().getParent();
    final Window window = LibSwingUtil.getWindowAncestor( parent );
    final SubReportParameterDialog parameterDialog;
    if ( window instanceof JDialog ) {
      parameterDialog = new SubReportParameterDialog( (JDialog) window );
    } else if ( window instanceof JFrame ) {
      parameterDialog = new SubReportParameterDialog( (JFrame) window );
    } else {
      parameterDialog = new SubReportParameterDialog();
    }

    final SubReportParameterDialog.EditResult editResult =
      parameterDialog.performEdit( context, subReport.getInputMappings(), subReport.getExportMappings() );
    if ( editResult == null ) {
      return;
    }

    final ParameterMapping[] oldImportMapping = subReport.getInputMappings();
    final ParameterMapping[] oldExportMapping = subReport.getExportMappings();
    activeContext.getUndo().addChange( ActionMessages.getString( "EditParametersAction.SubReport.Text" ),
      new EditSubreportParametersUndoEntry
        ( oldImportMapping, oldExportMapping, editResult.getImportParameters(), editResult.getExportParameters() ) );
    subReport.setExportMappings( editResult.getExportParameters() );
    subReport.setInputMappings( editResult.getImportParameters() );
  }

  private static class EditSubreportParametersUndoEntry implements UndoEntry {
    private ParameterMapping[] oldImportParameters;
    private ParameterMapping[] oldExportParameters;
    private ParameterMapping[] newImportParameters;
    private ParameterMapping[] newExportParameters;

    private EditSubreportParametersUndoEntry( final ParameterMapping[] oldImportParameters,
                                              final ParameterMapping[] oldExportParameters,
                                              final ParameterMapping[] newImportParameters,
                                              final ParameterMapping[] newExportParameters ) {
      this.oldImportParameters = oldImportParameters;
      this.oldExportParameters = oldExportParameters;
      this.newImportParameters = newImportParameters;
      this.newExportParameters = newExportParameters;
    }

    public void undo( final ReportDocumentContext renderContext ) {
      final SubReport reportDefinition = (SubReport) renderContext.getReportDefinition();
      reportDefinition.setInputMappings( oldImportParameters );
      reportDefinition.setExportMappings( oldExportParameters );
    }

    public void redo( final ReportDocumentContext renderContext ) {
      final SubReport reportDefinition = (SubReport) renderContext.getReportDefinition();
      reportDefinition.setInputMappings( newImportParameters );
      reportDefinition.setExportMappings( newExportParameters );
    }

    public UndoEntry merge( final UndoEntry newEntry ) {
      return null;
    }
  }

  public static void performEditMasterReportParameters( final ReportDesignerContext context,
                                                        final ParameterDefinitionEntry parameter )
    throws ReportDataFactoryException {
    final ReportDocumentContext activeContext = context.getActiveContext();
    if ( activeContext == null ) {
      return;
    }

    final Component theParent = context.getView().getParent();
    final Window theWindow = LibSwingUtil.getWindowAncestor( theParent );
    final ParameterDialog parameterDialog;
    if ( theWindow instanceof JDialog ) {
      parameterDialog = new ParameterDialog( (JDialog) theWindow, context );
    } else if ( theWindow instanceof JFrame ) {
      parameterDialog = new ParameterDialog( (JFrame) theWindow, context );
    } else {
      parameterDialog = new ParameterDialog( context );
    }

    final MasterReport masterReport = activeContext.getContextRoot();
    final ModifiableReportParameterDefinition parameterDefinition =
      (ModifiableReportParameterDefinition) masterReport.getParameterDefinition();
    int index = -1;
    for ( int i = 0; i < parameterDefinition.getParameterCount(); i++ ) {
      final ParameterDefinitionEntry definition = parameterDefinition.getParameterDefinition( i );
      if ( definition == parameter ) {
        index = i;
        break;
      }
    }
    if ( index == -1 ) {
      if ( parameter == null ) {
        index = parameterDefinition.getParameterCount();
      } else {
        throw new IndexOutOfBoundsException( "This parameter is not part of the existing parameter collection" );
      }
    }

    final ParameterDialog.ParameterEditResult definitionEntry = parameterDialog.performEditParameter
      ( context, masterReport, parameter );
    if ( definitionEntry != null ) {
      final ParameterEditUndoEntry parameterEditUndoEntry =
        new ParameterEditUndoEntry( index, parameter, definitionEntry.getParameter() );
      final ArrayList<UndoEntry> undos = new ArrayList<UndoEntry>();
      undos.add( parameterEditUndoEntry );
      final DataFactoryWrapper[] dataFactoryWrappers = definitionEntry.getWrappers();
      for ( int i = 0; i < dataFactoryWrappers.length; i++ ) {
        final DataFactoryWrapper dataFactoryWrapper = dataFactoryWrappers[ i ];
        if ( dataFactoryWrapper.getOriginalDataFactory() != dataFactoryWrapper.getEditedDataFactory() ) {
          undos.add( new DataSourceEditUndoEntry( i, dataFactoryWrapper.getOriginalDataFactory(),
            dataFactoryWrapper.getEditedDataFactory() ) );
        }
      }

      final CompoundUndoEntry undoEntry = new CompoundUndoEntry( undos.toArray( new UndoEntry[ undos.size() ] ) );
      undoEntry.redo( activeContext );
      activeContext.getUndo()
        .addChange( ActionMessages.getString( "EditParametersAction.MasterReport.Text" ), undoEntry );
    }
  }
}
