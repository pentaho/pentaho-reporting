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

package org.pentaho.plugin.jfreereport.reportcharts.collectors;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.general.ValueDataset;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.math.BigDecimal;

public class CollectorFunctionUtil {
  public static TimeSeries queryExistingValueFromDataSet( final TimeSeriesCollection dataset,
                                                          final String seriesName ) {
    try {
      return dataset.getSeries( seriesName );
    } catch ( Exception ignored ) {
      // dataset.getValue throws exceptions if the keys dont match ..
    }
    return null;
  }

  public static Number queryExistingValueFromDataSet( final PieDataset dataset,
                                                      final Comparable seriesName ) {
    try {
      return dataset.getValue( seriesName );
    } catch ( Exception ignored ) {
      // dataset.getValue throws exceptions if the keys dont match ..
    }
    return null;
  }

  public static Number queryExistingValueFromDataSet( final ValueDataset dataset ) {
    try {
      return dataset.getValue();
    } catch ( Exception ignored ) {
      // dataset.getValue throws exceptions if the keys dont match ..
    }
    return null;
  }

  public static Number queryExistingValueFromDataSet( final CategoryDataset dataset,
                                                      final Comparable seriesName,
                                                      final Comparable columnKey ) {
    try {
      return dataset.getValue( seriesName, columnKey );
    } catch ( Exception ignored ) {
      // dataset.getValue throws exceptions if the keys dont match ..
    }
    return null;
  }

  public static BigDecimal add( final Number a, final Number b ) {
    if ( a instanceof BigDecimal ) {
      final BigDecimal ab = (BigDecimal) a;
      return ab.add( new BigDecimal( b.toString() ) );
    }
    if ( b instanceof BigDecimal ) {
      final BigDecimal ab = (BigDecimal) b;
      return ab.add( new BigDecimal( a.toString() ) );
    }
    final BigDecimal ab = new BigDecimal( a.toString() );
    return ab.add( new BigDecimal( b.toString() ) );
  }
}
