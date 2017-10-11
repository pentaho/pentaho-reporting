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

import org.jfree.data.general.Dataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Month;
import org.jfree.data.time.Quarter;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.Week;
import org.jfree.data.time.Year;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.FunctionUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

/**
 * Creation-Date: 02.07.2007
 *
 * @author Gretchen Moran
 * @deprecated Dont use this one, use the TimeSeriesCollector instead.
 */
public class TimeSeriesCollectorFunction extends BaseCollectorFunction {
  /**
   * @noinspection EqualsAndHashcode
   */
  private static class FastTimeSeriesCollection extends TimeSeriesCollection {
    private static final long serialVersionUID = 2096209400748561882L;

    /**
     * Constructs an empty dataset, tied to the default timezone.
     */
    private FastTimeSeriesCollection() {
    }

    /**
     * Superclass hashcode is WAY slow.
     *
     * @see TimeSeriesCollection#hashCode()
     */
    public int hashCode() {
      return this.getSeriesCount();
    }
  }

  private static final long serialVersionUID = -8138304452870844825L;

  public static final String DAY_PERIOD_TYPE_STR = "Day"; //$NON-NLS-1$
  public static final String FIXEDMILLISECOND_PERIOD_TYPE_STR = "FixedMillisecond"; //$NON-NLS-1$
  public static final String HOUR_PERIOD_TYPE_STR = "Hour"; //$NON-NLS-1$
  public static final String MILLISECOND_PERIOD_TYPE_STR = "Millisecond"; //$NON-NLS-1$
  public static final String MINUTE_PERIOD_TYPE_STR = "Minute"; //$NON-NLS-1$
  public static final String MONTH_PERIOD_TYPE_STR = "Month"; //$NON-NLS-1$
  public static final String QUARTER_PERIOD_TYPE_STR = "Quarter"; //$NON-NLS-1$
  public static final String SECOND_PERIOD_TYPE_STR = "Second"; //$NON-NLS-1$
  public static final String WEEK_PERIOD_TYPE_STR = "Week"; //$NON-NLS-1$
  public static final String YEAR_PERIOD_TYPE_STR = "Year"; //$NON-NLS-1$

  private ArrayList valueColumns;
  private ArrayList timeValueColumns;
  private String domainPeriodType;

  public TimeSeriesCollectorFunction() {
    super();
    this.valueColumns = new ArrayList();
    this.timeValueColumns = new ArrayList();
    domainPeriodType = MILLISECOND_PERIOD_TYPE_STR;
  }

  public void setValueColumn( final int index, final String field ) {
    if ( valueColumns.size() == index ) {
      valueColumns.add( field );
    } else {
      valueColumns.set( index, field );
    }
  }

  public void setTimeValueColumn( final int index, final String field ) {
    if ( timeValueColumns.size() == index ) {
      timeValueColumns.add( field );
    } else {
      timeValueColumns.set( index, field );
    }
  }

  public String getValueColumn( final int index ) {
    return (String) valueColumns.get( index );
  }

  public int getValueColumnCount() {
    return valueColumns.size();
  }

  public String[] getValueColumn() {
    return (String[]) valueColumns.toArray( new String[ valueColumns.size() ] );
  }

  public void setValueColumn( final String[] fields ) {
    this.valueColumns.clear();
    this.valueColumns.addAll( Arrays.asList( fields ) );
  }

  public String getTimeValueColumn( final int index ) {
    return (String) timeValueColumns.get( index );
  }

  public int getTimeValueColumnCount() {
    return timeValueColumns.size();
  }

  public String[] getTimeValueColumn() {
    return (String[]) timeValueColumns.toArray( new String[ timeValueColumns.size() ] );
  }

  public void setTimeValueColumn( final String[] fields ) {
    this.timeValueColumns.clear();
    this.timeValueColumns.addAll( Arrays.asList( fields ) );
  }

  /*
   * ---------------------------------------------------------------- Now the function implementation ...
   */

  public void itemsAdvanced( final ReportEvent reportEvent ) {
    if ( FunctionUtilities.isDefinedPrepareRunLevel( this, reportEvent ) == false ) {
      // we do not modify the created dataset if this is not the function
      // computation run. (FunctionLevel '0')
      return;
    }
    if ( !isSummaryOnly() ) {
      buildDataset();
    }
  }

