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

package org.pentaho.reporting.designer.core.actions.global;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.ReportDesignerView;
import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Todo: Document Me
 *
 * @author Ezequiel Cuellar
 */
public abstract class AbstractViewStateAction extends AbstractReportContextAction {
  private class ViewStateChangeHandler implements PropertyChangeListener {
    private ViewStateChangeHandler() {
    }

    public void propertyChange( final PropertyChangeEvent evt ) {
      setEnabled( recomputeEnabled() );
    }
  }

  private ViewStateChangeHandler changeHandler;

  protected AbstractViewStateAction() {
    changeHandler = new ViewStateChangeHandler();
  }

  protected void updateDesignerContext( final ReportDesignerContext oldContext,
                                        final ReportDesignerContext newContext ) {
    if ( oldContext != null ) {
      oldContext.getView().removePropertyChangeListener( ReportDesignerView.REPORT_DESIGNER_VIEW_STATE_PROPERTY,
        changeHandler );
    }
    super.updateDesignerContext( oldContext, newContext );
    if ( newContext != null ) {
      newContext.getView().addPropertyChangeListener( ReportDesignerView.REPORT_DESIGNER_VIEW_STATE_PROPERTY,
        changeHandler );
    }
  }

  protected void updateActiveContext( final ReportRenderContext oldContext, final ReportRenderContext newContext ) {
    super.updateActiveContext( oldContext, newContext );
    if ( getReportDesignerContext() == null ) {
      setEnabled( false );
      return;
    }

    setEnabled( recomputeEnabled() );
  }

  protected abstract boolean recomputeEnabled();
}
