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
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.designtime.AttributeChange;
import org.pentaho.reporting.engine.classic.core.designtime.StyleChange;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportModelListener;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class LayoutReportTree extends AbstractReportTree {
  private class ReportUpdateHandler implements ReportModelListener {
    private ReportUpdateHandler() {
    }

    public void nodeChanged( final ReportModelEvent event ) {
      final ReportStructureTreeModel model = getStructureModel();
      if ( model == null ) {
        return;
      }
      try {
        if ( event.isNodeStructureChanged() || event.isNodeAddedEvent() || event.isNodeDeleteEvent() ) {
          model.fireTreeDataChanged( event.getSource() );
        } else if ( event.getType() == ReportModelEvent.NODE_PROPERTIES_CHANGED ) {
          final Object eventParameter = event.getParameter();
          if ( eventParameter instanceof AttributeChange ) {
            final AttributeChange attributeChange = (AttributeChange) eventParameter;
            if ( AttributeNames.Core.NAMESPACE.equals( attributeChange.getNamespace() ) ) {
              if ( AttributeNames.Core.NAME.equals( attributeChange.getName() ) ||
                AttributeNames.Core.FIELD.equals( attributeChange.getName() ) ||
                AttributeNames.Core.VALUE.equals( attributeChange.getName() ) ||
                AttributeNames.Core.RESOURCE_IDENTIFIER.equals( attributeChange.getName() ) ) {
                invalidateLayoutCache();
              }
            }
            return;
          }

          final Object element = event.getElement();
          if ( element instanceof ReportElement ) {
            if ( element instanceof Section && eventParameter instanceof StyleChange ) {
              final StyleChange change = (StyleChange) eventParameter;
              if ( BandStyleKeys.LAYOUT.equals( change.getStyleKey() ) ) {
                invalidateLayoutCache();
                model.fireTreeStructureChanged( element );
                return;
              }
            }
            model.fireTreeNodeChanged( event.getElement() );
          } else {
            model.fireTreeNodeChanged( event.getReport() );
          }
        }
      } finally {
        restoreState();
      }
    }

  }

  private ReportDocumentContext renderContext;
  private ReportUpdateHandler updateHandler;

  public LayoutReportTree() {
    updateHandler = new ReportUpdateHandler();

    setCellRenderer( new StructureTreeCellRenderer() );
    setDragEnabled( false );
    setEditable( false );
  }

  protected TreePath getPathForNode( final Object node ) {
    if ( getStructureModel() == null ) {
      return null;
    }

    return TreeSelectionHelper.getPathForNode( getStructureModel(), node );
  }

  public ReportDocumentContext getRenderContext() {
    return renderContext;
  }

  public void setRenderContext( final ReportDocumentContext renderContext ) {
    if ( this.renderContext != null ) {
      this.renderContext.getSelectionModel().removeReportSelectionListener( getSelectionHandler() );
      this.renderContext.getReportDefinition().removeReportModelListener( updateHandler );
    }
    this.renderContext = renderContext;
    if ( this.renderContext != null ) {
      this.renderContext.getSelectionModel().addReportSelectionListener( getSelectionHandler() );
      this.renderContext.getReportDefinition().addReportModelListener( updateHandler );
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
      setModel( new ReportStructureTreeModel( report ) );
    } finally {
      setUpdateFromExternalSource( false );
    }
  }

  private ReportStructureTreeModel getStructureModel() {
    final TreeModel model = getModel();
    if ( model instanceof ReportStructureTreeModel ) {
      return (ReportStructureTreeModel) model;
    }
    return null;
  }
}
