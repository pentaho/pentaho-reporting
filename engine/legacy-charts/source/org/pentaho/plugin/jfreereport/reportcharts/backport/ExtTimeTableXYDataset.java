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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.plugin.jfreereport.reportcharts.backport;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.jfree.data.DefaultKeyedValues2D;
import org.jfree.data.DomainInfo;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimePeriod;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.util.PublicCloneable;

/**
 * A dataset for regular time periods that implements the
 * {@link TableXYDataset} interface.  Note that the {@link TableXYDataset}
 * interface requires all series to share the same set of x-values.  When
 * adding a new item <code>(x, y)</code> to one series, all other series
 * automatically get a new item <code>(x, null)</code> unless a non-null item
 * has already been specified.
 *
 * @see org.jfree.data.xy.TableXYDataset
 */
public class ExtTimeTableXYDataset extends AbstractIntervalXYDataset
    implements Cloneable, PublicCloneable, DomainInfo, TableXYDataset
{

  /**
   * The data structure to store the values.  Each column represents
   * a series (elsewhere in JFreeChart rows are typically used for series,
   * but it doesn't matter that much since this data structure is private
   * and symmetrical anyway), each row contains values for the same
   * {@link RegularTimePeriod} (the rows are sorted into ascending order).
   */
  private DefaultKeyedValues2D values;

  /**
   * A flag that indicates that the domain is 'points in time'.  If this flag
   * is true, only the x-value (and not the x-interval) is used to determine
   * the range of values in the domain.
   */
  private boolean domainIsPointsInTime;

  /**
   * The point within each time period that is used for the X value when this
   * collection is used as an {@link org.jfree.data.xy.XYDataset}.  This can
   * be the start, middle or end of the time period.
   */
  private TimePeriodAnchor xPosition;

  /**
   * A working calendar (to recycle)
   */
  private Calendar workingCalendar;

  /**
   * Creates a new dataset.
   */
  public ExtTimeTableXYDataset()
  {
    // defer argument checking
    this(TimeZone.getDefault(), Locale.getDefault());
  }

  /**
   * Creates a new dataset with the given time zone.
   *
   * @param zone the time zone to use (<code>null</code> not permitted).
   */
  public ExtTimeTableXYDataset(final TimeZone zone)
  {
    // defer argument checking
    this(zone, Locale.getDefault());
  }

  /**
   * Creates a new dataset with the given time zone and locale.
   *
   * @param zone   the time zone to use (<code>null</code> not permitted).
   * @param locale the locale to use (<code>null</code> not permitted).
   */
  public ExtTimeTableXYDataset(final TimeZone zone, final Locale locale)
  {
    if (zone == null)
    {
      throw new IllegalArgumentException("Null 'zone' argument.");
    }
    if (locale == null)
    {
      throw new IllegalArgumentException("Null 'locale' argument.");
    }
    this.values = new DefaultKeyedValues2D(true);
    this.workingCalendar = Calendar.getInstance(zone, locale);
    this.xPosition = TimePeriodAnchor.START;
  }

  /**
   * Returns a flag that controls whether the domain is treated as 'points in
   * time'.
   * <p/>
   * This flag is used when determining the max and min values for the domain.
   * If true, then only the x-values are considered for the max and min
   * values.  If false, then the start and end x-values will also be taken
   * into consideration.
   *
   * @return The flag.
   * @see #setDomainIsPointsInTime(boolean)
   */
  public boolean getDomainIsPointsInTime()
  {
    return this.domainIsPointsInTime;
  }

  /**
   * Sets a flag that controls whether the domain is treated as 'points in
   * time', or time periods.  A {@link DatasetChangeEvent} is sent to all
   * registered listeners.
   *
   * @param flag the new value of the flag.
   * @see #getDomainIsPointsInTime()
   */
  public void setDomainIsPointsInTime(final boolean flag)
  {
    this.domainIsPointsInTime = flag;
    notifyListeners(new DatasetChangeEvent(this, this));
  }

  /**
   * Returns the position within each time period that is used for the X
   * value.
   *
   * @return The anchor position (never <code>null</code>).
   * @see #setXPosition(TimePeriodAnchor)
   */
  public TimePeriodAnchor getXPosition()
  {
    return this.xPosition;
  }

  /**
   * Sets the position within each time period that is used for the X values,
   * then sends a {@link DatasetChangeEvent} to all registered listeners.
   *
   * @param anchor the anchor position (<code>null</code> not permitted).
   * @see #getXPosition()
   */
  public void setXPosition(final TimePeriodAnchor anchor)
  {
    if (anchor == null)
    {
      throw new IllegalArgumentException("Null 'anchor' argument.");
    }
    this.xPosition = anchor;
    notifyListeners(new DatasetChangeEvent(this, this));
  }

  /**
   * Adds a new data item to the dataset and sends a
   * {@link DatasetChangeEvent} to all registered listeners.
   *
   * @param period     the time period.
   * @param y          the value for this period.
   * @param seriesName the name of the series to add the value.
   * @see #remove(TimePeriod, Comparable)
   */
  public void add(final TimePeriod period, final double y, final Comparable seriesName)
  {
    add(period, new Double(y), seriesName, true);
  }

  /**
   * Adds a new data item to the dataset and, if requested, sends a
   * {@link DatasetChangeEvent} to all registered listeners.
   *
   * @param period     the time period (<code>null</code> not permitted).
   * @param y          the value for this period (<code>null</code> permitted).
   * @param seriesName the name of the series to add the value
   *                   (<code>null</code> not permitted).
   * @param notify     whether dataset listener are notified or not.
   * @see #remove(TimePeriod, Comparable, boolean)
   */
  public void add(final TimePeriod period, final Number y, final Comparable seriesName,
                  final boolean notify)
  {
    // here's a quirk - the API has been defined in terms of a plain
    // TimePeriod, which cannot make use of the timezone and locale
    // specified in the constructor...so we only do the time zone
    // pegging if the period is an instanceof RegularTimePeriod
    if (period instanceof RegularTimePeriod)
    {
      final RegularTimePeriod p = (RegularTimePeriod) period;
      p.peg(this.workingCalendar);
    }
    this.values.addValue(y, period, seriesName);
    if (notify)
    {
      fireDatasetChanged();
    }
  }

  /**
   * Removes an existing data item from the dataset.
   *
   * @param period     the (existing!) time period of the value to remove
   *                   (<code>null</code> not permitted).
   * @param seriesName the (existing!) series name to remove the value
   *                   (<code>null</code> not permitted).
   * @see #add(TimePeriod, double, Comparable)
   */
  public void remove(final TimePeriod period, final Comparable seriesName)
  {
    remove(period, seriesName, true);
  }

  /**
   * Removes an existing data item from the dataset and, if requested,
   * sends a {@link DatasetChangeEvent} to all registered listeners.
   *
   * @param period     the (existing!) time period of the value to remove
   *                   (<code>null</code> not permitted).
   * @param seriesName the (existing!) series name to remove the value
   *                   (<code>null</code> not permitted).
   * @param notify     whether dataset listener are notified or not.
   * @see #add(TimePeriod, double, Comparable)
   */
  public void remove(final TimePeriod period, final Comparable seriesName, final boolean notify)
  {
    this.values.removeValue(period, seriesName);
    if (notify)
    {
      fireDatasetChanged();
    }
  }

  /**
   * Removes all data items from the dataset and sends a
   * {@link DatasetChangeEvent} to all registered listeners.
   *
   * @since 1.0.7
   */
  public void clear()
  {
    if (this.values.getRowCount() > 0)
    {
      this.values.clear();
      fireDatasetChanged();
    }
  }

  /**
   * Returns the time period for the specified item.  Bear in mind that all
   * series share the same set of time periods.
   *
   * @param item the item index (0 <= i <= {@link #getItemCount()}).
   * @return The time period.
   */
  public TimePeriod getTimePeriod(final int item)
  {
    return (TimePeriod) this.values.getRowKey(item);
  }

  /**
   * Returns the number of items in ALL series.
   *
   * @return The item count.
   */
  public int getItemCount()
  {
    return this.values.getRowCount();
  }

  /**
   * Returns the number of items in a series.  This is the same value
   * that is returned by {@link #getItemCount()} since all series
   * share the same x-values (time periods).
   *
   * @param series the series (zero-based index, ignored).
   * @return The number of items within the series.
   */
  public int getItemCount(final int series)
  {
    return getItemCount();
  }

  /**
   * Returns the number of series in the dataset.
   *
   * @return The series count.
   */
  public int getSeriesCount()
  {
    return this.values.getColumnCount();
  }

  /**
   * Returns the key for a series.
   *
   * @param series the series (zero-based index).
   * @return The key for the series.
   */
  public Comparable getSeriesKey(final int series)
  {
    return this.values.getColumnKey(series);
  }

  /**
   * Returns the x-value for an item within a series.  The x-values may or
   * may not be returned in ascending order, that is up to the class
   * implementing the interface.
   *
   * @param series the series (zero-based index).
   * @param item   the item (zero-based index).
   * @return The x-value.
   */
  public Number getX(final int series, final int item)
  {
    return new Double(getXValue(series, item));
  }

  /**
   * Returns the x-value (as a double primitive) for an item within a series.
   *
   * @param series the series index (zero-based).
   * @param item   the item index (zero-based).
   * @return The value.
   */
  public double getXValue(final int series, final int item)
  {
    final TimePeriod period = (TimePeriod) this.values.getRowKey(item);
    return getXValue(period);
  }

  /**
   * Returns the starting X value for the specified series and item.
   *
   * @param series the series (zero-based index).
   * @param item   the item within a series (zero-based index).
   * @return The starting X value for the specified series and item.
   * @see #getStartXValue(int, int)
   */
  public Number getStartX(final int series, final int item)
  {
    return new Double(getStartXValue(series, item));
  }

  /**
   * Returns the start x-value (as a double primitive) for an item within
   * a series.
   *
   * @param series the series index (zero-based).
   * @param item   the item index (zero-based).
   * @return The value.
   */
  public double getStartXValue(final int series, final int item)
  {
    final TimePeriod period = (TimePeriod) this.values.getRowKey(item);
    return period.getStart().getTime();
  }

  /**
   * Returns the ending X value for the specified series and item.
   *
   * @param series the series (zero-based index).
   * @param item   the item within a series (zero-based index).
   * @return The ending X value for the specified series and item.
   * @see #getEndXValue(int, int)
   */
  public Number getEndX(final int series, final int item)
  {
    return new Double(getEndXValue(series, item));
  }

  /**
   * Returns the end x-value (as a double primitive) for an item within
   * a series.
   *
   * @param series the series index (zero-based).
   * @param item   the item index (zero-based).
   * @return The value.
   */
  public double getEndXValue(final int series, final int item)
  {
    final TimePeriod period = (TimePeriod) this.values.getRowKey(item);
    return period.getEnd().getTime();
  }

  /**
   * Returns the y-value for an item within a series.
   *
   * @param series the series (zero-based index).
   * @param item   the item (zero-based index).
   * @return The y-value (possibly <code>null</code>).
   */
  public Number getY(final int series, final int item)
  {
    return this.values.getValue(item, series);
  }

  /**
   * Returns the starting Y value for the specified series and item.
   *
   * @param series the series (zero-based index).
   * @param item   the item within a series (zero-based index).
   * @return The starting Y value for the specified series and item.
   */
  public Number getStartY(final int series, final int item)
  {
    return getY(series, item);
  }

  /**
   * Returns the ending Y value for the specified series and item.
   *
   * @param series the series (zero-based index).
   * @param item   the item within a series (zero-based index).
   * @return The ending Y value for the specified series and item.
   */
  public Number getEndY(final int series, final int item)
  {
    return getY(series, item);
  }

  /**
   * Returns the x-value for a time period.
   *
   * @param period the time period.
   * @return The x-value.
   */
  private long getXValue(final TimePeriod period)
  {
    long result = 0L;
    if (this.xPosition == TimePeriodAnchor.START)
    {
      result = period.getStart().getTime();
    }
    else if (this.xPosition == TimePeriodAnchor.MIDDLE)
    {
      final long t0 = period.getStart().getTime();
      final long t1 = period.getEnd().getTime();
      result = t0 + (t1 - t0) / 2L;
    }
    else if (this.xPosition == TimePeriodAnchor.END)
    {
      result = period.getEnd().getTime();
    }
    return result;
  }

  /**
   * Returns the minimum x-value in the dataset.
   *
   * @param includeInterval a flag that determines whether or not the
   *                        x-interval is taken into account.
   * @return The minimum value.
   */
  public double getDomainLowerBound(final boolean includeInterval)
  {
    double result = Double.NaN;
    final Range r = getDomainBounds(includeInterval);
    if (r != null)
    {
      result = r.getLowerBound();
    }
    return result;
  }

  /**
   * Returns the maximum x-value in the dataset.
   *
   * @param includeInterval a flag that determines whether or not the
   *                        x-interval is taken into account.
   * @return The maximum value.
   */
  public double getDomainUpperBound(final boolean includeInterval)
  {
    double result = Double.NaN;
    final Range r = getDomainBounds(includeInterval);
    if (r != null)
    {
      result = r.getUpperBound();
    }
    return result;
  }

  /**
   * Returns the range of the values in this dataset's domain.
   *
   * @param includeInterval a flag that controls whether or not the
   *                        x-intervals are taken into account.
   * @return The range.
   */
  public Range getDomainBounds(final boolean includeInterval)
  {
    final List keys = this.values.getRowKeys();
    if (keys.isEmpty())
    {
      return null;
    }

    final TimePeriod first = (TimePeriod) keys.get(0);
    final TimePeriod last = (TimePeriod) keys.get(keys.size() - 1);

    if (!includeInterval || this.domainIsPointsInTime)
    {
      return new Range(getXValue(first), getXValue(last));
    }
    else
    {
      return new Range(first.getStart().getTime(),
          last.getEnd().getTime());
    }
  }

  /**
   * Tests this dataset for equality with an arbitrary object.
   *
   * @param obj the object (<code>null</code> permitted).
   * @return A boolean.
   */
  public boolean equals(final Object obj)
  {
    if (obj == this)
    {
      return true;
    }
    if (!(obj instanceof ExtTimeTableXYDataset))
    {
      return false;
    }
    final ExtTimeTableXYDataset that = (ExtTimeTableXYDataset) obj;
    if (this.domainIsPointsInTime != that.domainIsPointsInTime)
    {
      return false;
    }
    if (this.xPosition != that.xPosition)
    {
      return false;
    }
    if (!this.workingCalendar.getTimeZone().equals(
        that.workingCalendar.getTimeZone())
        )
    {
      return false;
    }
    if (!this.values.equals(that.values))
    {
      return false;
    }
    return true;
  }

  /**
   * Returns a clone of this dataset.
   *
   * @return A clone.
   * @throws CloneNotSupportedException if the dataset cannot be cloned.
   */
  public Object clone() throws CloneNotSupportedException
  {
    final ExtTimeTableXYDataset clone = (ExtTimeTableXYDataset) super.clone();
    clone.values = (DefaultKeyedValues2D) this.values.clone();
    clone.workingCalendar = (Calendar) this.workingCalendar.clone();
    return clone;
  }

}
