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


package org.pentaho.reporting.designer.core.actions.report;

import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.structuretree.ReportQueryNode;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.util.ReportDesignerDesignTimeContext;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.undo.DataSourceEditUndoEntry;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Handles action to edit data-source
 *
 * @author Thomas Morgner
 */
public class EditQueryAction extends AbstractElementSelectionAction {
  private DataFactory editedDataFactory = null;

  public EditQueryAction() {
    putValue( Action.NAME, ActionMessages.getString( "EditQueryAction.Text" ) );
    putValue( Action.DEFAULT, ActionMessages.getString( "EditQueryAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "EditQueryAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "EditQueryAction.Accelerator" ) );
  }

  protected void selectedElementPropertiesChanged( final ReportModelEvent event ) {
  }

  protected void updateSelection() {
    final DocumentContextSelectionModel selectionModel1 = getSelectionModel();
    if ( selectionModel1 == null ) {
      setEnabled( false );
      editedDataFactory = null;
      return;
    }

    final Object[] selectedObjects = selectionModel1.getSelectedElements();
    for ( int i = 0; i < selectedObjects.length; i++ ) {
      final Object selectedObject = selectedObjects[ i ];
      if ( selectedObject instanceof ReportQueryNode == false ) {
        continue;
      }
      final ReportQueryNode queryNode = (ReportQueryNode) selectedObject;
      final DataFactory dataFactory = queryNode.getDataFactory();

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

    final Object[] selectedElements = getSelectionModel().getSelectedElements();
    for ( int i = 0; i < selectedElements.length; i++ ) {
      final Object element = selectedElements[ i ];
      if ( element instanceof ReportQueryNode ) {
        final ReportQueryNode queryNode = (ReportQueryNode) element;
        try {
          performEdit( queryNode.getDataFactory(), queryNode.getQueryName() );
        } catch ( ReportDataFactoryException e1 ) {
          UncaughtExceptionsModel.getInstance().addException( e1 );
        }
        break;
      } else if ( element instanceof DataFactory ) {
        try {
          final AbstractReportDefinition report = activeContext.getReportDefinition();
          final DataFactory dataFactory = ( (DataFactory) element );
          performEdit( dataFactory, report.getQuery() );
        } catch ( ReportDataFactoryException e1 ) {
          UncaughtExceptionsModel.getInstance().addException( e1 );
        }
        break;
      }
    }
  }

  public void performEdit( final DataFactory dataFactory, final String queryName ) throws ReportDataFactoryException {
    final DataFactoryMetaData metadata = dataFactory.getMetaData();
    if ( metadata.isEditable() == false ) {
      return;
    }

    final DataSourcePlugin dataSourcePlugin = metadata.createEditor();
    final DataFactory storedFactory = dataFactory.derive();
    if ( dataSourcePlugin.canHandle( dataFactory ) ) {
      final ReportDocumentContext activeContext = getActiveContext();
      final AbstractReportDefinition report = activeContext.getReportDefinition();
      final boolean editingActiveQuery = contains( report.getQuery(), dataFactory.getQueryNames() );

      final ReportDesignerDesignTimeContext designTimeContext =
        new ReportDesignerDesignTimeContext( getReportDesignerContext() );
      editedDataFactory = dataSourcePlugin.performEdit( designTimeContext, dataFactory, queryName, null );
      if ( editedDataFactory == null ) {
        return;
      }

      final Window parentWindow = designTimeContext.getParentWindow();
      parentWindow.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );

      final CompoundDataFactory collection = (CompoundDataFactory) report.getDataFactory();
      final int dataFactoryCount = collection.size();
      for ( int j = 0; j < dataFactoryCount; j++ ) {
        final DataFactory originalDataFactory = collection.getReference( j );
        if ( originalDataFactory == dataFactory ) {
          collection.remove( j );

          final DataFactory editedClone = editedDataFactory.derive();
          collection.add( j, editedDataFactory );
          activeContext.getUndo().addChange( ActionMessages.getString( "EditQueryAction.Text" ),
            new DataSourceEditUndoEntry( j, storedFactory, editedClone ) );

          report.notifyNodeChildRemoved( originalDataFactory );
          report.notifyNodeChildAdded( editedDataFactory );

          parentWindow.setCursor( Cursor.getDefaultCursor() );
          if ( editingActiveQuery == false ) {
            // if we are editing a query that is not the one the current report uses, do not mess around with it.
            return;
          }

          final String[] editedQueries = editedDataFactory.getQueryNames();
          if ( contains( report.getQuery(), editedQueries ) == false ) {
            report.setQuery( null );
          }
          return;
        }
      }

      throw new IllegalStateException();
    }

  }

  private static boolean contains( final String key, final String[] haystack ) {
    for ( int i = 0; i < haystack.length; i++ ) {
      if ( ObjectUtilities.equal( haystack[ i ], key ) ) {
        return true;
      }
    }
    return false;
  }

  public DataFactory getEditedDataFactory() {
    return editedDataFactory;
  }
}
