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

import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.data.category.CategoryDataset;

import java.text.NumberFormat;

/**
 * A standard label generator that can be used with a {@link org.jfree.chart.renderer.category.CategoryItemRenderer}.
 */
public class LogCategoryItemLabelGenerator extends StandardCategoryItemLabelGenerator {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public LogCategoryItemLabelGenerator() {
    super( DEFAULT_LABEL_FORMAT_STRING, NumberFormat.getInstance() );
  }

  /**
   * Creates a new generator with the specified number formatter.
   *
   * @param labelFormat the label format string (<code>null</code> not permitted).
   * @param formatter   the number formatter (<code>null</code> not permitted).
   */
  public LogCategoryItemLabelGenerator( final String labelFormat ) {
    super( labelFormat, NumberFormat.getInstance() );
  }

  /**
   * Creates the array of items that can be passed to the {@link java.text.MessageFormat} class for creating labels.
   *
   * @param dataset the dataset (<code>null</code> not permitted).
   * @param row     the row index (zero-based).
   * @param column  the column index (zero-based).
   * @return The items (never <code>null</code>).
   */
  protected Object[] createItemArray( final CategoryDataset dataset, final int row, final int column ) {
    final Object[] objects = super.createItemArray( dataset, row, column );
    final Number value = dataset.getValue( row, column );
    objects[ 2 ] = formatValue( value );
    return objects;
  }

  public static String formatValue( final Number number ) {
    if ( number == null ) {
      return null;
    }

    final double rawValue = number.doubleValue();
    final double value = Math.abs( rawValue );

    if ( value < 1000.0 ) {
      return NumberFormat.getNumberInstance().format( rawValue );
    } else if ( value < 1000000.0 ) {
      return NumberFormat.getNumberInstance().format( rawValue / 1000.0 ) + "K";
    } else if ( value < 1000000000.0 ) {
      return NumberFormat.getNumberInstance().format( rawValue / 1000000.0 ) + "M";
    } else if ( value < 1000000000000.0 ) {
      return NumberFormat.getNumberInstance().format( rawValue / 1000000000.0 ) + "B";
    } else {
      return NumberFormat.getNumberInstance().format( rawValue / 1000000000000.0 ) + "T";
    }
  }
}
