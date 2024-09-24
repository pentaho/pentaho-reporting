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

import org.pentaho.reporting.designer.core.DesignerContextComponent;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.model.selection.ReportSelectionEvent;
import org.pentaho.reporting.designer.core.model.selection.ReportSelectionListener;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportModelListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public abstract class ElementSelectionComponentSupport implements DesignerContextComponent {
  private ReportDesignerContext reportDesignerContext;

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

    }
  }

  private class ReportModelChangeHandler implements ReportModelListener {
    private ReportModelChangeHandler() {
    }

    public void nodeChanged( final ReportModelEvent event ) {
      ElementSelectionComponentSupport.this.nodeChanged( event );
    }
  }

  private DocumentContextSelectionModel selectionModel;
  private SelectionUpdateHandler updateHandler;
  private ActiveContextChangeHandler changeHandler;
  private ReportModelChangeHandler modelChangeHandler;

  public ElementSelectionComponentSupport() {
    updateHandler = new SelectionUpdateHandler();
    changeHandler = new ActiveContextChangeHandler();
    modelChangeHandler = new ReportModelChangeHandler();
  }

  protected void updateDesignerContext( final ReportDesignerContext oldContext,
                                        final ReportDesignerContext newContext ) {
    if ( oldContext != null ) {
      oldContext.removePropertyChangeListener( ReportDesignerContext.ACTIVE_CONTEXT_PROPERTY, changeHandler );
      updateActiveContext( oldContext.getActiveContext(), null );
    }

    if ( newContext != null ) {
      newContext.addPropertyChangeListener( ReportDesignerContext.ACTIVE_CONTEXT_PROPERTY, changeHandler );
      updateActiveContext( null, newContext.getActiveContext() );
    }

  }

  public DocumentContextSelectionModel getSelectionModel() {
    return selectionModel;
  }

  protected ReportDocumentContext getActiveContext() {
    return getReportDesignerContext().getActiveContext();
  }

  protected void updateActiveContext( final ReportDocumentContext oldContext,
                                      final ReportDocumentContext newContext ) {
    if ( oldContext != null ) {
      oldContext.getReportDefinition().removeReportModelListener( modelChangeHandler );
    }
    if ( this.selectionModel != null ) {
      this.selectionModel.removeReportSelectionListener( updateHandler );
    }

    if ( newContext != null ) {
      this.selectionModel = newContext.getSelectionModel();
      this.selectionModel.addReportSelectionListener( updateHandler );
      updateSelection();
      newContext.getReportDefinition().addReportModelListener( modelChangeHandler );
    } else {
      this.selectionModel = null;
      updateSelection();
    }
  }

  protected abstract void updateSelection();

  protected abstract void nodeChanged( ReportModelEvent event );

  protected boolean isSingleElementSelection() {
    if ( selectionModel == null ) {
      return false;
    }
    if ( selectionModel.getSelectionCount() != 1 ) {
      return false;
    }
    return true;
  }

  public void setReportDesignerContext( final ReportDesignerContext context ) {
    final ReportDesignerContext old = this.reportDesignerContext;
    this.reportDesignerContext = context;
    updateDesignerContext( old, reportDesignerContext );
  }

  public ReportDesignerContext getReportDesignerContext() {
    return reportDesignerContext;
  }

}
