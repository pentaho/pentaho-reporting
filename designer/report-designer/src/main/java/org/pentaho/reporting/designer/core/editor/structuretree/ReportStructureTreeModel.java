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

import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.function.ExpressionCollection;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class ReportStructureTreeModel implements TreeModel {
  private AbstractReportDefinition report;
  private EventListenerList eventListenerList;

  public ReportStructureTreeModel( final AbstractReportDefinition masterReportElement ) {
    if ( masterReportElement == null ) {
      throw new NullPointerException();
    }

    this.report = masterReportElement;
    this.eventListenerList = new EventListenerList();
  }

  protected CompoundDataFactory getDataFactoryElement() {
    return (CompoundDataFactory) report.getDataFactory();
  }

  protected ExpressionCollection getExpressions() {
    return report.getExpressions();
  }

  public AbstractReportDefinition getReport() {
    return report;
  }

  public Object getRoot() {
    return report;
  }

  public Object getChild( final Object parent, final int index ) {
    if ( parent instanceof Section == false ) {
      throw new IndexOutOfBoundsException();
    }
    if ( parent instanceof RootLevelBand ) {
      final Section re = (Section) parent;
      if ( index >= re.getElementCount() ) {
        final int subReportIndex = index - re.getElementCount();
        final RootLevelBand rl = (RootLevelBand) parent;
        return rl.getSubReport( subReportIndex );
      }
    }

    final Section br = (Section) parent;
    if ( isStrictOrderNeeded( br ) ) {
      return br.getElement( index );
    } else {
      return br.getElement( br.getElementCount() - index - 1 );
    }
  }

  private boolean isStrictOrderNeeded( final Section section ) {
    if ( section instanceof AbstractReportDefinition ||
      section instanceof Group ||
      section instanceof GroupBody ) {
      return true;
    }

    if ( section instanceof Band ) {
      final Band b = (Band) section;
      final Object o = b.getStyle().getStyleProperty( BandStyleKeys.LAYOUT );
      if ( o == null || "canvas".equals( o ) ) // NON-NLS
      {
        return false;
      }
      return true;
    }
    return false;
  }

  public int getChildCount( final Object parent ) {
    if ( parent != report && parent instanceof SubReport ) {
      return 0;
    }
    if ( parent instanceof RootLevelBand && parent instanceof Section ) {
      final RootLevelBand re = (RootLevelBand) parent;
      final Section se = (Section) parent;
      return re.getSubReportCount() + se.getElementCount();
    }
    if ( parent instanceof Section ) {
      final Section br = (Section) parent;
      return br.getElementCount();
    }
    return 0;
  }

  public int getIndexOfChild( final Object parent, final Object child ) {
    if ( child != report && child instanceof SubReport ) {
      return -1;
    }
    if ( parent instanceof Section == false ) {
      return -1;
    }

    final Section br = (Section) parent;
    if ( parent instanceof RootLevelBand && child instanceof SubReport ) {
      final RootLevelBand re = (RootLevelBand) parent;
      final int subreportIndexOf = ModelUtility.findSubreportIndexOf( re, (SubReport) child );
      if ( subreportIndexOf != -1 ) {
        return br.getElementCount() + subreportIndexOf;
      }
    }

    if ( child instanceof Element == false ) {
      return -1;
    }

    if ( isStrictOrderNeeded( br ) ) {
      return ModelUtility.findIndexOf( br, (Element) child );
    } else {
      return br.getElementCount() - ModelUtility.findIndexOf( br, (Element) child ) - 1;
    }
  }

  public boolean isLeaf( final Object node ) {
    if ( node instanceof Element && node instanceof Section == false ) {
      return true;
    }
    if ( node instanceof SubReport && node != getRoot() ) {
      return true;
    }

    return false;
  }

  public void valueForPathChanged( final TreePath path, final Object newValue ) {
    // emoty ..
  }

  public void fireTreeDataChanged( final Object source ) {
    final TreeModelListener[] treeModelListeners = eventListenerList.getListeners( TreeModelListener.class );
    final TreeModelEvent treeEvent = new TreeModelEvent( this, TreeSelectionHelper.getPathForNode( this, source ) );
    for ( int i = treeModelListeners.length - 1; i >= 0; i -= 1 ) {
      final TreeModelListener listener = treeModelListeners[ i ];
      listener.treeStructureChanged( treeEvent );
    }
  }

  public void addTreeModelListener( final TreeModelListener l ) {
    eventListenerList.add( TreeModelListener.class, l );
  }

  public void removeTreeModelListener( final TreeModelListener l ) {
    eventListenerList.remove( TreeModelListener.class, l );
  }


  public void fireTreeNodeChanged( final Object element ) {
    final TreePath path = TreeSelectionHelper.getPathForNode( this, element );
    final TreeModelListener[] treeModelListeners = eventListenerList.getListeners( TreeModelListener.class );
    final TreeModelEvent treeEvent = new TreeModelEvent( this, path );
    for ( int i = treeModelListeners.length - 1; i >= 0; i -= 1 ) {
      final TreeModelListener listener = treeModelListeners[ i ];
      listener.treeNodesChanged( treeEvent );
    }
  }

  public void fireTreeStructureChanged( final Object element ) {
    TreePath path = TreeSelectionHelper.getPathForNode( this, element );
    if ( path == null ) {
      // if we cannot come up with a sensible path, we will take the root and hope the best
      path = new TreePath( getRoot() );
    }

    final TreeModelListener[] treeModelListeners = eventListenerList.getListeners( TreeModelListener.class );
    final TreeModelEvent treeEvent = new TreeModelEvent( this, path );
    for ( int i = treeModelListeners.length - 1; i >= 0; i -= 1 ) {
      final TreeModelListener listener = treeModelListeners[ i ];
      listener.treeStructureChanged( treeEvent );
    }
  }
}
