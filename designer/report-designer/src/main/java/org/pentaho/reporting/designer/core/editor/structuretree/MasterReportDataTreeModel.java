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
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

public class MasterReportDataTreeModel extends AbstractReportDataTreeModel {
  private MasterReport masterReportElement;
  private ReportParametersNode reportParametersNode;
  private ParameterDefinitionEntry[] cachedParameterDefinitions;

  public MasterReportDataTreeModel( final ReportDocumentContext renderContext ) {
    super( renderContext );
    if ( renderContext.getReportDefinition() instanceof MasterReport == false ) {
      throw new IllegalArgumentException( "Instantiating a MasterReportDataTreeModel on a SubReport-Context" );
    }
    this.masterReportElement = (MasterReport) renderContext.getReportDefinition();
    this.reportParametersNode = new ReportParametersNode();
    refreshParameterCache();
  }

  protected ReportParametersNode getReportParametersNode() {
    return reportParametersNode;
  }

  public Object getRoot() {
    return masterReportElement;
  }

  public Object getChild( final Object parent, final int index ) {
    if ( parent == masterReportElement ) {
      switch( index ) {
        case 0:
          return masterReportElement.getDataFactory();
        case 1:
          return getReportFunctionNode();
        case 2:
          return getReportEnvironmentDataRow();
        case 3:
          return reportParametersNode;
        default:
          throw new IndexOutOfBoundsException();
      }
    }
    if ( parent == reportParametersNode ) {
      return masterReportElement.getParameterDefinition().getParameterDefinition( index );
    }
    return super.getChild( parent, index );
  }

  public int getChildCount( final Object parent ) {
    if ( parent == masterReportElement ) {
      return 4;
    }
    if ( parent == reportParametersNode ) {
      return masterReportElement.getParameterDefinition().getParameterCount();
    }
    return super.getChildCount( parent );
  }

  public boolean isLeaf( final Object node ) {
    if ( node instanceof ParameterDefinitionEntry ) {
      return true;
    }
    return super.isLeaf( node );
  }

  public int getIndexOfChild( final Object parent, final Object child ) {
    if ( parent == masterReportElement ) {
      if ( child == masterReportElement.getDataFactory() ) {
        return 0;
      }
      if ( child == getReportFunctionNode() ) {
        return 1;
      }
      if ( child == getReportEnvironmentDataRow() ) {
        return 2;
      }
      if ( child == reportParametersNode ) {
        return 3;
      }
      return -1;
    }
    if ( parent == reportParametersNode ) {
      final ReportParameterDefinition definition = masterReportElement.getParameterDefinition();

      for ( int i = 0; i < definition.getParameterCount(); i++ ) {
        final ParameterDefinitionEntry dataFactory = definition.getParameterDefinition( i );
        if ( dataFactory == child ) {
          return i;
        }
      }
      return -1;
    }

    return super.getIndexOfChild( parent, child );
  }

  public TreePath getPathForNode( final Object node ) {
    if ( node instanceof ParameterDefinitionEntry ) {
      final ReportParametersNode params = getReportParametersNode();
      if ( getIndexOfChild( params, node ) < 0 ) {
        return null;
      }
      return new TreePath( new Object[] { getRoot(), params, node } );
    }
    return super.getPathForNode( node );
  }

  public void fireTreeDataChanged() {
    super.fireTreeDataChanged();
    refreshParameterCache();
  }

  public void fireTreeStructureChanged( final Object element ) {
    super.fireTreeStructureChanged( element );
    refreshParameterCache();
  }

  public void fireParameterAdded( final ParameterDefinitionEntry parameter ) {
    final TreePath pathForNode = new TreePath( new Object[] { getRoot(), getReportParametersNode() } );
    final TreeModelListener[] treeModelListeners = getListeners();
    final int index = getIndexOfChild( getReportParametersNode(), parameter );
    if ( index == -1 ) {
      return;
    }

    final TreeModelEvent treeEvent = new TreeModelEvent( this, pathForNode,
      new int[] { index }, new Object[] { parameter } );
    for ( int i = treeModelListeners.length - 1; i >= 0; i -= 1 ) {
      final TreeModelListener listener = treeModelListeners[ i ];
      listener.treeNodesInserted( treeEvent );
    }
    refreshParameterCache();
  }

  public void fireParameterRemoved( final ParameterDefinitionEntry parameter ) {
    final TreePath pathForNode = new TreePath( new Object[] { getRoot(), getReportParametersNode() } );
    final TreeModelListener[] treeModelListeners = getListeners();
    final int index = findParameterInCache( parameter );
    if ( index == -1 ) {
      return;
    }

    final TreeModelEvent treeEvent = new TreeModelEvent( this, pathForNode,
      new int[] { index }, new Object[] { parameter } );
    for ( int i = treeModelListeners.length - 1; i >= 0; i -= 1 ) {
      final TreeModelListener listener = treeModelListeners[ i ];
      listener.treeNodesRemoved( treeEvent );
    }

    refreshParameterCache();
  }

  private int findParameterInCache( final ParameterDefinitionEntry parameter ) {
    if ( cachedParameterDefinitions == null ) {
      return -1;
    }
    for ( int i = 0; i < cachedParameterDefinitions.length; i++ ) {
      final ParameterDefinitionEntry cachedParameterDefinition = cachedParameterDefinitions[ i ];
      if ( parameter == cachedParameterDefinition ) {
        return i;
      }
    }
    return -1;
  }

  private void refreshParameterCache() {
    cachedParameterDefinitions = masterReportElement.getParameterDefinition().getParameterDefinitions();
  }

}
