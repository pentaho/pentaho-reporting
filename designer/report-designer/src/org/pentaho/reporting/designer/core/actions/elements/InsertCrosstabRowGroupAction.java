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

package org.pentaho.reporting.designer.core.actions.elements;

import java.awt.event.ActionEvent;
import javax.swing.Action;

import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.settings.SettingsListener;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.undo.UndoEntry;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroupBody;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * Inserts a crosstab row group, only appears when inside a crosstab row
 *
 * @author Will Gorman (wgorman@pentaho.com)
 */
public final class InsertCrosstabRowGroupAction extends AbstractElementSelectionAction implements SettingsListener
{
  private static final long serialVersionUID = 8941387470673515186L;

  public InsertCrosstabRowGroupAction()
  {
    putValue(Action.NAME, ActionMessages.getString("InsertCrosstabRowGroupAction.Text"));
    putValue(Action.SHORT_DESCRIPTION, ActionMessages.getString("InsertCrosstabRowGroupAction.Description"));
    putValue(Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic("InsertCrosstabRowGroupAction.Mnemonic"));
    putValue(Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke("InsertCrosstabRowGroupAction.Accelerator"));
    putValue(Action.SMALL_ICON, IconLoader.getInstance().getGenericSquare());
    setVisible(WorkspaceSettings.getInstance().isExperimentalFeaturesVisible());
    WorkspaceSettings.getInstance().addSettingsListener(this);
  }

  protected void selectedElementPropertiesChanged(final ReportModelEvent event)
  {
  }

  public void actionPerformed(final ActionEvent e)
  {
    final ReportRenderContext activeContext = getActiveContext();
    if (activeContext == null)
    {
      return;
    }

    final CrosstabRowGroup newGroup = new CrosstabRowGroup();
    try
    {
      Object selectedElement = null;
      if (getSelectionModel().getSelectionCount() > 0)
      {
        selectedElement = getSelectionModel().getSelectedElement(0);
      }
      if (selectedElement instanceof CrosstabRowGroup)
      {
        // execution order is important here.
        // first unlink the old root-group by setting a new one ...
        final CrosstabRowGroup selectedGroup = (CrosstabRowGroup) selectedElement;
        final GroupBody oldGroupBody = selectedGroup.getBody();
        final CrosstabRowGroupBody newGroupBody = new CrosstabRowGroupBody(newGroup);
        selectedGroup.setBody(newGroupBody);
        newGroup.setBody(oldGroupBody);
        
        activeContext.getUndo().addChange(ActionMessages.getString("InsertCrosstabRowGroupAction.UndoName"),
            new InsertGroupBodyOnGroupUndoEntry(selectedGroup.getObjectID(), oldGroupBody, newGroupBody));
      }
    }
    catch (Exception ex)
    {
      UncaughtExceptionsModel.getInstance().addException(ex);
    }
  }

  protected void updateSelection()
  {
    if (isVisible() == false)
    {
      setEnabled(false);
      return;
    }
	  
    if (getSelectionModel() != null && getSelectionModel().getSelectionCount() == 0)
    {
      // there's nothing selected, we can safely add a new group
      // at the report level (AbstractReportDefinition)
      setEnabled(false);
      return;
    }
    if (isSingleElementSelection() == false)
    {
      // there's more than 1 element selected, disable because
      // we can't know where to insert in this case
      setEnabled(false);
      return;
    }

    final Object selectedElement = getSelectionModel().getSelectedElement(0);
    if (selectedElement instanceof CrosstabRowGroup)
    {
      // if the selectedElement is the report-definition or a relational group
      // then we can safely insert to those
      setEnabled(true);
      return;
    }

    setEnabled(false);
  }

  private static class InsertGroupBodyOnGroupUndoEntry implements UndoEntry
  {
    private static final long serialVersionUID = 6615171451777587555L;
    
    private InstanceID target;
    private GroupBody newRootGroup;
    private GroupBody oldRootGroup;

    private InsertGroupBodyOnGroupUndoEntry(final InstanceID target, final GroupBody oldRootGroup, final GroupBody newRootGroup)
    {
      this.target = target;
      this.oldRootGroup = oldRootGroup;
      this.newRootGroup = newRootGroup;
    }

    public void undo(final ReportRenderContext renderContext)
    {
      final Group selectedGroup = (Group)
              ModelUtility.findElementById(renderContext.getReportDefinition(), target);
      selectedGroup.setBody(oldRootGroup);
    }

    public void redo(final ReportRenderContext renderContext)
    {
      final Group selectedGroup = (Group)
              ModelUtility.findElementById(renderContext.getReportDefinition(), target);
      selectedGroup.setBody(newRootGroup);
    }

    public UndoEntry merge(final UndoEntry newEntry)
    {
      return null;
    }
  }
  
  public void settingsChanged()
  {
    setVisible(WorkspaceSettings.getInstance().isExperimentalFeaturesVisible());
  }
}
