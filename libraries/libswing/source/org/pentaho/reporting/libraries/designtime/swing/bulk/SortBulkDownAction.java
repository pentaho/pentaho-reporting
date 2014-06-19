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

package org.pentaho.reporting.libraries.designtime.swing.bulk;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;

import org.pentaho.reporting.libraries.base.util.BulkDataUtility;
import org.pentaho.reporting.libraries.designtime.swing.Messages;

public class SortBulkDownAction extends AbstractAction implements ListSelectionListener, ListDataListener
{
  private BulkDataProvider tableModel;
  private ListSelectionModel listSelectionModel;
  private JTable editorTable;
  /**
   * Defines an <code>Action</code> object with a default description string and default icon.
   */
  public SortBulkDownAction(final BulkDataProvider tableModel,
                            final ListSelectionModel listSelectionModel)
  {
    this.tableModel = tableModel;
    this.listSelectionModel = listSelectionModel;
    this.listSelectionModel.addListSelectionListener(this);

    putValue(Action.SMALL_ICON, Messages.getInstance().getIcon("Icons.MOVE_DOWN"));
    putValue(Action.SHORT_DESCRIPTION, Messages.getInstance().getString("Action.MOVE_DOWN"));
  }

  public SortBulkDownAction (final BulkDataProvider tableModel,
                            final ListSelectionModel listSelectionModel,
                            final JTable table)
  {

    this(tableModel, listSelectionModel);
    this.editorTable = table;
  }


  public void valueChanged(final ListSelectionEvent e)
  {
    updateEnabled();
  }

  private void updateEnabled()
  {
    if (listSelectionModel.isSelectionEmpty())
    {
      setEnabled(false);
    }
    else if (listSelectionModel.getMaxSelectionIndex() == (tableModel.getBulkDataSize() - 1))
    {
      // already the last entry ...
      setEnabled(false);
    }
    else
    {
      setEnabled(true);
    }
  }

  public void intervalAdded(final ListDataEvent e)
  {
    updateEnabled();
  }

  public void intervalRemoved(final ListDataEvent e)
  {
    updateEnabled();
  }

  public void contentsChanged(final ListDataEvent e)
  {
    updateEnabled();
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed(final ActionEvent e)
  {
    if (listSelectionModel.isSelectionEmpty())
    {
      return;
    }
    final Object[] data = tableModel.getBulkData();
    if (listSelectionModel.getMaxSelectionIndex() == (data.length - 1))
    {
      // already the first entry ...
      return;
    }

    if (editorTable != null)
    {
      final TableCellEditor cellEditor = editorTable.getCellEditor();
      if(cellEditor != null)
      {
        cellEditor.stopCellEditing();
      }      
    }

    final Object[] result = (Object[]) data.clone();
    final boolean[] selections = new boolean[result.length];
    for (int i = listSelectionModel.getMinSelectionIndex(); i <= listSelectionModel.getMaxSelectionIndex(); i++)
    {
      selections[i] = listSelectionModel.isSelectedIndex(i);
    }

    BulkDataUtility.pushDown(result, selections);

    tableModel.setBulkData(result);

    listSelectionModel.setValueIsAdjusting(true);
    listSelectionModel.removeSelectionInterval(0, selections.length);
    for (int i = 0; i < selections.length; i++)
    {
      final boolean selection = selections[i];
      if (selection)
      {
        listSelectionModel.addSelectionInterval(i, i);
      }
    }
    listSelectionModel.setValueIsAdjusting(false);
  }
}
