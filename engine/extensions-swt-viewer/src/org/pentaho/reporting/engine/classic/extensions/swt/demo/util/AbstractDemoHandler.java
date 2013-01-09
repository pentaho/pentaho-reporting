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

package org.pentaho.reporting.engine.classic.extensions.swt.demo.util;

/**
 * ===========================================
 * JFreeReport : a free Java reporting library
 * ===========================================
 *
 * Project Info:  http://jfreereport.pentaho.org/
 *
 * (C) Copyright 2006, by Pentaho Corporation and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * ------------
 * AbstractDemoHandler.java
 * ------------
 * (C) Copyright 2006, by Pentaho Corporation.
 */

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;


/**
 * The AbstractDemoHandler provides some common implementations that are used
 * by all other demo handlers, and which are of minor interest for the demo's
 * purposes.
 *
 * @author Thomas Morgner
 */
public abstract class AbstractDemoHandler implements InternalDemoHandler
{
  private DemoController controller;

  protected AbstractDemoHandler()
  {
  }

  protected JComponent createDefaultTable(final TableModel data) {
    final JTable table = new JTable(data);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

    for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++) {
        final TableColumn column = table.getColumnModel().getColumn(columnIndex);
        column.setMinWidth(50);
        final Class c = data.getColumnClass(columnIndex);
        if (c.equals(Number.class)) {
//            column.setCellRenderer(new NumberCellRenderer());
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
