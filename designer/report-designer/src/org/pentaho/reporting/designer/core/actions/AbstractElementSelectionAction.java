/*
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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.designer.core.actions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.model.selection.ReportSelectionEvent;
import org.pentaho.reporting.designer.core.model.selection.ReportSelectionListener;
import org.pentaho.reporting.designer.core.model.selection.ReportSelectionModel;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportModelListener;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public abstract class AbstractElementSelectionAction extends AbstractDesignerContextAction
{
  private class ActiveContextChangeHandler implements PropertyChangeListener
  {
    private ActiveContextChangeHandler()
    {
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */

    public void propertyChange(final PropertyChangeEvent evt)
    {
      final ReportRenderContext oldContext = (ReportRenderContext) evt.getOldValue();
      final ReportRenderContext activeContext = (ReportRenderContext) evt.getNewValue();
      updateActiveContext(oldContext, activeContext);
    }
  }

  private class SelectionUpdateHandler implements ReportSelectionListener
  {
    private SelectionUpdateHandler()
    {
    }

    public void selectionAdded(final ReportSelectionEvent event)
    {
      updateSelection();
    }

    public void selectionRemoved(final ReportSelectionEvent event)
    {
      updateSelection();
    }

    public void leadSelectionChanged(final ReportSelectionEvent event)
    {
      updateSelection();
    }
  }
  
  protected class UpdatePropertiesForSelectionHandler implements ReportModelListener
  {
    private UpdatePropertiesForSelectionHandler()
    {
    }

    public void nodeChanged(final ReportModelEvent event)
    {
      final ReportRenderContext activeContext = getActiveContext();
      if (activeContext == null)
      {
        throw new IllegalStateException("Stale Action reference!");
      }
      if (activeContext.getSelectionModel().isSelected(event.getElement()))
      {
        updateSelection();
      }
    }
  }


  private ReportSelectionModel selectionModel;
  private SelectionUpdateHandler updateHandler;
  private AbstractElementSelectionAction.ActiveContextChangeHandler changeHandler;
  private UpdatePropertiesForSelectionHandler updateSelectionHandler;
  
  protected AbstractElementSelectionAction()
  {
    updateHandler = new SelectionUpdateHandler();
    changeHandler = new ActiveContextChangeHandler();
    updateSelectionHandler = new UpdatePropertiesForSelectionHandler();
  }

  protected void updateDesignerContext(final ReportDesignerContext oldContext, final ReportDesignerContext newContext)
  {
    if (oldContext != null)
    {
      oldContext.removePropertyChangeListener(ReportDesignerContext.ACTIVE_CONTEXT_PROPERTY, changeHandler);
      updateActiveContext(oldContext.getActiveContext(), null);
    }
    super.updateDesignerContext(oldContext, newContext);
    if (newContext != null)
    {
      newContext.addPropertyChangeListener(ReportDesignerContext.ACTIVE_CONTEXT_PROPERTY, changeHandler);
      updateActiveContext(null, newContext.getActiveContext());
    }

  }

  public ReportSelectionModel getSelectionModel()
  {
    return selectionModel;
  }

  protected ReportRenderContext getActiveContext()
  {

    final ReportDesignerContext context = getReportDesignerContext();
    if (context == null)
    {
      return null;
    }
    return context.getActiveContext();
  }

  protected void updateActiveContext(final ReportRenderContext oldContext,
                                     final ReportRenderContext newContext)
  {
    if (this.selectionModel != null)
    {
      this.selectionModel.removeReportSelectionListener(updateHandler);
    }
    if (oldContext != null)
    {
      oldContext.getReportDefinition().removeReportModelListener(updateSelectionHandler);
    }
    if (newContext != null)
    {
      this.selectionModel = newContext.getSelectionModel();
      this.selectionModel.addReportSelectionListener(updateHandler);
      updateSelection();
    }
    else
    {
      this.selectionModel = null;
      updateSelection();
    }
    if (newContext != null)
    {
      newContext.getReportDefinition().addReportModelListener(updateSelectionHandler);
    }
  }

  protected void updateSelection()
  {
    if (selectionModel == null)
    {
      setEnabled(false);
      return;
    }

    setEnabled(selectionModel.getSelectionCount() > 0);
  }

  protected boolean isSingleElementSelection()
  {
    if (selectionModel == null)
    {
      return false;
    }
    if (selectionModel.getSelectionCount() != 1)
    {
      return false;
    }
    return true;
  }
}
