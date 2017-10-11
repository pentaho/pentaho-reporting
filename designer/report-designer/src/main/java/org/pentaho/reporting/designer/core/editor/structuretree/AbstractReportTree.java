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

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.model.selection.ReportSelectionEvent;
import org.pentaho.reporting.designer.core.model.selection.ReportSelectionListener;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import java.util.TreeSet;

public abstract class AbstractReportTree extends JTree {
  public static enum RenderType {
    REPORT, DATA
  }


  protected class RestoreStateTask implements Runnable {
    protected RestoreStateTask() {
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread
     * causes the object's <code>run</code> method to be called in that separately executing thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run() {
      if ( getRenderContext() == null ) {
        return;
      }

      for ( final Integer index : getExpandedNodes() ) {
        expandRow( index.intValue() );
      }
    }
  }

  private class ReportTreeExpansionListener implements TreeWillExpandListener {
    public void treeWillExpand( final TreeExpansionEvent event ) throws ExpandVetoException {
      addExpandedNode( getRowForPath( event.getPath() ) );
    }

    public void treeWillCollapse( final TreeExpansionEvent event ) throws ExpandVetoException {
      removeExpandedNode( getRowForPath( event.getPath() ) );
    }
  }


  private class TreeSelectionHandler implements TreeSelectionListener {
    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    public void valueChanged( final TreeSelectionEvent e ) {
      addExpandedNode( getRowForPath( e.getPath() ) );

      if ( updateFromExternalSource ) {
        return;
      }

      updateFromInternalSource = true;
      try {
        final ReportDocumentContext renderContext = getRenderContext();
        if ( renderContext == null ) {
          return;
        }

        final TreePath[] treePaths = getSelectionPaths();
        if ( treePaths == null ) {
          selectionModel.clearSelection();
          renderContext.getSelectionModel().clearSelection();
          return;
        }

        final DocumentContextSelectionModel selectionModel = renderContext.getSelectionModel();
        final Object[] data = new Object[ treePaths.length ];
        for ( int i = 0; i < treePaths.length; i++ ) {
          final TreePath path = treePaths[ i ];
          data[ i ] = path.getLastPathComponent();
        }
        selectionModel.setSelectedElements( data );
      } finally {
        updateFromInternalSource = false;
      }
    }
  }

  private class ReportSelectionHandler implements ReportSelectionListener {
    public void selectionAdded( final ReportSelectionEvent event ) {
      if ( isUpdateFromInternalSource() ) {
        return;
      }
      try {
        setUpdateFromExternalSource( true );

        final TreePath path = getPathForNode( event.getElement() );
        if ( path != null ) {
          addSelectionPath( path );
        }
      } finally {
        setUpdateFromExternalSource( false );
      }
    }

    public void selectionRemoved( final ReportSelectionEvent event ) {
      if ( isUpdateFromInternalSource() ) {
        return;
      }

      try {
        setUpdateFromExternalSource( true );

        final TreePath path = getPathForNode( event.getElement() );
        if ( path != null ) {
          removeSelectionPath( path );
        }
      } finally {
        setUpdateFromExternalSource( false );
      }
    }

    public void leadSelectionChanged( final ReportSelectionEvent event ) {
    }
  }

  protected static final DefaultTreeModel EMPTY_MODEL = new DefaultTreeModel( null, false );
  private ReportDesignerContext reportDesignerContext;
  private boolean updateFromInternalSource;
  private boolean updateFromExternalSource;
  private ReportSelectionHandler selectionHandler;

  public AbstractReportTree() {
    super( EMPTY_MODEL );
    selectionHandler = new ReportSelectionHandler();
    addTreeWillExpandListener( new ReportTreeExpansionListener() );
    getSelectionModel().addTreeSelectionListener( new TreeSelectionHandler() );
  }

  protected ReportSelectionHandler getSelectionHandler() {
    return selectionHandler;
  }

  protected abstract TreePath getPathForNode( Object node );

  public abstract void setRenderContext( final ReportDocumentContext renderContext );

  protected abstract ReportDocumentContext getRenderContext();

  public ReportDesignerContext getReportDesignerContext() {
    return reportDesignerContext;
  }

  public void setReportDesignerContext( final ReportDesignerContext reportDesignerContext ) {
    this.reportDesignerContext = reportDesignerContext;
  }

  protected TreeSet<Integer> getExpandedNodes() {
    final ReportDocumentContext renderContext = getRenderContext();
    if ( renderContext == null ) {
      // dummy operation..
      return new TreeSet<Integer>();
    }

    final Object property = renderContext.getProperties().get( "::layout-report-tree:expanded-nodes" );
    if ( property instanceof TreeSet ) {
      return (TreeSet<Integer>) property;
    }

    final TreeSet<Integer> retval = new TreeSet<Integer>();
    renderContext.getProperties().put( "::layout-report-tree:expanded-nodes", retval );
    return retval;
  }

  protected void addExpandedNode( final int row ) {
    getExpandedNodes().add( row );
  }

  protected void removeExpandedNode( final int row ) {
    getExpandedNodes().remove( row );
  }

  protected void restoreState() {
    SwingUtilities.invokeLater( new RestoreStateTask() );
  }

  protected boolean isUpdateFromInternalSource() {
    return updateFromInternalSource;
  }

  protected void setUpdateFromInternalSource( final boolean updateFromInternalSource ) {
    this.updateFromInternalSource = updateFromInternalSource;
  }

  protected boolean isUpdateFromExternalSource() {
    return updateFromExternalSource;
  }

  protected void setUpdateFromExternalSource( final boolean updateFromExternalSource ) {
    this.updateFromExternalSource = updateFromExternalSource;
  }

  protected void invalidateLayoutCache() {
    // this bit of magic invalidates the layout cache
    setCellRenderer( new StructureTreeCellRenderer() );
  }

}
