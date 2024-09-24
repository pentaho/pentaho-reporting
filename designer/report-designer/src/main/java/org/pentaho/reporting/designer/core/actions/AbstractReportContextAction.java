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
