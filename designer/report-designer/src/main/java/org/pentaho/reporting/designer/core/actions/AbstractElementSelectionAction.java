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
