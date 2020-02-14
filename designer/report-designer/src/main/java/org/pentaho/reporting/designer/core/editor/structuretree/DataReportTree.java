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
* Copyright (c) 2002-2020 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.designer.core.editor.structuretree;

import org.pentaho.reporting.designer.core.actions.report.EditParametersAction;
import org.pentaho.reporting.designer.core.actions.report.EditQueryAction;
import org.pentaho.reporting.designer.core.editor.ReportDataChangeListener;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.settings.SettingsListener;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.dnd.FieldDescriptionTransferable;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.designtime.AttributeChange;
import org.pentaho.reporting.engine.classic.core.designtime.AttributeExpressionChange;
import org.pentaho.reporting.engine.classic.core.designtime.StyleChange;
import org.pentaho.reporting.engine.classic.core.designtime.StyleExpressionChange;
import org.pentaho.reporting.engine.classic.core.designtime.SubReportParameterChange;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportModelListener;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.libraries.base.util.DebugLog;

import javax.swing.*;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class DataReportTree extends AbstractReportTree {
  private class ReportUpdateHandler implements ReportModelListener, ReportDataChangeListener {
    private ReportUpdateHandler() {
    }

    public void dataModelChanged( final ReportDocumentContext context ) {
      final AbstractReportDataTreeModel realModel = getDataTreeModel();
      if ( realModel == null ) {
        return;
      }

      DebugLog.log( "Data Changed, Update tree" );
      realModel.fireQueryChanged( renderContext.getReportDefinition().getQuery() );
    }

    public void nodeChanged( final ReportModelEvent event ) {
      final AbstractReportDataTreeModel realModel = getDataTreeModel();
      if ( realModel == null ) {
        return;
      }

      try {
        if ( event.isNodeDeleteEvent() ) {
          handleNodeRemoved( event, realModel );
        }

        if ( event.isNodeAddedEvent() ) {
          handleNodeAdded( event, realModel );
        }

        if ( event.getType() == ReportModelEvent.NODE_PROPERTIES_CHANGED ) {
          if ( event.getElement() == renderContext.getReportDefinition() ) {
            final Object eventParameter = event.getParameter();
            if ( eventParameter instanceof AttributeExpressionChange ||
              eventParameter instanceof StyleChange ||
              eventParameter instanceof StyleExpressionChange ) {
              // these things have no effect on the data ..
              return;
            }

            if ( eventParameter instanceof AttributeChange ) {
              final AttributeChange attributeChange = (AttributeChange) eventParameter;
              if ( AttributeNames.Internal.NAMESPACE.equals( attributeChange.getNamespace() ) ||
                AttributeNames.Internal.QUERY.equals( attributeChange.getName() ) ) {

                realModel.fireQueryChanged( attributeChange.getOldValue() );
                realModel.fireQueryChanged( attributeChange.getNewValue() );
              }
              return;
            }

            if ( eventParameter instanceof Expression ||
              eventParameter instanceof ReportParameterDefinition ) {
              realModel.fireTreeNodeChanged( eventParameter );
            } else if ( eventParameter instanceof SubReportParameterChange ) {
              if ( realModel instanceof SubReportDataTreeModel ) {
                final SubReportDataTreeModel subReportDataTreeModel = (SubReportDataTreeModel) realModel;
                realModel.fireTreeStructureChanged( subReportDataTreeModel.getReportParametersNode() );
              }
            } else {
              realModel.fireTreeDataChanged();
            }
          } else {
            realModel.fireTreeNodeChanged( event.getElement() );
          }
        }
      } finally {
        restoreState();
        expandAfterDataSourceEdit( event );
      }
    }

    private void handleNodeRemoved( final ReportModelEvent event, final AbstractReportDataTreeModel realModel ) {
      if ( event.getElement() == renderContext.getReportDefinition() ) {
        final Object eventParam = event.getParameter();
        if ( eventParam instanceof Expression ) {
          final Expression expression = (Expression) eventParam;
          realModel.fireExpressionRemoved( expression );
        } else if ( eventParam instanceof ParameterDefinitionEntry ) {
          final ParameterDefinitionEntry parameter = (ParameterDefinitionEntry) eventParam;
          if ( realModel instanceof MasterReportDataTreeModel ) {
            final MasterReportDataTreeModel masterModel = (MasterReportDataTreeModel) realModel;
            masterModel.fireParameterRemoved( parameter );
          }
        } else if ( eventParam instanceof DataFactory ) {
          realModel.fireTreeStructureChanged( realModel.getDataFactoryElement() );
        } else {
          realModel.fireTreeDataChanged();
        }
      }
    }

    private void handleNodeAdded( final ReportModelEvent event, final AbstractReportDataTreeModel realModel ) {
      if ( event.getElement() == renderContext.getReportDefinition() ) {
        final Object eventParam = event.getParameter();
        if ( eventParam instanceof Expression ) {
          final Expression expression = (Expression) eventParam;
          realModel.fireExpressionAdded( expression );
        } else if ( eventParam instanceof ParameterDefinitionEntry ) {
          final ParameterDefinitionEntry parameter = (ParameterDefinitionEntry) eventParam;
          if ( realModel instanceof MasterReportDataTreeModel ) {
            final MasterReportDataTreeModel masterModel = (MasterReportDataTreeModel) realModel;
            masterModel.fireParameterAdded( parameter );
          }
        } else if ( eventParam instanceof DataFactory ) {
          realModel.fireTreeStructureChanged( realModel.getDataFactoryElement() );
        } else {
          realModel.fireTreeDataChanged();
        }
      }
    }

    private void expandAfterDataSourceEdit( final ReportModelEvent event ) {
      final Object element = event.getElement();
      if ( !event.isNodeStructureChanged() && !event.isNodeAddedEvent() ) {
        return;
      }
      if ( element instanceof AbstractReportDefinition == false ) {
        return;
      }
      if ( treeModel instanceof AbstractReportDataTreeModel == false ) {
        return;
      }
      final AbstractReportDataTreeModel dataTreeModel = (AbstractReportDataTreeModel) treeModel;

      final Object parameter = event.getParameter();
      if ( parameter instanceof DataFactory ) {
        SwingUtilities.invokeLater( new ExpandDataFactoryNodesTask( dataTreeModel ) );
      } else if ( parameter instanceof Expression ) {
        SwingUtilities.invokeLater( new ExpandExpressionNodesTask( dataTreeModel ) );
      } else if ( parameter instanceof ReportParameterDefinition ) {
        SwingUtilities.invokeLater( new ExpandParameterDataSourceTask( dataTreeModel ) );
      }
    }
  }


  private class SettingsChangeHandler implements SettingsListener {
    private boolean showIndexColumns;

    private SettingsChangeHandler() {
      showIndexColumns = WorkspaceSettings.getInstance().isShowIndexColumns();
    }

    public void settingsChanged() {
      // revalidate the data model ..
      if ( showIndexColumns != WorkspaceSettings.getInstance().isShowIndexColumns() ) {
        showIndexColumns = WorkspaceSettings.getInstance().isShowIndexColumns();

        final TreeModel model = getModel();
        if ( model instanceof AbstractReportDataTreeModel ) {
          final AbstractReportDataTreeModel realModel = (AbstractReportDataTreeModel) model;
          realModel.fireTreeDataChanged();
          restoreState();
        }
      }
      invalidateLayoutCache();
    }
  }

  private class ExpandDataFactoryNodesTask implements Runnable {
    private AbstractReportDataTreeModel treeModel;

    private ExpandDataFactoryNodesTask( final AbstractReportDataTreeModel treeModel ) {
      this.treeModel = treeModel;
    }

    public void run() {
      if ( getModel() != treeModel ) {
        return;
      }

      final CompoundDataFactory compoundDataFactory = treeModel.getDataFactoryElement();
      final int size = compoundDataFactory.size();
      for ( int i = 0; i < size; i++ ) {
        final DataFactory df = compoundDataFactory.getReference( i );
        final TreePath path = treeModel.getPathForNode( df );
        if ( path == null ) {
          return;
        }

        expandPath( path );
        final int count = treeModel.getChildCount( df );
        for ( int x = 0; x < count; x++ ) {
          final Object child = treeModel.getChild( df, x );
          if ( child == null ) {
            continue;
          }
          final TreePath childPath = path.pathByAddingChild( child );
          expandPath( childPath );
        }
      }
    }
  }


  private class ExpandExpressionNodesTask implements Runnable {
    private AbstractReportDataTreeModel treeModel;

    private ExpandExpressionNodesTask( final AbstractReportDataTreeModel treeModel ) {
      this.treeModel = treeModel;
    }

    public void run() {
      if ( getModel() != treeModel ) {
        return;
      }

      final AbstractReportDataTreeModel dataTreeModel = treeModel;
      expandPath( new TreePath( new Object[] { dataTreeModel.getRoot(), dataTreeModel.getReportFunctionNode() } ) );
    }
  }

  private class ExpandParameterDataSourceTask implements Runnable {
    private AbstractReportDataTreeModel treeModel;

    private ExpandParameterDataSourceTask( final AbstractReportDataTreeModel treeModel ) {
      this.treeModel = treeModel;
    }

    public void run() {
      if ( getModel() != treeModel ) {
        return;
      }

      if ( treeModel instanceof MasterReportDataTreeModel ) {
        final MasterReportDataTreeModel dataTreeModel = (MasterReportDataTreeModel) treeModel;
        expandPath( new TreePath( new Object[] { dataTreeModel.getRoot(), dataTreeModel.getReportParametersNode() } ) );
      }

      if ( treeModel instanceof SubReportDataTreeModel ) {
        final SubReportDataTreeModel dataTreeModel = (SubReportDataTreeModel) treeModel;
        expandPath( new TreePath( new Object[] { dataTreeModel.getRoot(), dataTreeModel.getReportParametersNode() } ) );
      }
    }
  }


  private class ExpandEnvironmentDataSourceTask implements Runnable {
    private AbstractReportDataTreeModel treeModel;

    private ExpandEnvironmentDataSourceTask( final AbstractReportDataTreeModel treeModel ) {
      this.treeModel = treeModel;
    }

    public void run() {
      if ( getModel() != treeModel ) {
        return;
      }

      expandPath( new TreePath( new Object[] { treeModel.getRoot(), treeModel.getReportEnvironmentDataRow() } ) );
    }
  }


  private class ColumnTransferHandler extends TransferHandler {
    /**
     * Creates a <code>Transferable</code> to use as the source for a data transfer. Returns the representation of the
     * data to be transferred, or <code>null</code> if the component's property is <code>null</code>
     *
     * @param c the component holding the data to be transferred; this argument is provided to enable sharing of
     *          <code>TransferHandler</code>s by multiple components
     * @return the representation of the data to be transferred, or <code>null</code> if the property associated with
     * <code>c</code> is <code>null</code>
     */
    protected Transferable createTransferable( final JComponent c ) {
      if ( c != DataReportTree.this ) {
        return null;
      }

      final Object node = getSelectionPath().getLastPathComponent();
      if ( node instanceof ReportFieldNode ) {
        final ReportFieldNode field = (ReportFieldNode) node;
        return new FieldDescriptionTransferable( field.getFieldName() );
      }
      if ( node instanceof ParameterMapping ) {
        final Object o = getSelectionPath().getParentPath().getLastPathComponent();
        if ( o instanceof SubReportParametersNode.ImportParametersNode ) {
          final ParameterMapping field = (ParameterMapping) node;
          return new FieldDescriptionTransferable( field.getAlias() );
        }
      }
      if ( node instanceof ParameterDefinitionEntry ) {
        final ParameterDefinitionEntry field = (ParameterDefinitionEntry) node;
        return new FieldDescriptionTransferable( field.getName() );
      }
      if ( node instanceof Expression ) {
        final Expression expression = (Expression) node;
        if ( expression.getName() != null ) {
          return new FieldDescriptionTransferable( expression.getName() );
        }
      }
      return null;
    }

    public int getSourceActions( final JComponent c ) {
      return COPY;
    }
  }

  private class EditQueryDoubleClickHandler extends MouseAdapter {
    private EditQueryDoubleClickHandler() {
    }

    public void mouseClicked( final MouseEvent e ) {
      if ( e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1 ) {
        final TreePath selectionPath = getLeadSelectionPath();
        if ( selectionPath == null ) {
          return;
        }

        try {
          final Object selection = selectionPath.getLastPathComponent();
          if ( selection instanceof ParameterDefinitionEntry ) {
            final ParameterDefinitionEntry parameterDefinitionEntry = (ParameterDefinitionEntry) selection;
            EditParametersAction
              .performEditMasterReportParameters( getReportDesignerContext(), parameterDefinitionEntry );
            return;
          }

          if ( selection instanceof ParameterMapping ||
            selection instanceof SubReportParametersNode ) {
            EditParametersAction.performEditSubReportParameters( getReportDesignerContext() );
            return;
          }

          if ( selection instanceof DataFactory ) {
            final EditQueryAction action = new EditQueryAction();
            action.setReportDesignerContext( getReportDesignerContext() );
            action.performEdit( (DataFactory) selection, null );
            return;
          }

          if ( selection instanceof ReportQueryNode ) {
            final ReportQueryNode queryNode = (ReportQueryNode) selection;
            if ( queryNode.isAllowEdit() == false ) {
              return;
            }
            final EditQueryAction action = new EditQueryAction();
            action.setReportDesignerContext( getReportDesignerContext() );
            action.performEdit( queryNode.getDataFactory(), queryNode.getQueryName() );
            e.consume();
          }
        } catch ( ReportDataFactoryException e1 ) {
          UncaughtExceptionsModel.getInstance().addException( e1 );
        }

      }
    }
  }

  private ReportDocumentContext renderContext;
  private ReportUpdateHandler updateHandler;

  @SuppressWarnings( "FieldCanBeLocal" )
  private SettingsChangeHandler settingsChangeHandler;

  public DataReportTree() {
    updateHandler = new ReportUpdateHandler();

    setCellRenderer( new StructureTreeCellRenderer() );
    setTransferHandler( new ColumnTransferHandler() );
    setDragEnabled( true );
    setEditable( false );
    setRootVisible( false );

    addMouseListener( new EditQueryDoubleClickHandler() );

    settingsChangeHandler = new SettingsChangeHandler();
    WorkspaceSettings.getInstance().addSettingsListener( settingsChangeHandler );
  }

  public ReportDocumentContext getRenderContext() {
    return renderContext;
  }

  protected TreePath getPathForNode( final Object node ) {
    if ( getDataTreeModel() == null ) {
      return null;
    }

    return getDataTreeModel().getPathForNode( node );
  }

  public void setRenderContext( final ReportDocumentContext renderContext ) {
    if ( this.renderContext != null ) {
      this.renderContext.getSelectionModel().removeReportSelectionListener( getSelectionHandler() );
      this.renderContext.getReportDefinition().removeReportModelListener( updateHandler );
      this.renderContext.removeReportDataChangeListener( updateHandler );
    }
    this.renderContext = renderContext;
    if ( this.renderContext != null ) {
      this.renderContext.getSelectionModel().addReportSelectionListener( getSelectionHandler() );
      this.renderContext.getReportDefinition().addReportModelListener( updateHandler );
      this.renderContext.addReportDataChangeListener( updateHandler );
    }
    updateFromRenderContext();
    restoreState();
  }

  protected void updateFromRenderContext() {
    try {
      setUpdateFromExternalSource( true );

      if ( this.renderContext == null ) {
        setModel( EMPTY_MODEL );
        return;
      }

      final AbstractReportDefinition report = this.renderContext.getReportDefinition();
      final AbstractReportDataTreeModel model;
      if ( report instanceof MasterReport ) {
        model = new MasterReportDataTreeModel( renderContext );
      } else if ( report instanceof SubReport ) {
        model = new SubReportDataTreeModel( renderContext );
      } else {
        setModel( EMPTY_MODEL );
        return;
      }

      setModel( model );

      final DocumentContextSelectionModel selectionModel = renderContext.getSelectionModel();
      final Object[] selectedElements = selectionModel.getSelectedElements();
      final ArrayList<TreePath> selectionPaths = new ArrayList<TreePath>();
      for ( int i = 0; i < selectedElements.length; i++ ) {
        final Object o = selectedElements[ i ];
        final TreePath path = model.getPathForNode( o );
        if ( path != null ) {
          selectionPaths.add( path );
        }
      }
      getSelectionModel().setSelectionPaths( selectionPaths.toArray( new TreePath[ selectionPaths.size() ] ) );

      SwingUtilities.invokeLater( new ExpandDataFactoryNodesTask( model ) );
      SwingUtilities.invokeLater( new ExpandExpressionNodesTask( model ) );
      SwingUtilities.invokeLater( new ExpandParameterDataSourceTask( model ) );
      SwingUtilities.invokeLater( new ExpandEnvironmentDataSourceTask( model ) );
    } finally {
      setUpdateFromExternalSource( false );
    }
  }

  private AbstractReportDataTreeModel getDataTreeModel() {
    final TreeModel model = getModel();
    if ( model instanceof AbstractReportDataTreeModel ) {
      return (AbstractReportDataTreeModel) model;
    }
    return null;
  }
}
