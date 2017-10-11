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

package org.pentaho.reporting.designer.core.editor.structuretree;

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.model.DataSchemaUtility;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportEnvironmentDataRow;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeUtil;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.ExpressionCollection;
import org.pentaho.reporting.engine.classic.core.wizard.ContextAwareDataSchemaModel;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;

public abstract class AbstractReportDataTreeModel implements TreeModel {
  private EventListenerList eventListenerList;
  private ReportFunctionNode reportFunctionNode;
  private ReportDocumentContext context;
  private ReportEnvironmentDataRow reportEnvironmentDataRow;
  private Expression[] cachedExpressions;

  protected AbstractReportDataTreeModel( final ReportDocumentContext context ) {
    if ( context == null ) {
      throw new NullPointerException();
    }
    this.context = context;
    this.reportEnvironmentDataRow = new ReportEnvironmentDataRow( context.getContextRoot().getReportEnvironment() );
    this.eventListenerList = new EventListenerList();
    this.reportFunctionNode = new ReportFunctionNode();
    refreshExpressionsCache();
  }

  protected AbstractReportDefinition getReportDefinition() {
    return this.context.getReportDefinition();
  }

  public ReportEnvironmentDataRow getReportEnvironmentDataRow() {
    return reportEnvironmentDataRow;
  }

  protected ReportDocumentContext getContext() {
    return context;
  }

  public ReportFunctionNode getReportFunctionNode() {
    return reportFunctionNode;
  }

  protected CompoundDataFactory getDataFactoryElement() {
    return (CompoundDataFactory) context.getReportDefinition().getDataFactory();
  }

  protected ExpressionCollection getExpressions() {
    return context.getReportDefinition().getExpressions();
  }

  public Object getChild( final Object parent, final int index ) {
    if ( parent == getDataFactoryElement() ) {
      final CompoundDataFactory dataFactoryElement = getDataFactoryElement();
      return dataFactoryElement.getReference( index );
    }
    if ( parent == reportEnvironmentDataRow ) {
      final String[] columnNames = reportEnvironmentDataRow.getColumnNames();
      final String name = columnNames[ index ];
      final Class targetClass = reportEnvironmentDataRow.isArray( name ) ? Object[].class : Object.class;
      return new ReportFieldNode( getContext(), name, targetClass );
    }
    if ( parent == reportFunctionNode ) {
      return getExpressions().getExpression( index );
    }
    if ( parent instanceof DataFactory ) {
      final DataFactory dataFactory = (DataFactory) parent;
      final String[] queryNames = dataFactory.getQueryNames();
      return new ReportQueryNode( dataFactory, queryNames[ index ], true );
    }
    if ( parent instanceof ReportQueryNode ) {
      final ReportQueryNode queryNode = (ReportQueryNode) parent;
      if ( isSelectedDataSource( queryNode ) ) {
        final String[] names = getDataFactoryColumns();
        final String name = names[ index ];
        final ContextAwareDataSchemaModel model = context.getReportDataSchemaModel();
        final DataAttributes attributes = model.getDataSchema().getAttributes( name );
        final Class type = (Class) attributes.getMetaAttribute
          ( MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.TYPE, Class.class,
            model.getDataAttributeContext() );
        final DataFactory dataFactory = queryNode.getDataFactory();
        return new ReportFieldNode( context, dataFactory, name, type );
      }

      throw new IndexOutOfBoundsException();
    }
    return null;
  }

  private boolean isSelectedDataSource( final ReportQueryNode queryNode ) {
    AbstractReportDefinition reportDefinition = context.getReportDefinition();
    return DesignTimeUtil
      .isSelectedDataSource( reportDefinition, queryNode.getDataFactory(), queryNode.getQueryName() );
  }

