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

package org.pentaho.reporting.designer.core.actions;

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.model.selection.ReportSelectionEvent;
import org.pentaho.reporting.designer.core.model.selection.ReportSelectionListener;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportModelListener;

public abstract class AbstractElementSelectionAction extends AbstractReportContextAction {
  private class SelectionUpdateHandler implements ReportSelectionListener {
    private SelectionUpdateHandler() {
    }

    public void selectionAdded( final ReportSelectionEvent event ) {
      updateSelection();
    }

    public void selectionRemoved( final ReportSelectionEvent event ) {
      updateSelection();
    }

    public void leadSelectionChanged( final ReportSelectionEvent event ) {
      updateSelection();
    }
  }

  protected class UpdatePropertiesForSelectionHandler implements ReportModelListener {
    private UpdatePropertiesForSelectionHandler() {
    }

    public void nodeChanged( final ReportModelEvent event ) {
      final ReportDocumentContext activeContext = getActiveContext();
      if ( activeContext == null ) {
        throw new IllegalStateException( "Stale Action reference!" );
      }
      if ( activeContext.getSelectionModel().isSelected( event.getElement() ) ) {
        selectedElementPropertiesChanged( event );
      }
    }
  }


  private DocumentContextSelectionModel selectionModel;
  private SelectionUpdateHandler updateHandler;
  private UpdatePropertiesForSelectionHandler updateSelectionHandler;

  protected AbstractElementSelectionAction() {
    updateHandler = new SelectionUpdateHandler();
    updateSelectionHandler = new UpdatePropertiesForSelectionHandler();
  }

  protected DocumentContextSelectionModel getSelectionModel() {
    return selectionModel;
  }

  protected void updateActiveContext( final ReportRenderContext oldContext,
                                      final ReportRenderContext newContext ) {
    if ( this.selectionModel != null ) {
      this.selectionModel.removeReportSelectionListener( updateHandler );
    }
    if ( oldContext != null ) {
      oldContext.getReportDefinition().removeReportModelListener( updateSelectionHandler );
    }
    if ( newContext != null ) {
      this.selectionModel = newContext.getSelectionModel();
      this.selectionModel.addReportSelectionListener( updateHandler );
      updateSelection();
    } else {
      this.selectionModel = null;
      updateSelection();
    }
    if ( newContext != null ) {
      newContext.getReportDefinition().addReportModelListener( updateSelectionHandler );
    }
  }

  protected abstract void selectedElementPropertiesChanged( final ReportModelEvent event );

  protected void updateSelection() {
    if ( selectionModel == null ) {
      setEnabled( false );
      return;
    }

    setEnabled( selectionModel.getSelectionCount() > 0 );
  }

  protected boolean isSingleElementSelection() {
    if ( selectionModel == null ) {
      return false;
    }
    if ( selectionModel.getSelectionCount() != 1 ) {
      return false;
    }
    return true;
  }
}
