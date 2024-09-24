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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.demo.util;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;


/**
 * The AbstractDemoHandler provides some common implementations that are used by all other demo handlers, and which are
 * of minor interest for the demo's purposes.
 *
 * @author Thomas Morgner
 */
public abstract class AbstractDemoHandler implements InternalDemoHandler
{
  private DemoController controller;

  public AbstractDemoHandler()
  {
  }


  protected JComponent createDefaultTable(final TableModel data)
  {
    final JTable table = new JTable(data);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

    for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++)
    {
      final TableColumn column = table.getColumnModel().getColumn(columnIndex);
      column.setMinWidth(50);
      final Class c = data.getColumnClass(columnIndex);
      if (c.equals(Number.class))
      {
        column.setCellRenderer(new NumberCellRenderer());
      }
    }

    return new JScrollPane
        (table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
  }

  public DemoController getController()
  {
    return controller;
  }

  public void setController(final DemoController controler)
  {
    this.controller = controler;
  }

  public PreviewHandler getPreviewHandler()
  {
    return new DefaultPreviewHandler(this);
  }
}
