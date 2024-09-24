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
