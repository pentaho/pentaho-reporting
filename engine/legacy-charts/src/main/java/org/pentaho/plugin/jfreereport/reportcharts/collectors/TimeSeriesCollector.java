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

import org.jfree.data.general.Dataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.util.Sequence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/**
 * The number of entries in the series, value, and timeValueColumns properties must be the same. The function will
 * collect from tuples of {series, value (y), timeValue (x)}.
 *
 * @author Thomas Morgner.
 */
public class TimeSeriesCollector extends AbstractCollectorFunction {
  /**
   * @noinspection EqualsAndHashcode
   */
  private static class FastTimeSeriesCollection extends TimeSeriesCollection {
    private static final long serialVersionUID = 2096209400748561882L;

    /**
     * Superclass hashcode is WAY slow.
     *
     * @see TimeSeriesCollection#hashCode()
     */
    public int hashCode() {
      return this.getSeriesCount();
    }
  }

  private Class timePeriod;
  private ArrayList<String> valueColumns;
  private ArrayList<String> timeValueColumns;
  private HashMap<ReportStateKey, Sequence<HashMap<Comparable, TimeSeries>>> seriesSequenceMap;

  public TimeSeriesCollector() {
    this.valueColumns = new ArrayList<String>();
    this.timeValueColumns = new ArrayList<String>();
    this.timePeriod = Day.class;
    this.seriesSequenceMap = new HashMap<ReportStateKey, Sequence<HashMap<Comparable, TimeSeries>>>();
  }

  protected Dataset createNewDataset() {
    return new FastTimeSeriesCollection();
  }

  public Class getTimePeriod() {
    return timePeriod;
  }

  public void setTimePeriod( final Class timePeriod ) {
    this.timePeriod = timePeriod;
  }

  protected HashMap<Comparable, TimeSeries> getSeriesMap() {
    final ReportStateKey key = getStateKey();
    Sequence<HashMap<Comparable, TimeSeries>> sequence = seriesSequenceMap.get( key );
    if ( sequence == null ) {
      sequence = new Sequence<HashMap<Comparable, TimeSeries>>();
      seriesSequenceMap.put( key, sequence );
    }

    HashMap<Comparable, TimeSeries> map = sequence.get( getLastGroupSequenceNumber() );
    if ( map == null ) {
      map = new HashMap<Comparable, TimeSeries>();
      sequence.set( getLastGroupSequenceNumber(), map );
    }
    return map;
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
    return valueColumns.get( index );
  }

  public int getValueColumnCount() {
    return valueColumns.size();
  }

  public String[] getValueColumn() {
    return valueColumns.toArray( new String[ valueColumns.size() ] );
  }

  public void setValueColumn( final String[] fields ) {
    this.valueColumns.clear();
    this.valueColumns.addAll( Arrays.asList( fields ) );
  }

  public String getTimeValueColumn( final int index ) {
    return timeValueColumns.get( index );
  }

  public int getTimeValueColumnCount() {
    return timeValueColumns.size();
  }

  public String[] getTimeValueColumn() {
    return timeValueColumns.toArray( new String[ timeValueColumns.size() ] );
  }

  public void setTimeValueColumn( final String[] fields ) {
    this.timeValueColumns.clear();
    this.timeValueColumns.addAll( Arrays.asList( fields ) );
  }

  protected void buildDataset() {
    final Object o = getDataSet();
    if ( o instanceof TimeSeriesCollection == false ) {
      return;
    }

    final TimeSeriesCollection timeSeriesDataset = (TimeSeriesCollection) o;
    final List seriesList = timeSeriesDataset.getSeries();
    final HashMap<Comparable, TimeSeries> seriesMap = getSeriesMap();
    if ( seriesMap.isEmpty() ) {
      for ( int i = 0; i < seriesList.size(); i++ ) {
        final TimeSeries series = (TimeSeries) seriesList.get( i );
        seriesMap.put( series.getKey(), series );
      }
    }

    final int maxIndex = this.valueColumns.size();
    for ( int i = 0; i < maxIndex; i++ ) {
      final Comparable seriesName = querySeriesValue( i );
      final Object valueObject = getDataRow().get( valueColumns.get( i ) );
      final Object timeValueObject = getDataRow().get( timeValueColumns.get( i ) );

      final Number value = ( valueObject instanceof Number ) ? (Number) valueObject : null;
      final Date timeValue = convertToDate( timeValueObject );
      if ( timeValue == null ) {
        continue;
      }

      TimeSeries series = seriesMap.get( seriesName );
      if ( series == null ) {
        series = new TimeSeries( seriesName );
        timeSeriesDataset.addSeries( series );
        seriesMap.put( seriesName, series );
      }

      final RegularTimePeriod regularTimePeriod =
        RegularTimePeriod.createInstance( getTimePeriod(), timeValue, TimeZone.getDefault() );

      final TimeSeriesDataItem timeSeriesDataItem = new TimeSeriesDataItem( regularTimePeriod, value );
      series.add( timeSeriesDataItem );
    }
  }

  private Date convertToDate( final Object value ) {
    if ( value instanceof Date ) {
      return (Date) value;
    }
    if ( value instanceof Number ) {
      final Number n = (Number) value;
      return new Date( n.longValue() );
    }
    return null;

  }

  /**
   * Receives notification that report generation has completed, the report footer was printed, no more output is done.
   * This is a helper event to shut down the output service.
   *
   * @param event The event.
   */
  public void reportDone( final ReportEvent event ) {
    seriesSequenceMap.clear();
  }

  /**
   * Return a completly separated copy of this function. The copy does no longer share any changeable objects with the
   * original function.
   *
   * @return a copy of this function.
   */
  public Expression getInstance() {
    final TimeSeriesCollector expression = (TimeSeriesCollector) super.getInstance();
    expression.valueColumns = (ArrayList<String>) valueColumns.clone();
    expression.timeValueColumns = (ArrayList<String>) timeValueColumns.clone();
    expression.seriesSequenceMap = new HashMap<ReportStateKey, Sequence<HashMap<Comparable, TimeSeries>>>();
    return expression;
  }
}
