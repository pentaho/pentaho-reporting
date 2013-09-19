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
 * Copyright (c) 2008 - 2009 Pentaho Corporation, .  All rights reserved.
 */

package org.pentaho.reporting.designer.core.editor.expressions;

import java.awt.BorderLayout;
import java.util.ArrayList;
import javax.swing.JScrollPane;
import javax.swing.table.TableCellEditor;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.model.selection.ReportSelectionModel;
import org.pentaho.reporting.designer.core.util.SidePanel;
import org.pentaho.reporting.designer.core.util.table.ElementMetaDataTable;
import org.pentaho.reporting.designer.core.util.table.GroupedMetaTableModel;
import org.pentaho.reporting.designer.core.util.table.GroupedNameCellRenderer;
import org.pentaho.reporting.designer.core.util.table.SortHeaderPanel;
import org.pentaho.reporting.engine.classic.core.function.Expression;

public class ExpressionPropertiesEditorPanel extends SidePanel
{
  private ExpressionPropertiesTableModel dataModel;
  private ElementMetaDataTable table;

  private static final Expression[] EMPTY_EXPRESSIONS = new Expression[0];
  private SortHeaderPanel headerPanel;

  public ExpressionPropertiesEditorPanel()
  {
    setLayout(new BorderLayout());

    dataModel = new ExpressionPropertiesTableModel();

    table = new ElementMetaDataTable();
    table.setModel(new GroupedMetaTableModel(dataModel));
    table.getColumnModel().getColumn(0).setCellRenderer(new GroupedNameCellRenderer());

    headerPanel = new SortHeaderPanel(dataModel);

    add(headerPanel, BorderLayout.NORTH);
    add(new JScrollPane(table), BorderLayout.CENTER);
  }

  public Expression[] getData()
  {
    return dataModel.getData();
  }

  public void setData(final Expression[] elements)
  {
    stopEditing();

    dataModel.setData(elements);
  }

  public void stopEditing()
  {
    final TableCellEditor tableCellEditor = table.getCellEditor();
    if (tableCellEditor != null)
    {
      tableCellEditor.stopCellEditing();
    }
  }

  protected void updateDesignerContext(final ReportDesignerContext oldContext, final ReportDesignerContext newContext)
  {
    super.updateDesignerContext(oldContext, newContext);
    table.setReportDesignerContext(newContext);
  }

  protected void updateSelection(final ReportSelectionModel model)
  {
    if (model == null)
    {
      setData(EMPTY_EXPRESSIONS);
    }
    else
    {
      final Object[] selectedElements = model.getSelectedElements();
      final ArrayList<Expression> filter = new ArrayList<Expression>();
      for (int i = 0; i < selectedElements.length; i++)
      {
        final Object element = selectedElements[i];
        if (element instanceof Expression)
        {
          filter.add((Expression) element);
        }
      }
      setData(filter.toArray(new Expression[filter.size()]));
    }
  }

  protected void updateActiveContext(final ReportRenderContext oldContext, final ReportRenderContext newContext)
  {
    stopEditing();

    super.updateActiveContext(oldContext, newContext);
    dataModel.setActiveContext(newContext);
    if (newContext == null)
    {
      setData(EMPTY_EXPRESSIONS);
    }
  }

  public void setEnabled(final boolean enabled)
  {
    super.setEnabled(enabled);
    table.setEnabled(enabled);
    headerPanel.setEnabled(enabled);
  }
}
