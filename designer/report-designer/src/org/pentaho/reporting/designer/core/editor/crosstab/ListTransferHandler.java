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

package org.pentaho.reporting.designer.core.editor.crosstab;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.libraries.base.util.CSVQuoter;
import org.pentaho.reporting.libraries.base.util.CSVTokenizer;
import org.pentaho.reporting.libraries.designtime.swing.bulk.DefaultBulkListModel;

public class ListTransferHandler extends TransferHandler
{
  private JList targetList;
  private DefaultBulkListModel listModel;

  public ListTransferHandler(final JList targetList,
                      final DefaultBulkListModel listModel)
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
      final int idx = targetList.locationToIndex(point);
      if (idx == -1)
      {
        for (int i = 0; i < items.size(); i++)
        {
          final String item = items.get(i);
          listModel.addElement(item);
        }
      }
      else
      {
        for (int i = items.size() - 1; i >= 0; i -= 1)
        {
          final String item = items.get(i);
          listModel.add(idx, item);
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
    final JList lcomp = (JList) c;
    final StringBuilder b = new StringBuilder();
    final CSVQuoter quoter = new CSVQuoter(',', '"');
    final Object[] selectedValues = lcomp.getSelectedValues();
    for (int i = 0; i < selectedValues.length; i++)
    {
      if (i != 0)
      {
        b.append(',');
      }
      final Object value = selectedValues[i];
      b.append(quoter.doQuoting(String.valueOf(value)));
    }

    return new StringSelection(b.toString());
  }
}
