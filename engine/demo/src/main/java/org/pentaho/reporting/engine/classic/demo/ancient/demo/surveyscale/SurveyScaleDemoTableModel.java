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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.demo.ancient.demo.surveyscale;

import javax.swing.table.AbstractTableModel;

/**
 * A table model used by the SurveyScaleAPIDemoHandler application.
 */
public class SurveyScaleDemoTableModel extends AbstractTableModel
{

  private String[] categories;

  private String[] categoryDescriptions;

  private String[] items;

  private Number[] responses;

  private Number[] averages;

  /**
   * Creates a new table model.
   */
  public SurveyScaleDemoTableModel()
  {

    this.categories = new String[3];
    this.categories[0] = "EVALUATION";
    this.categories[1] = "USAGE";
    this.categories[2] = "CONTRIBUTION";

    this.categoryDescriptions = new String[3];
    this.categoryDescriptions[0] = "When evaluating free / open source software libraries for the Java(tm) platform, how important are the following items to you:";
    this.categoryDescriptions[1] = "In day to day usage of a free / open source software library, how important are the following items to you:";
    this.categoryDescriptions[2] = "How important are the following items in influencing your decision to contribute code to a free / open source software project:";

    this.items = new String[15];
    this.items[0] = "An informative and well designed web site.";
    this.items[1] = "An active user community (indicated by high traffic in the user mailing list or forum).";
    this.items[2] = "An easy-to-run demo application.";
    this.items[3] = "Screen shots on the project web-page.";
    this.items[4] = "The license under which the source code is distributed (GNU GPL, GNU LGPL, Apache-style, BSD-style etc.)";
    this.items[5] = "Comprehensive Javadoc HTML pages.";
    this.items[6] = "Developer documentation providing an overview of the library framework.";
    this.items[7] = "Demo code that illustrates how to use the library.";
    this.items[8] = "A search facility for the mailing list archives or online support forum.";
    this.items[9] = "A list of frequently-asked-questions.";
    this.items[10] = "Willingness of the project's main developers to engage in discussion about proposed modifications.";
    this.items[11] = "Turnaround time for getting patches accepted.";
    this.items[12] = "The project's coding standards.";
    this.items[13] = "Desire to avoid maintaining a separate branch of modifications to the main project.";
    this.items[14] = "Internal policies at your company.";

    this.responses = new Number[15];
    this.responses[0] = new Integer(4);
    this.responses[1] = new Integer(5);
    this.responses[2] = new Integer(4);
    this.responses[3] = new Integer(3);
    this.responses[4] = new Integer(3);
    this.responses[5] = new Integer(4);
    this.responses[6] = new Integer(4);
    this.responses[7] = new Integer(3);
    this.responses[8] = new Integer(2);
    this.responses[9] = new Integer(4);
    this.responses[10] = new Integer(4);
    this.responses[11] = new Integer(4);
    this.responses[12] = new Integer(1);
    this.responses[13] = new Integer(3);
    this.responses[14] = new Integer(3);

    this.averages = new Number[15];
    this.averages[0] = new Double(3.85);
    this.averages[1] = new Double(4.25);
    this.averages[2] = new Double(4.00);
    this.averages[3] = new Double(4.40);
    this.averages[4] = new Double(3.55);
    this.averages[5] = new Double(3.70);
    this.averages[6] = new Double(4.60);
    this.averages[7] = new Double(3.50);
    this.averages[8] = new Double(4.50);
    this.averages[9] = new Double(4.15);
    this.averages[10] = new Double(4.25);
    this.averages[11] = new Double(3.85);
    this.averages[12] = new Double(3.95);
    this.averages[13] = new Double(3.85);
    this.averages[14] = new Double(4.70);

  }

  /**
   * Returns the number of columns.
   *
   * @return 5.
   */
  public int getColumnCount()
  {
    return 5;
  }

  /**
   * Returns the name of a column.
   *
   * @param index the column index.
   * @return The column name.
   */
  public String getColumnName(final int index)
  {
    String result = null;
    if (index == 0)
    {
      result = "Category";
    }
    else if (index == 1)
    {
      result = "Category Description";
    }
    else if (index == 2)
    {
      result = "Item";
    }
    else if (index == 3)
    {
      result = "Your Response";
    }
    else if (index == 4)
    {
      result = "Average Response";
    }
    return result;
  }

  /**
   * Returns the row count.
   *
   * @return 15.
   */
  public int getRowCount()
  {
    return 15;
  }

  /**
   * Returns an item for the table.
   *
   * @param row    the row index (zero-based).
   * @param column the column index (zero-based).
   * @return The item.
   */
  public Object getValueAt(final int row, final int column)
  {
    if (column == 0)
    {
      return this.categories[row / 5];
    }
    else if (column == 1)
    {
      return this.categoryDescriptions[row / 5];
    }
    else if (column == 2)
    {
      return this.items[row];
    }
    else if (column == 3)
    {
      return this.responses[row];
    }
    else if (column == 4)
    {
      return this.averages[row];
    }
    else
    {
      return null;
    }
  }

}
