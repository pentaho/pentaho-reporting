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
