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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.plugin.jfreereport.reportcharts;

import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.data.xy.XYDataset;

import java.text.NumberFormat;

/**
 * A standard label generator that can be used with a {@link org.jfree.chart.renderer.category.CategoryItemRenderer}.
 */
public class LogXYItemLabelGenerator extends StandardXYItemLabelGenerator {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public LogXYItemLabelGenerator() {
    super();
  }

  /**
   * Creates a new generator with the specified number formatter.
   *
   * @param labelFormat the label format string (<code>null</code> not permitted).
   */
  public LogXYItemLabelGenerator( final String labelFormat ) {
    super( labelFormat, NumberFormat.getInstance(), NumberFormat.getInstance() );
  }

  /**
   * Creates the array of items that can be passed to the {@link java.text.MessageFormat} class for creating labels.
   *
   * @param dataset the dataset (<code>null</code> not permitted).
   * @param series  the series (zero-based index).
   * @param item    the item (zero-based index).
   * @return An array of three items from the dataset formatted as <code>String</code> objects (never
   * <code>null</code>).
   */
  protected Object[] createItemArray( final XYDataset dataset, final int series, final int item ) {
    final Object[] objects = super.createItemArray( dataset, series, item );
    final Number value = dataset.getY( series, item );
    objects[ 2 ] = LogCategoryItemLabelGenerator.formatValue( value );
    return objects;
  }
}
