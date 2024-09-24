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

package org.pentaho.reporting.designer.core.util;

import org.pentaho.reporting.designer.core.DesignerContextComponent;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.model.selection.ReportSelectionEvent;
import org.pentaho.reporting.designer.core.model.selection.ReportSelectionListener;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Although in the report designer the designer-context never changes, for better testability of the side-panels we
 * allow the tests to define a own context and track that here.
 *
 * @author Thomas Morgner
 */
public abstract class SidePanel extends JPanel implements DesignerContextComponent {
  private class ActiveContextChangeHandler implements PropertyChangeListener {
    private ActiveContextChangeHandler() {
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */

    public void propertyChange( final PropertyChangeEvent evt ) {
      updateActiveContext( (ReportDocumentContext) evt.getOldValue(), (ReportDocumentContext) evt.getNewValue() );
    }
  }

  private class SelectionHandler implements ReportSelectionListener {
    public void selectionAdded( final ReportSelectionEvent event ) {
      updateSelection( event.getModel() );
    }

    public void selectionRemoved( final ReportSelectionEvent event ) {
      updateSelection( event.getModel() );
    }

    public void leadSelectionChanged( final ReportSelectionEvent event ) {

    }
  }


  private ReportDesignerContext reportDesignerContext;
  private ActiveContextChangeHandler contextChangeHandler;
  private SelectionHandler selectionHandler;

  /**
   * Creates a new <code>JPanel</code> with a double buffer and a flow layout.
   */
  protected SidePanel() {
    contextChangeHandler = new ActiveContextChangeHandler();
    selectionHandler = new SelectionHandler();
  }

  public ReportDesignerContext getReportDesignerContext() {
    return reportDesignerContext;
  }

  public void setReportDesignerContext( final ReportDesignerContext reportDesignerContext ) {
    final ReportDesignerContext oldContext = this.reportDesignerContext;
    this.reportDesignerContext = reportDesignerContext;
    updateDesignerContext( oldContext, reportDesignerContext );
  }

  protected void updateDesignerContext( final ReportDesignerContext oldContext,
                                        final ReportDesignerContext newContext ) {
    final ReportDocumentContext oldRenderContext;
    if ( oldContext != null ) {
      oldContext.removePropertyChangeListener( ReportDesignerContext.ACTIVE_CONTEXT_PROPERTY, contextChangeHandler );
      oldRenderContext = oldContext.getActiveContext();
    } else {
      oldRenderContext = null;
    }

    final ReportDocumentContext newRenderContext;
    if ( newContext != null ) {
      newContext.addPropertyChangeListener( ReportDesignerContext.ACTIVE_CONTEXT_PROPERTY, contextChangeHandler );
      newRenderContext = newContext.getActiveContext();
    } else {
      newRenderContext = null;
    }

    updateActiveContext( oldRenderContext, newRenderContext );
  }

  protected void updateActiveContext( final ReportDocumentContext oldContext, final ReportDocumentContext newContext ) {
    if ( oldContext != null ) {
      oldContext.getSelectionModel().removeReportSelectionListener( selectionHandler );
    }
    if ( newContext != null ) {
      newContext.getSelectionModel().addReportSelectionListener( selectionHandler );
      updateSelection( newContext.getSelectionModel() );
    }
    setEnabled( newContext != null );
  }

  @SuppressWarnings( { "NoopMethodInAbstractClass" } )
  protected void updateSelection( final DocumentContextSelectionModel model ) {
  }
}