  private String[] getDataFactoryColumns() {
    final ContextAwareDataSchemaModel model = context.getReportDataSchemaModel();
    final String[] columnNames = model.getColumnNames();
    final ArrayList<String> targetCols = new ArrayList<String>( columnNames.length );
    for ( int i = 0; i < columnNames.length; i++ ) {
      final String columnName = columnNames[ i ];
      final DataAttributes attributes = model.getDataSchema().getAttributes( columnName );
      if ( attributes == null ) {
        // if in doubt, then do not add.
        continue;
      }
      if ( DataSchemaUtility.isFiltered( attributes, model.getDataAttributeContext() ) ) {
        continue;
      }

      if ( "table".equals( attributes.getMetaAttribute
        ( MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.SOURCE,
          String.class, model.getDataAttributeContext() ) ) ) {
        targetCols.add( columnName );
      }

    }
    return targetCols.toArray( new String[ targetCols.size() ] );
  }

  public int getChildCount( final Object parent ) {
    if ( parent == reportEnvironmentDataRow ) {
      return reportEnvironmentDataRow.getColumnNames().length;
    }
    if ( parent == getDataFactoryElement() ) {
      final CompoundDataFactory dataFactoryElement = getDataFactoryElement();
      return dataFactoryElement.size();
    }
    if ( parent == reportFunctionNode ) {
      return getExpressions().size();
    }
    if ( parent instanceof DataFactory ) {
      // a DataFactory is a leaf if it has no columns defined
      final DataFactory df = (DataFactory) parent;
      return df.getQueryNames().length;
    }
    if ( parent instanceof ReportQueryNode ) {
      final ReportQueryNode queryNode = (ReportQueryNode) parent;
      if ( isSelectedDataSource( queryNode ) ) {
        return getDataFactoryColumns().length;
      }
      return 0;
    }
    return 0;
  }

  public boolean isLeaf( final Object node ) {
    if ( node == getDataFactoryElement() ) {
      return false;
    }

    if ( node instanceof DataFactory ) {
      final DataFactory dataFactory = (DataFactory) node;
      return dataFactory.getQueryNames().length == 0;
    }

    if ( node instanceof ReportQueryNode ) {
      final ReportQueryNode queryNode = (ReportQueryNode) node;
      if ( isSelectedDataSource( queryNode ) ) {
        return false;
      }
      return true;
    }
    if ( node instanceof Expression ) {
      return true;
    }
    if ( node instanceof ReportFieldNode ) {
      return true;
    }
    return false;
  }

  public int getIndexOfChild( final Object parent, final Object child ) {
    if ( parent == reportEnvironmentDataRow ) {
      if ( child instanceof ReportFieldNode == false ) {
        return -1;
      }
      final ReportFieldNode fieldNode = (ReportFieldNode) child;
      final String[] columnNames = reportEnvironmentDataRow.getColumnNames();
      for ( int i = 0; i < columnNames.length; i++ ) {
        final String columnName = columnNames[ i ];
        if ( columnName.equals( fieldNode.getFieldName() ) ) {
          return i;
        }
      }
      return -1;
    }

    if ( parent == getDataFactoryElement() ) {
      final CompoundDataFactory dataFactoryElement = getDataFactoryElement();
      for ( int i = 0; i < dataFactoryElement.size(); i++ ) {
        final DataFactory dataFactory = dataFactoryElement.getReference( i );
        if ( dataFactory == child ) {
          return i;
        }
      }
      return -1;
    }
    if ( parent instanceof DataFactory ) {
      if ( child instanceof ReportQueryNode == false ) {
        return -1;
      }
      final ReportQueryNode rfn = (ReportQueryNode) child;
      if ( rfn.getDataFactory() != parent ) {
        return -1;
      }
      final String[] queryNames = rfn.getDataFactory().getQueryNames();
      return indexOf( queryNames, rfn.getQueryName() );
    }
    if ( parent instanceof ReportQueryNode ) {
      final ReportQueryNode queryNode = (ReportQueryNode) parent;
      if ( child instanceof ReportFieldNode == false ) {
        return -1;
      }
      final ReportFieldNode node = (ReportFieldNode) child;
      if ( node.getSource() != queryNode.getDataFactory() ) {
        return -1;
      }
      if ( isSelectedDataSource( queryNode ) == false ) {
        return -1;
      }
      return indexOf( getDataFactoryColumns(), node.getFieldName() );
    }
    if ( parent == reportFunctionNode ) {
      final ExpressionCollection expressionCollection = getExpressions();
      for ( int i = 0; i < expressionCollection.size(); i++ ) {
        final Expression dataFactory = expressionCollection.getExpression( i );
        if ( dataFactory == child ) {
          return i;
        }
      }
      return -1;
    }

    return -1;
  }

