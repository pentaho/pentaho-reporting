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
 * Copyright (c) 2005-2011 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.designer.core.editor.crosstab;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.libraries.base.util.CSVQuoter;
import org.pentaho.reporting.libraries.base.util.CSVTokenizer;

public class CrosstabDetailTableTransferHandler extends TransferHandler
{
  private JTable targetList;
  private CrosstabDetailTableModel listModel;

  public CrosstabDetailTableTransferHandler(final JTable targetList,
                                            final CrosstabDetailTableModel listModel)
  {
    this.targetList = targetList;
    this.listModel = listModel;
  }

  public boolean importData(final TransferSupport support)
  {
    if (support.isDataFlavorSupported(DataFlavor.stringFlavor) == false)
    {
      return false;
    }

    if (support.isDrop() == false)
    {
      return false;
    }
    try
    {
      final String transferData = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
      if (transferData == null)
      {
        return false;
      }

      final CSVTokenizer tokenizer = new CSVTokenizer(transferData, ",", "\"");
      final ArrayList<String> items = new ArrayList<String>();
      while (tokenizer.hasMoreElements())
      {
        items.add(tokenizer.nextToken());
      }

      final DropLocation dropLocation = support.getDropLocation();
      final Point point = dropLocation.getDropPoint();
      final int idx = targetList.rowAtPoint(point);
      if (idx == -1)
      {
        for (int i = 0; i < items.size(); i++)
        {
          final String item = items.get(i);
          listModel.add(new CrosstabDetail(item));
        }
      }
      else
      {
        for (int i = items.size() - 1; i >= 0; i -= 1)
        {
          final String item = items.get(i);
          listModel.add(idx, new CrosstabDetail(item));
        }
      }
    }
    catch (Exception e)
    {
      UncaughtExceptionsModel.getInstance().addException(e);
    }

    return super.importData(support);
  }

  public boolean canImport(final TransferSupport support)
  {
    if (support.isDrop() == false)
    {
      return false;
    }
    return (support.isDataFlavorSupported(DataFlavor.stringFlavor));
  }

  public int getSourceActions(final JComponent c)
  {
    return TransferHandler.COPY;
  }

  protected Transferable createTransferable(final JComponent c)
  {
    if (c != targetList)
    {
      throw new IllegalStateException();
    }

    final StringBuilder b = new StringBuilder();
    final CSVQuoter quoter = new CSVQuoter(',', '"');

    final int[] selectedRows = targetList.getSelectedRows();
    for (int i = 0; i < selectedRows.length; i++)
    {
      if (i != 0)
      {
        b.append(',');
      }
      final int row = selectedRows[i];
      final CrosstabDetail crosstabDimension = listModel.get(row);
      if (crosstabDimension == null)
      {
        continue;
      }
      final String field = crosstabDimension.getField();
      if (field == null)
      {
        continue;
      }
      b.append(quoter.doQuoting(String.valueOf(field)));
    }

    return new StringSelection(b.toString());
  }
}