  public void groupFinished( final ReportEvent reportEvent ) {

    if ( FunctionUtilities.isDefinedPrepareRunLevel( this, reportEvent ) == false ) {
      // we do not modify the created dataset if this is not the function
      // computation run. (FunctionLevel '0')
      return;
    }

    if ( isSummaryOnly() ) {
      if ( FunctionUtilities.isDefinedGroup( getGroup(), reportEvent ) ) {
        // we can be sure that everything has been computed here. So
        // grab the values and add them to the dataset.
        buildDataset();
      }
    }
  }

  private void buildDataset() {
    final Object o = getDatasourceValue();
    if ( !( o instanceof TimeSeriesCollection ) ) {
      return;
    }
    final TimeSeriesCollection timeSeriesDataset = (TimeSeriesCollection) o;

    final int maxIndex =
      Math.min( getSeriesNameCount(), Math.min( this.valueColumns.size(), timeValueColumns.size() ) );
    for ( int i = 0; i < maxIndex; i++ ) {
      String seriesName = getSeriesName( i );
      final String column = (String) valueColumns.get( i );
      final Object valueObject = getDataRow().get( column );
      final String timeColumn = (String) timeValueColumns.get( i );
      final Object timeValueObject = getDataRow().get( timeColumn );

      if ( isSeriesColumn() ) {
        final Object tmp = getDataRow().get( seriesName );
        if ( tmp != null ) {
          seriesName = tmp.toString();
        }
      }

      final Number value = ( valueObject instanceof Number ) ? (Number) valueObject : null;

      final Date timeValue;
      if ( timeValueObject instanceof Date ) {
        timeValue = (Date) timeValueObject;
      } else {
        timeValue = new Date();
      }

      TimeSeries series = null;
      try {
        series = timeSeriesDataset.getSeries( seriesName );
      } catch ( Exception ignored ) {
      }

      final Class timePeriodClass = getTimePeriodClass( getDomainPeriodType() );
      if ( series == null ) {
        series = new TimeSeries( seriesName, timePeriodClass );
        timeSeriesDataset.addSeries( series );
      }
      final RegularTimePeriod regularTimePeriod =
        RegularTimePeriod.createInstance( timePeriodClass, timeValue, TimeZone.getDefault() );

      final TimeSeriesDataItem timeSeriesDataItem = new TimeSeriesDataItem( regularTimePeriod, value );
      series.add( timeSeriesDataItem );
    }
  }

  public Dataset createNewDataset() {
    return new FastTimeSeriesCollection();
  }

  public String getDomainPeriodType() {
    return domainPeriodType;
  }

  public void setDomainPeriodType( final String domainPeriodType ) {
    this.domainPeriodType = domainPeriodType;
  }

  /**
   * Return a completly separated copy of this function. The copy no longer shares any changeable objects with the
   * original function. Also from Thomas: Should retain data from the report definition, but clear calculated data.
   *
   * @return a copy of this function.
   */
  public Expression getInstance() {
    final TimeSeriesCollectorFunction fn = (TimeSeriesCollectorFunction) super.getInstance();
    fn.valueColumns = (ArrayList) valueColumns.clone();
    fn.timeValueColumns = (ArrayList) timeValueColumns.clone();
    return fn;
  }


  private static Class getTimePeriodClass( final String timePeriodStr ) {
    Class retClass = Millisecond.class;
    if ( timePeriodStr.equalsIgnoreCase( SECOND_PERIOD_TYPE_STR ) ) {
      retClass = Second.class;
    } else if ( timePeriodStr.equalsIgnoreCase( MINUTE_PERIOD_TYPE_STR ) ) {
      retClass = Minute.class;
    } else if ( timePeriodStr.equalsIgnoreCase( HOUR_PERIOD_TYPE_STR ) ) {
      retClass = Hour.class;
    } else if ( timePeriodStr.equalsIgnoreCase( DAY_PERIOD_TYPE_STR ) ) {
      retClass = Day.class;
    } else if ( timePeriodStr.equalsIgnoreCase( WEEK_PERIOD_TYPE_STR ) ) {
      retClass = Week.class;
    } else if ( timePeriodStr.equalsIgnoreCase( MONTH_PERIOD_TYPE_STR ) ) {
      retClass = Month.class;
    } else if ( timePeriodStr.equalsIgnoreCase( QUARTER_PERIOD_TYPE_STR ) ) {
      retClass = Quarter.class;
    } else if ( timePeriodStr.equalsIgnoreCase( YEAR_PERIOD_TYPE_STR ) ) {
      retClass = Year.class;
    }
    return retClass;
  }

}