  protected int indexOf( final String[] array, final String key ) {
    for ( int i = 0; i < array.length; i++ ) {
      final String value = array[ i ];
      if ( ObjectUtilities.equal( key, value ) ) {
        return i;
      }
    }
    return -1;
  }

  public void valueForPathChanged( final TreePath path, final Object newValue ) {
    // wont happen, we are not editable
  }

  public void fireTreeDataChanged() {
    final TreeModelListener[] treeModelListeners = getListeners();
    final TreeModelEvent treeEvent = new TreeModelEvent( this, new TreePath( getRoot() ) );
    //    for (int i = treeModelListeners.length - 1; i >= 0; i -= 1)
    for ( int i = 0; i < treeModelListeners.length; i++ ) {
      final TreeModelListener listener = treeModelListeners[ i ];
      listener.treeStructureChanged( treeEvent );
    }

    refreshExpressionsCache();
  }

  private void refreshExpressionsCache() {
    this.cachedExpressions = getReportDefinition().getExpressions().getExpressions();
  }

  protected TreeModelListener[] getListeners() {
    return eventListenerList.getListeners( TreeModelListener.class );
  }

  public void addTreeModelListener( final TreeModelListener l ) {
    if ( l == null ) {
      throw new NullPointerException();
    }
    eventListenerList.add( TreeModelListener.class, l );
  }

  public void removeTreeModelListener( final TreeModelListener l ) {
    if ( l == null ) {
      throw new NullPointerException();
    }
    eventListenerList.remove( TreeModelListener.class, l );
  }

  public void fireTreeNodeChanged( final Object element ) {
    TreePath path = getPathForNode( element );
    if ( path == null ) {
      // if we cannot come up with a sensible path, we will take the root and hope the best
      path = new TreePath( getRoot() );
    }

    final TreeModelListener[] treeModelListeners = getListeners();
    final TreeModelEvent treeEvent = new TreeModelEvent( this, path );
    for ( int i = treeModelListeners.length - 1; i >= 0; i -= 1 ) {
      final TreeModelListener listener = treeModelListeners[ i ];
      listener.treeNodesChanged( treeEvent );
    }
  }

  public void fireTreeStructureChanged( final Object element ) {
    TreePath path = getPathForNode( element );
    if ( path == null ) {
      // if we cannot come up with a sensible path, we will take the root and hope the best
      path = new TreePath( getRoot() );
    }

    final TreeModelListener[] treeModelListeners = getListeners();
    final TreeModelEvent treeEvent = new TreeModelEvent( this, path );
    for ( int i = treeModelListeners.length - 1; i >= 0; i -= 1 ) {
      final TreeModelListener listener = treeModelListeners[ i ];
      listener.treeStructureChanged( treeEvent );
    }

    refreshExpressionsCache();
  }

