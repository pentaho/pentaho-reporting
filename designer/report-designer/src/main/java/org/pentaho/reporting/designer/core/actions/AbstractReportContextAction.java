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

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public abstract class AbstractReportContextAction extends AbstractDesignerContextAction {
  private class ActiveContextChangeHandler implements PropertyChangeListener {
    private ActiveContextChangeHandler() {
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */

    public void propertyChange( final PropertyChangeEvent evt ) {
      final ReportRenderContext oldContext = (ReportRenderContext) evt.getOldValue();
      final ReportRenderContext activeContext = (ReportRenderContext) evt.getNewValue();
      updateActiveContext( oldContext, activeContext );
    }
  }

  private ActiveContextChangeHandler changeHandler;

  protected AbstractReportContextAction() {
    changeHandler = new ActiveContextChangeHandler();
  }

  protected ReportDocumentContext getActiveContext() {
    if ( getReportDesignerContext() == null ) {
      return null;
    }
    return getReportDesignerContext().getActiveContext();
  }

  protected void updateDesignerContext( final ReportDesignerContext oldContext,
                                        final ReportDesignerContext newContext ) {
    if ( oldContext != null ) {
      oldContext.removePropertyChangeListener( this.changeHandler );
      final ReportDocumentContext oldActiveContext = getActiveContext();
      updateActiveContext( oldActiveContext, null );
    }
    super.updateDesignerContext( oldContext, newContext );
    if ( newContext != null ) {
      newContext.addPropertyChangeListener( ReportDesignerContext.ACTIVE_CONTEXT_PROPERTY, changeHandler );
      updateActiveContext( null, newContext.getActiveContext() );
    }
  }

  protected void updateActiveContext( final ReportDocumentContext oldContext,
                                      final ReportDocumentContext newContext ) {
    setEnabled( newContext != null );

    ReportRenderContext oldCtx = null;
    ReportRenderContext newCtx = null;
    if ( oldContext instanceof ReportRenderContext ) {
      oldCtx = (ReportRenderContext) oldContext;
    }
    if ( newContext instanceof ReportRenderContext ) {
      newCtx = (ReportRenderContext) newContext;
    }
    updateActiveContext( oldCtx, newCtx );
  }

  protected void updateActiveContext( final ReportRenderContext oldContext,
                                      final ReportRenderContext newContext ) {
    setEnabled( newContext != null );
  }
}
