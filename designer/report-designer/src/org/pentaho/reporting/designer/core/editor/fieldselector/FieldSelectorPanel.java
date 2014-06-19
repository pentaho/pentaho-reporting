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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.designer.core.editor.fieldselector;

import java.awt.BorderLayout;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.structuretree.ReportFieldNode;
import org.pentaho.reporting.designer.core.model.ReportDataSchemaModel;
import org.pentaho.reporting.designer.core.settings.SettingsListener;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.SidePanel;
import org.pentaho.reporting.designer.core.util.dnd.FieldDescriptionTransferable;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportModelListener;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;

public class FieldSelectorPanel extends SidePanel
{
  private class ReportModelChangeHandler implements ReportModelListener, SettingsListener
  {
    private ReportModelChangeHandler()
    {
    }

    public void nodeChanged(final ReportModelEvent event)
    {
      final ReportDesignerContext designerContext = getReportDesignerContext();
      final ReportDocumentContext activeContext = designerContext.getActiveContext();
      if (activeContext == null)
      {
        return;
      }

      if (event.getElement() == activeContext.getReportDefinition())
      {
        final ReportDataSchemaModel model = activeContext.getReportDataSchemaModel();
        dataModel.setDataSchema(computeColumns(model));
      }
    }

    public void settingsChanged()
    {
      final ReportDesignerContext designerContext = getReportDesignerContext();
      final ReportDocumentContext activeContext = designerContext.getActiveContext();
      if (activeContext == null)
      {
        return;
      }
      final ReportDataSchemaModel model = activeContext.getReportDataSchemaModel();
      dataModel.setDataSchema(computeColumns(model));
    }
  }

  private FieldSelectorTableModel dataModel;
  private JTable table;
  private ReportModelChangeHandler changeHandler;
  private AbstractReportDefinition report;

  public FieldSelectorPanel()
  {
    setLayout(new BorderLayout());

    dataModel = new FieldSelectorTableModel();

    table = new JTable();
    table.setModel(dataModel);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setTransferHandler(new ColumnTransferHandler());
    table.setDefaultRenderer(ReportFieldNode.class, new FieldCellRenderer());
    table.setDragEnabled(true);

    changeHandler = new ReportModelChangeHandler();
    WorkspaceSettings.getInstance().addSettingsListener(changeHandler);

    add(new JScrollPane(table));
  }

  protected void updateActiveContext(final ReportDocumentContext oldContext, final ReportDocumentContext newContext)
  {
    super.updateActiveContext(oldContext, newContext);
    if (report != null)
    {
      report.removeReportModelListener(changeHandler);
    }
    if (newContext == null)
    {
      report = null;
      dataModel.setDataSchema(FieldSelectorTableModel.EMPTY_NODES);
    }
    else
    {
      report = newContext.getReportDefinition();
      report.addReportModelListener(changeHandler);

      final ReportDataSchemaModel model = newContext.getReportDataSchemaModel();
      dataModel.setDataSchema(computeColumns(model));
    }
  }

  protected ReportFieldNode[] computeColumns(final ReportDataSchemaModel model)
  {
    final String[] columnNames = model.getColumnNames();
    final ArrayList<ReportFieldNode> nodes = new ArrayList<ReportFieldNode>(columnNames.length);
    for (int i = 0; i < columnNames.length; i++)
    {
      final String name = columnNames[i];
      final DataAttributes attributes = model.getDataSchema().getAttributes(name);
      if (attributes != null)
      {
        if (ReportDataSchemaModel.isFiltered(attributes, model.getDataAttributeContext()))
        {
          continue;
        }
        final Class type = (Class) attributes.getMetaAttribute
            (MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.TYPE, Class.class, model.getDataAttributeContext());
        nodes.add(new ReportFieldNode(model, name, type));
      }
      else
      {
        nodes.add(new ReportFieldNode(model, name, Object.class));
      }
    }
    return nodes.toArray(new ReportFieldNode[nodes.size()]);
  }

  private class ColumnTransferHandler extends TransferHandler
  {
    /**
     * Creates a <code>Transferable</code> to use as the source for a data transfer. Returns the representation of the
     * data to be transferred, or <code>null</code> if the component's property is <code>null</code>
     *
     * @param c the component holding the data to be transferred; this argument is provided to enable sharing of
     *          <code>TransferHandler</code>s by multiple components
     * @return the representation of the data to be transferred, or <code>null</code> if the property associated with
     *         <code>c</code> is <code>null</code>
     */
    protected Transferable createTransferable(final JComponent c)
    {
      if (c != table)
      {
        return null;
      }

      final int selectedRow = table.getSelectedRow();
      if (selectedRow == -1)
      {
        return null;
      }

      return new FieldDescriptionTransferable(dataModel.getFieldName(selectedRow));
    }

    public int getSourceActions(final JComponent c)
    {
      return COPY;
    }
  }
}
