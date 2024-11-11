/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.demo.ancient.demo.functions;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 * Creation-Date: 01.10.2005, 11:36:13
 *
 * @author Thomas Morgner
 */
public class WayBillTableModel extends AbstractTableModel
{
  public static class CategoryItem
  {
    private String container;
    private String item;
    private String notes;
    private Double weight;

    public CategoryItem(final String category,
                        final String item,
                        final String notes,
                        final double weight)
    {
      this.container = category;
      this.item = item;
      this.notes = notes;
      this.weight = new Double(weight);
    }

    public Double getWeight()
    {
      return weight;
    }

    public String getContainer()
    {
      return container;
    }

    public String getItem()
    {
      return item;
    }

    public String getNotes()
    {
      return notes;
    }
  }

  private String[] COLNAMES = {"Container", "Item", "Notes", "Weight"};

  private ArrayList rows;

  public WayBillTableModel()
  {
    rows = new ArrayList();
  }

  public void addItem(CategoryItem item)
  {
    rows.add(item);
  }

  public int getRowCount()
  {
    return rows.size();
  }

  public int getColumnCount()
  {
    return COLNAMES.length;
  }

  public Class getColumnClass(int columnIndex)
  {
    if (columnIndex == 3)
    {
      return Double.class;
    }
    return String.class;
  }

  public String getColumnName(int column)
  {
    return COLNAMES[column];
  }

  public Object getValueAt(int rowIndex, int columnIndex)
  {
    CategoryItem item = (CategoryItem) rows.get(rowIndex);
    switch (columnIndex)
    {
      case 0:
        return item.getContainer();
      case 1:
        return item.getItem();
      case 2:
        return item.getNotes();
      case 3:
        return item.getWeight();
    }
    return null;
  }
}
