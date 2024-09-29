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
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.util.ReportDesignerDesignTimeContext;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.undo.DataSourceEditUndoEntry;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;
import org.pentaho.reporting.engine.classic.core.designtime.DefaultDataFactoryChangeRecorder;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class EditDataSourceAction extends AbstractElementSelectionAction {
  public EditDataSourceAction() {
    putValue( Action.NAME, ActionMessages.getString( "EditDataSourceAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "EditDataSourceAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "EditDataSourceAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "EditDataSourceAction.Accelerator" ) );
  }

  protected void selectedElementPropertiesChanged( final ReportModelEvent event ) {
  }

  protected void updateSelection() {
    final DocumentContextSelectionModel selectionModel1 = getSelectionModel();
    if ( selectionModel1 == null ) {
      setEnabled( false );
      return;
    }

    final List<DataFactory> selectedObjects = selectionModel1.getSelectedElementsOfType( DataFactory.class );
    for ( DataFactory dataFactory : selectedObjects ) {
      final DataFactoryMetaData metadata = dataFactory.getMetaData();
      if ( metadata.isEditable() ) {
        setEnabled( true );
        return;
      }
    }

    setEnabled( false );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final ReportDocumentContext activeContext = getActiveContext();
    if ( activeContext == null ) {
      return;
    }

    final List<DataFactory> selectedElements = getSelectionModel().getSelectedElementsOfType( DataFactory.class );
    for ( DataFactory dataFactory : selectedElements ) {
      try {
        performEdit( dataFactory );
      } catch ( ReportDataFactoryException e1 ) {
        UncaughtExceptionsModel.getInstance().addException( e1 );
      }
      return;
    }
  }

  protected void performEdit( final DataFactory dataFactory ) throws ReportDataFactoryException {
    final DataFactoryMetaData metadata = dataFactory.getMetaData();
    if ( metadata.isEditable() == false ) {
      return;
    }

    final DataSourcePlugin dataSourcePlugin = metadata.createEditor();
    final DataFactory storedFactory = dataFactory.derive();
    if ( dataSourcePlugin.canHandle( dataFactory ) == false ) {
      return;
    }

    final DefaultDataFactoryChangeRecorder recorder = new DefaultDataFactoryChangeRecorder();
    final DataFactory editedDataFactory = dataSourcePlugin.performEdit
      ( new ReportDesignerDesignTimeContext( getReportDesignerContext() ), dataFactory, null, recorder );
    if ( editedDataFactory == null ) {
      return;
    }

    final ReportDocumentContext activeContext = getActiveContext();
    final AbstractReportDefinition report = activeContext.getReportDefinition();
    final CompoundDataFactory collection = (CompoundDataFactory) report.getDataFactory();
    final int j = collection.indexOfByReference( dataFactory );
    if ( j == -1 ) {
      throw new IllegalStateException( "Edited data-source does not exist in the report anymore." );
    }

    DefaultDataFactoryChangeRecorder.applyChanges( collection, recorder.getChanges() );

    final DataFactory editedClone = editedDataFactory.derive();
    collection.set( j, editedDataFactory );
    activeContext.getUndo().addChange
      ( ActionMessages.getString( "EditDataSourceAction.UndoName" ),
        new DataSourceEditUndoEntry( j, storedFactory, editedClone ) );

    report.notifyNodeChildRemoved( dataFactory );
    report.notifyNodeChildAdded( editedDataFactory );
  }
}
