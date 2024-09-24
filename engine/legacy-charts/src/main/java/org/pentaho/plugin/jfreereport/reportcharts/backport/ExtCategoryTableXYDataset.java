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

package org.pentaho.plugin.jfreereport.reportcharts.backport;

import org.jfree.data.DefaultKeyedValues2D;
import org.jfree.data.DomainInfo;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.CategoryTableXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.IntervalXYDelegate;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.util.PublicCloneable;

/**
 * An implementation variant of the {@link org.jfree.data.xy.TableXYDataset} where every series shares the same x-values
 * (required for generating stacked area charts). This implementation uses a {@link org.jfree.data.DefaultKeyedValues2D}
 * Object as backend implementation and is hence more "category oriented" than the {@link
 * org.jfree.data.xy.DefaultTableXYDataset} implementation.
 * <p/>
 * This implementation provides no means to remove data items yet. This is due to the lack of such facility in the
 * DefaultKeyedValues2D class.
 * <p/>
 * This class also implements the {@link org.jfree.data.xy.IntervalXYDataset} interface, but this implementation is
 * provisional.
 */
public class ExtCategoryTableXYDataset extends AbstractIntervalXYDataset
  implements TableXYDataset, IntervalXYDataset, DomainInfo,
  PublicCloneable {

  /**
   * The backing data structure.
   */
  private DefaultKeyedValues2D values;

  /**
   * A delegate for controlling the interval width.
   */
  private IntervalXYDelegate intervalDelegate;

  /**
   * Creates a new empty CategoryTableXYDataset.
   */
  public ExtCategoryTableXYDataset() {
    this.values = new DefaultKeyedValues2D( true );
    this.intervalDelegate = new IntervalXYDelegate( this );
    addChangeListener( this.intervalDelegate );
  }

  /**
   * Adds a data item to this dataset and sends a {@link org.jfree.data.general.DatasetChangeEvent} to all registered
   * listeners.
   *
   * @param x          the x value.
   * @param y          the y value.
   * @param seriesName the name of the series to add the data item.
   */
  public void add( final double x, final double y, final Comparable seriesName ) {
    add( new Double( x ), new Double( y ), seriesName, true );
  }

  /**
   * Adds a data item to this dataset and, if requested, sends a {@link org.jfree.data.general.DatasetChangeEvent} to
   * all registered listeners.
   *
   * @param x          the x value.
   * @param y          the y value.
   * @param seriesName the name of the series to add the data item.
   * @param notify     notify listeners?
   */
  public void add( final Number x, final Number y, final Comparable seriesName, final boolean notify ) {
    this.values.addValue( y, (Comparable) x, seriesName );
    if ( notify ) {
      fireDatasetChanged();
    }
  }

  /**
   * Removes a value from the dataset.
   *
   * @param x          the x-value.
   * @param seriesName the series name.
   */
  public void remove( final double x, final Comparable seriesName ) {
    remove( new Double( x ), seriesName, true );
  }

  /**
   * Removes an item from the dataset.
   *
   * @param x          the x-value.
   * @param seriesName the series name.
   * @param notify     notify listeners?
   */
  public void remove( final Number x, final Comparable seriesName, final boolean notify ) {
    this.values.removeValue( (Comparable) x, seriesName );
    if ( notify ) {
      fireDatasetChanged();
    }
  }


  /**
   * Returns the number of series in the collection.
   *
   * @return The series count.
   */
  public int getSeriesCount() {
    return this.values.getColumnCount();
  }

  /**
   * Returns the key for a series.
   *
   * @param series the series index (zero-based).
   * @return The key for a series.
   */
  public Comparable getSeriesKey( final int series ) {
    return this.values.getColumnKey( series );
  }

  /**
   * Returns the number of x values in the dataset.
   *
   * @return The item count.
   */
  public int getItemCount() {
    return this.values.getRowCount();
  }

  /**
   * Returns the number of items in the specified series. Returns the same as {@link
   * CategoryTableXYDataset#getItemCount()}.
   *
   * @param series the series index (zero-based).
   * @return The item count.
   */
  public int getItemCount( final int series ) {
    return getItemCount();  // all series have the same number of items in
    // this dataset
  }

  /**
   * Returns the x-value for the specified series and item.
   *
   * @param series the series index (zero-based).
   * @param item   the item index (zero-based).
   * @return The value.
   */
  public Number getX( final int series, final int item ) {
    return (Number) this.values.getRowKey( item );
  }

  /**
   * Returns the starting X value for the specified series and item.
   *
   * @param series the series index (zero-based).
   * @param item   the item index (zero-based).
   * @return The starting X value.
   */
  public Number getStartX( final int series, final int item ) {
    return this.intervalDelegate.getStartX( series, item );
  }

  /**
   * Returns the ending X value for the specified series and item.
   *
   * @param series the series index (zero-based).
   * @param item   the item index (zero-based).
   * @return The ending X value.
   */
  public Number getEndX( final int series, final int item ) {
    return this.intervalDelegate.getEndX( series, item );
  }

  /**
   * Returns the y-value for the specified series and item.
   *
   * @param series the series index (zero-based).
   * @param item   the item index (zero-based).
   * @return The y value (possibly <code>null</code>).
   */
  public Number getY( final int series, final int item ) {
    return this.values.getValue( item, series );
  }

  /**
   * Returns the starting Y value for the specified series and item.
   *
   * @param series the series index (zero-based).
   * @param item   the item index (zero-based).
   * @return The starting Y value.
   */
  public Number getStartY( final int series, final int item ) {
    return getY( series, item );
  }

  /**
   * Returns the ending Y value for the specified series and item.
   *
   * @param series the series index (zero-based).
   * @param item   the item index (zero-based).
   * @return The ending Y value.
   */
  public Number getEndY( final int series, final int item ) {
    return getY( series, item );
  }

  /**
   * Returns the minimum x-value in the dataset.
   *
   * @param includeInterval a flag that determines whether or not the x-interval is taken into account.
   * @return The minimum value.
   */
  public double getDomainLowerBound( final boolean includeInterval ) {
    return this.intervalDelegate.getDomainLowerBound( includeInterval );
  }

  /**
   * Returns the maximum x-value in the dataset.
   *
   * @param includeInterval a flag that determines whether or not the x-interval is taken into account.
   * @return The maximum value.
   */
  public double getDomainUpperBound( final boolean includeInterval ) {
    return this.intervalDelegate.getDomainUpperBound( includeInterval );
  }

  /**
   * Returns the range of the values in this dataset's domain.
   *
   * @param includeInterval a flag that determines whether or not the x-interval is taken into account.
   * @return The range.
   */
  public Range getDomainBounds( final boolean includeInterval ) {
    if ( includeInterval ) {
      return this.intervalDelegate.getDomainBounds( includeInterval );
    } else {
      return DatasetUtilities.iterateDomainBounds( this, includeInterval );
    }
  }

  /**
   * Returns the interval position factor.
   *
   * @return The interval position factor.
   */
  public double getIntervalPositionFactor() {
    return this.intervalDelegate.getIntervalPositionFactor();
  }

  /**
   * Sets the interval position factor. Must be between 0.0 and 1.0 inclusive. If the factor is 0.5, the gap is in the
   * middle of the x values. If it is lesser than 0.5, the gap is farther to the left and if greater than 0.5 it gets
   * farther to the right.
   *
   * @param d the new interval position factor.
   */
  public void setIntervalPositionFactor( final double d ) {
    this.intervalDelegate.setIntervalPositionFactor( d );
    fireDatasetChanged();
  }

  /**
   * Returns the full interval width.
   *
   * @return The interval width to use.
   */
  public double getIntervalWidth() {
    return this.intervalDelegate.getIntervalWidth();
  }

  /**
   * Sets the interval width to a fixed value, and sends a {@link org.jfree.data.general.DatasetChangeEvent} to all
   * registered listeners.
   *
   * @param d the new interval width (must be > 0).
   */
  public void setIntervalWidth( final double d ) {
    this.intervalDelegate.setFixedIntervalWidth( d );
    fireDatasetChanged();
  }

  /**
   * Returns whether the interval width is automatically calculated or not.
   *
   * @return whether the width is automatically calculated or not.
   */
  public boolean isAutoWidth() {
    return this.intervalDelegate.isAutoWidth();
  }

  /**
   * Sets the flag that indicates whether the interval width is automatically calculated or not.
   *
   * @param b the flag.
   */
  public void setAutoWidth( final boolean b ) {
    this.intervalDelegate.setAutoWidth( b );
    fireDatasetChanged();
  }

  /**
   * Tests this dataset for equality with an arbitrary object.
   *
   * @param obj the object (<code>null</code> permitted).
   * @return A boolean.
   */
  public boolean equals( final Object obj ) {
    if ( !( obj instanceof ExtCategoryTableXYDataset ) ) {
      return false;
    }
    final ExtCategoryTableXYDataset that = (ExtCategoryTableXYDataset) obj;
    if ( !this.intervalDelegate.equals( that.intervalDelegate ) ) {
      return false;
    }
    if ( !this.values.equals( that.values ) ) {
      return false;
    }
    return true;
  }

  /**
   * Returns an independent copy of this dataset.
   *
   * @return A clone.
   * @throws CloneNotSupportedException if there is some reason that cloning cannot be performed.
   */
  public Object clone() throws CloneNotSupportedException {
    final ExtCategoryTableXYDataset clone = (ExtCategoryTableXYDataset) super.clone();
    clone.values = (DefaultKeyedValues2D) this.values.clone();
    clone.intervalDelegate = new IntervalXYDelegate( clone );
    // need to configure the intervalDelegate to match the original
    clone.intervalDelegate.setFixedIntervalWidth( getIntervalWidth() );
    clone.intervalDelegate.setAutoWidth( isAutoWidth() );
    clone.intervalDelegate.setIntervalPositionFactor(
      getIntervalPositionFactor() );
    return clone;
  }

}
