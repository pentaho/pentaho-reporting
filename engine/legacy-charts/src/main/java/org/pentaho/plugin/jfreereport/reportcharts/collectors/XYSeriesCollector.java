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

import org.jfree.data.general.Dataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.util.Sequence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class XYSeriesCollector extends AbstractCollectorFunction {
  private ArrayList<String> xValueColumns;
  private ArrayList<String> yValueColumns;
  private HashMap<ReportStateKey, Sequence<HashMap<Comparable, XYSeries>>> seriesSequenceMap;

  public XYSeriesCollector() {
    xValueColumns = new ArrayList<String>();
    yValueColumns = new ArrayList<String>();
    this.seriesSequenceMap = new HashMap<ReportStateKey, Sequence<HashMap<Comparable, XYSeries>>>();
  }

  protected HashMap<Comparable, XYSeries> getSeriesMap() {
    final ReportStateKey key = getStateKey();
    Sequence<HashMap<Comparable, XYSeries>> sequence = seriesSequenceMap.get( key );
    if ( sequence == null ) {
      sequence = new Sequence<HashMap<Comparable, XYSeries>>();
      seriesSequenceMap.put( key, sequence );
    }

    HashMap<Comparable, XYSeries> map = sequence.get( getLastGroupSequenceNumber() );
    if ( map == null ) {
      map = new HashMap<Comparable, XYSeries>();
      sequence.set( getLastGroupSequenceNumber(), map );
    }
    return map;
  }

  protected Dataset createNewDataset() {
    return new XYSeriesCollection();
  }

  public void setXValueColumn( final int index, final String field ) {
    if ( xValueColumns.size() == index ) {
      xValueColumns.add( field );
    } else {
      xValueColumns.set( index, field );
    }
  }

  public String getXValueColumn( final int index ) {
    return xValueColumns.get( index );
  }

  public int getXValueColumnCount() {
    return xValueColumns.size();
  }

  public String[] getXValueColumn() {
    return xValueColumns.toArray( new String[ xValueColumns.size() ] );
  }

  public void setXValueColumn( final String[] fields ) {
    this.xValueColumns.clear();
    this.xValueColumns.addAll( Arrays.asList( fields ) );
  }

  public void setYValueColumn( final int index, final String field ) {
    if ( yValueColumns.size() == index ) {
      yValueColumns.add( field );
    } else {
      yValueColumns.set( index, field );
    }
  }


  public String getYValueColumn( final int index ) {
    return yValueColumns.get( index );
  }

  public int getYValueColumnCount() {
    return yValueColumns.size();
  }

  public String[] getYValueColumn() {
    return yValueColumns.toArray( new String[ yValueColumns.size() ] );
  }

  public void setYValueColumn( final String[] fields ) {
    this.yValueColumns.clear();
    this.yValueColumns.addAll( Arrays.asList( fields ) );
  }

  /**
   * Return a completly separated copy of this function. The copy does no longer share any changeable objects with the
   * original function.
   *
   * @return a copy of this function.
   */
  public Expression getInstance() {
    final XYSeriesCollector expression = (XYSeriesCollector) super.getInstance();
    expression.xValueColumns = (ArrayList<String>) xValueColumns.clone();
    expression.yValueColumns = (ArrayList<String>) yValueColumns.clone();
    expression.seriesSequenceMap = new HashMap<ReportStateKey, Sequence<HashMap<Comparable, XYSeries>>>();
    return expression;

  }


  protected void buildDataset() {
    final XYSeriesCollection xySeriesDataset = (XYSeriesCollection) getDataSet();
    final List seriesList = xySeriesDataset.getSeries();
    final HashMap<Comparable, XYSeries> seriesMap = getSeriesMap();
    if ( seriesMap.isEmpty() ) {
      for ( int i = 0; i < seriesList.size(); i++ ) {
        final XYSeries series = (XYSeries) seriesList.get( i );
        seriesMap.put( series.getKey(), series );
      }
    }

    final int maxIndex = Math.min( this.xValueColumns.size(), this.yValueColumns.size() );
    for ( int i = 0; i < maxIndex; i++ ) {
      final Comparable seriesName = querySeriesValue( i );
      final Object xValueObject = getDataRow().get( xValueColumns.get( i ) );
      final Object yValueObject = getDataRow().get( yValueColumns.get( i ) );

      if ( xValueObject instanceof Number == false ) {
        continue;
      }
      final Number xValue = (Number) xValueObject;
      final Number yValue = ( yValueObject instanceof Number ) ? (Number) yValueObject : null;

      XYSeries series = seriesMap.get( seriesName );
      if ( series == null ) {
        series = new XYSeries( seriesName );
        xySeriesDataset.addSeries( series );
        seriesMap.put( seriesName, series );
      }

      series.add( xValue, yValue, false );
    }
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
}
