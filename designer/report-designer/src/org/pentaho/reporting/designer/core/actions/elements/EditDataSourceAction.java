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
import org.pentaho.reporting.designer.core.model.selection.ReportSelectionModel;
import org.pentaho.reporting.designer.core.util.ReportDesignerDesignTimeContext;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.undo.DataSourceEditUndoEntry;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;
import org.pentaho.reporting.engine.classic.core.designtime.DefaultDataFactoryChangeRecorder;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;

public class EditDataSourceAction extends AbstractElementSelectionAction
{
  public EditDataSourceAction()
  {
    putValue(Action.NAME, ActionMessages.getString("EditDataSourceAction.Text"));
    putValue(Action.SHORT_DESCRIPTION, ActionMessages.getString("EditDataSourceAction.Description"));
    putValue(Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic("EditDataSourceAction.Mnemonic"));
    putValue(Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke("EditDataSourceAction.Accelerator"));
  }

  protected void updateSelection()
  {
    final ReportSelectionModel selectionModel1 = getSelectionModel();
    if (selectionModel1 == null)
    {
      setEnabled(false);
      return;
    }

    final Object[] selectedObjects = selectionModel1.getSelectedElements();
    for (int i = 0; i < selectedObjects.length; i++)
    {
      final Object selectedObject = selectedObjects[i];
      if (selectedObject instanceof DataFactory == false)
      {
        continue;
      }
      final DataFactory dataFactory = (DataFactory) selectedObject;
      if (DataFactoryRegistry.getInstance().isRegistered(dataFactory.getClass().getName()) == false)
      {
        setEnabled(false);
        return;
      }

      final DataFactoryMetaData metadata =
          dataFactory.getMetaData();
      if (metadata.isEditable())
      {
        setEnabled(true);
        return;
      }

    }

    setEnabled(false);
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed(final ActionEvent e)
  {
    final ReportRenderContext activeContext = getActiveContext();
    if (activeContext == null)
    {
      return;
    }

    final Object[] selectedElements = getSelectionModel().getSelectedElements();
    for (int i = 0; i < selectedElements.length; i++)
    {
      final Object element = selectedElements[i];
      if (element instanceof DataFactory)
      {
        try
        {
          performEdit((DataFactory) element);
        }
        catch (ReportDataFactoryException e1)
        {
          UncaughtExceptionsModel.getInstance().addException(e1);
        }
        break;
      }
    }
  }

  protected void performEdit(final DataFactory dataFactory) throws ReportDataFactoryException
  {
    if (DataFactoryRegistry.getInstance().isRegistered(dataFactory.getClass().getName()) == false)
    {
      return;
    }

    final DataFactoryMetaData metadata =
        dataFactory.getMetaData();
    if (metadata.isEditable() == false)
    {
      return;
    }

    final DataSourcePlugin dataSourcePlugin = metadata.createEditor();
    final DataFactory storedFactory = dataFactory.derive();
    if (dataSourcePlugin.canHandle(dataFactory) == false)
    {
      return;
    }

    final DefaultDataFactoryChangeRecorder recorder = new DefaultDataFactoryChangeRecorder();
    final DataFactory editedDataFactory = dataSourcePlugin.performEdit
        (new ReportDesignerDesignTimeContext(getReportDesignerContext()), dataFactory, null, recorder);
    if (editedDataFactory == null)
    {
      return;
    }

    final ReportRenderContext activeContext = getActiveContext();
    final AbstractReportDefinition report = activeContext.getReportDefinition();
    final CompoundDataFactory collection = (CompoundDataFactory) report.getDataFactory();
    final int j = collection.indexOfByReference(dataFactory);
    if (j == -1)
    {
      throw new IllegalStateException("Edited data-source does not exist in the report anymore.");
    }

    DefaultDataFactoryChangeRecorder.applyChanges(collection, recorder.getChanges());

    final DataFactory editedClone = editedDataFactory.derive();
    collection.set(j, editedDataFactory);
    activeContext.getUndo().addChange
        (ActionMessages.getString("EditDataSourceAction.UndoName"), new DataSourceEditUndoEntry(j, storedFactory, editedClone));

    report.notifyNodeChildRemoved(dataFactory);
    report.notifyNodeChildAdded(editedDataFactory);
  }
}
