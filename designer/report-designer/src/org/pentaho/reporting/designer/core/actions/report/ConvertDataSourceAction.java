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
 * Copyright (c) 2000 - 2011 Pentaho Corporation and Contributors...  
 * All rights reserved.
 */

package org.pentaho.reporting.designer.core.actions.report;

import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.table.TableModel;

import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.editor.structuretree.ReportQueryNode;
import org.pentaho.reporting.designer.core.model.selection.ReportSelectionModel;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.DataFactoryChange;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DesignTimeDataFactoryContext;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.libraries.designtime.swing.background.BackgroundCancellableProcessHelper;

public class ConvertDataSourceAction extends AbstractElementSelectionAction
{
  private class ConvertDataSourceTask implements Runnable
  {
    private ConvertDataSourceTask()
    {
    }

    public void run()
    {
      final Object[] selectedElements = getSelectionModel().getSelectedElements();
      for (int i = 0; i < selectedElements.length; i++)
      {
        final Object element = selectedElements[i];
        if (element instanceof ReportQueryNode)
        {
          try
          {
            final ReportQueryNode queryNode = (ReportQueryNode) element;
            final DataFactory dataFactory = queryNode.getDataFactory().derive();
            final MasterReport report = getActiveContext().getMasterReportElement();
            dataFactory.initialize(new DesignTimeDataFactoryContext(report));
            if (dataFactory.isQueryExecutable(queryNode.getQueryName(), new StaticDataRow()) == false)
            {
              return;
            }

            final TableModel tableModel = dataFactory.queryData(queryNode.getQueryName(), new StaticDataRow());

            final TableDataFactory tableDataFactory = new TableDataFactory();
            tableDataFactory.addTable(queryNode.getQueryName(), createModel(tableModel));
            AddDataFactoryAction.addDataFactory(getActiveContext(), tableDataFactory, new DataFactoryChange[0]);
          }
          catch (ReportDataFactoryException e1)
          {
            UncaughtExceptionsModel.getInstance().addException(e1);
          }
          catch (ReportProcessingException e)
          {
            UncaughtExceptionsModel.getInstance().addException(e);
          }
          break;
        }
      }
    }


    public TableModel createModel(final TableModel model)
    {
      final TypedTableModel tableModel = new TypedTableModel();
      final int columnCount = model.getColumnCount();
      for (int col = 0; col < columnCount; col++)
      {
        tableModel.addColumn(model.getColumnName(col), model.getColumnClass(col));
      }

      final int rowCount = model.getRowCount();
      for (int r = 0; r < rowCount; r++)
      {
        for (int col = 0; col < columnCount; col++)
        {
          tableModel.setValueAt(model.getValueAt(r, col), r, col);
        }
      }

      return tableModel;
    }
  }

  public ConvertDataSourceAction()
  {
    putValue(Action.NAME, ActionMessages.getString("ConvertDataSourceAction.Text"));
    putValue(Action.DEFAULT, ActionMessages.getString("ConvertDataSourceAction.Description"));
    putValue(Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic("ConvertDataSourceAction.Mnemonic"));
    putValue(Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke("ConvertDataSourceAction.Accelerator"));
  }

  protected void updateSelection()
  {
    final ReportSelectionModel model = getSelectionModel();
    if (model == null)
    {
      setEnabled(false);
      return;
    }

    final Object[] selectedObjects = model.getSelectedElements();
    for (int i = 0; i < selectedObjects.length; i++)
    {
      final Object selectedObject = selectedObjects[i];
      if (selectedObject instanceof ReportQueryNode == false)
      {
        continue;
      }
      final ReportQueryNode queryNode = (ReportQueryNode) selectedObject;
      final DataFactory dataFactory = queryNode.getDataFactory();

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

    final Thread thread = new Thread(new ConvertDataSourceTask());
    thread.setName("ConvertDataSource-Worker");
    thread.setDaemon(true);
    BackgroundCancellableProcessHelper.executeProcessWithCancelDialog
        (thread, null, getReportDesignerContext().getParent());
  }
}