  public void fireQueryChanged( final Object query ) {
    if ( query == null ) {
      return;
    }

    final ArrayList<ReportQueryNode> nodes = new ArrayList<ReportQueryNode>();
    findAllQueryNodes( query, nodes, getDataFactoryElement() );

    final TreeModelListener[] treeModelListeners = getListeners();
    for ( int n = 0; n < nodes.size(); n++ ) {
      final ReportQueryNode queryNode = nodes.get( n );
      final TreePath path = getPathForNode( queryNode.getDataFactory() );
      final TreePath queryPath = path.pathByAddingChild( queryNode );

      final TreeModelEvent treeEvent = new TreeModelEvent( this, queryPath );
      for ( int i = treeModelListeners.length - 1; i >= 0; i -= 1 ) {
        final TreeModelListener listener = treeModelListeners[ i ];
        listener.treeStructureChanged( treeEvent );
      }
    }
  }

  protected void findAllQueryNodes( final Object query, final ArrayList<ReportQueryNode> nodes,
                                    final Object element ) {
    final int childCount = getChildCount( element );
    for ( int i = 0; i < childCount; i += 1 ) {
      final Object child = getChild( element, i );
      if ( child instanceof ReportQueryNode ) {
        final ReportQueryNode queryNode = (ReportQueryNode) child;
        if ( query.equals( queryNode.getQueryName() ) ) {
          nodes.add( queryNode );
        }
      }
      if ( isLeaf( child ) == false ) {
        findAllQueryNodes( query, nodes, child );
      }
    }
  }

  public void fireExpressionAdded( final Expression parameter ) {
    final TreePath pathForNode = new TreePath( new Object[] { getRoot(), getReportFunctionNode() } );
    final TreeModelListener[] treeModelListeners = getListeners();
    final int index = getIndexOfChild( getReportFunctionNode(), parameter );
    if ( index == -1 ) {
      return;
    }

    final TreeModelEvent treeEvent = new TreeModelEvent( this, pathForNode,
      new int[] { index }, new Object[] { parameter } );
    for ( int i = treeModelListeners.length - 1; i >= 0; i -= 1 ) {
      final TreeModelListener listener = treeModelListeners[ i ];
      listener.treeNodesInserted( treeEvent );
    }

    refreshExpressionsCache();
  }

  public void fireExpressionRemoved( final Expression parameter ) {
    final TreePath pathForNode = new TreePath( new Object[] { getRoot(), getReportFunctionNode() } );
    final TreeModelListener[] treeModelListeners = getListeners();
    final int index = findExpressionInCache( parameter );
    if ( index == -1 ) {
      return;
    }

    final TreeModelEvent treeEvent = new TreeModelEvent( this, pathForNode,
      new int[] { index }, new Object[] { parameter } );
    for ( int i = treeModelListeners.length - 1; i >= 0; i -= 1 ) {
      final TreeModelListener listener = treeModelListeners[ i ];
      listener.treeNodesRemoved( treeEvent );
    }

    refreshExpressionsCache();
  }

  private int findExpressionInCache( final Expression parameter ) {
    if ( cachedExpressions == null ) {
      return -1;
    }
    for ( int i = 0; i < cachedExpressions.length; i++ ) {
      Expression cachedExpression = cachedExpressions[ i ];
      if ( cachedExpression == parameter ) {
        return i;
      }
    }
    return -1;
  }

  public TreePath getPathForNode( final Object node ) {
    if ( node == getRoot() ) {
      return new TreePath( new Object[] { getRoot() } );
    }

    if ( node instanceof Expression ) {
      final ReportFunctionNode functions = getReportFunctionNode();
      if ( getIndexOfChild( functions, node ) < 0 ) {
        return null;
      }
      return new TreePath( new Object[] { getRoot(), functions, node } );
    }

    if ( node instanceof DataFactory ) {
      final DataFactory dataFactoryElement = getDataFactoryElement();
      if ( getIndexOfChild( dataFactoryElement, node ) < 0 ) {
        return null;
      }
      return new TreePath( new Object[] { getRoot(), dataFactoryElement, node } );
    }

    if ( node == getDataFactoryElement() ) {
      return new TreePath( new Object[] { getRoot(), getDataFactoryElement() } );
    }
    return null;
  }

}
