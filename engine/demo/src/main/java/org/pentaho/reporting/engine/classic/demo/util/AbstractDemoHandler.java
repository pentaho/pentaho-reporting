/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
