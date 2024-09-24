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
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;
import org.pentaho.reporting.engine.classic.core.function.Expression;

import java.util.ArrayList;
import java.util.Arrays;

public class IntervalXYSeriesCollector extends AbstractCollectorFunction {
  private ArrayList<String> xMinValueColumns;
  private ArrayList<String> yMinValueColumns;
  private ArrayList<String> xMaxValueColumns;
  private ArrayList<String> yMaxValueColumns;

  public IntervalXYSeriesCollector() {
    xMinValueColumns = new ArrayList<String>();
    yMinValueColumns = new ArrayList<String>();
    xMaxValueColumns = new ArrayList<String>();
    yMaxValueColumns = new ArrayList<String>();
  }

  protected Dataset createNewDataset() {
    return new XYIntervalSeriesCollection();
  }

  public void setXMinValueColumn( final int index, final String field ) {
    if ( xMinValueColumns.size() == index ) {
      xMinValueColumns.add( field );
    } else {
      xMinValueColumns.set( index, field );
    }
  }

  public String getXMinValueColumn( final int index ) {
    return xMinValueColumns.get( index );
  }

  public int getXMinValueColumnCount() {
    return xMinValueColumns.size();
  }

  public String[] getXMinValueColumn() {
    return xMinValueColumns.toArray( new String[ xMinValueColumns.size() ] );
  }

  public void setXMinValueColumn( final String[] fields ) {
    this.xMinValueColumns.clear();
    this.xMinValueColumns.addAll( Arrays.asList( fields ) );
  }

  public void setYMinValueColumn( final int index, final String field ) {
    if ( yMinValueColumns.size() == index ) {
      yMinValueColumns.add( field );
    } else {
      yMinValueColumns.set( index, field );
    }
  }


  public String getYMinValueColumn( final int index ) {
    return yMinValueColumns.get( index );
  }

  public int getYMinValueColumnCount() {
    return yMinValueColumns.size();
  }

  public String[] getYMinValueColumn() {
    return yMinValueColumns.toArray( new String[ yMinValueColumns.size() ] );
  }

  public void setYMinValueColumn( final String[] fields ) {
    this.yMinValueColumns.clear();
    this.yMinValueColumns.addAll( Arrays.asList( fields ) );
  }

  public void setYMaxValueColumn( final int index, final String field ) {
    if ( xMaxValueColumns.size() == index ) {
      xMaxValueColumns.add( field );
    } else {
      xMaxValueColumns.set( index, field );
    }
  }


  public String getXMaxValueColumn( final int index ) {
    return xMaxValueColumns.get( index );
  }

  public int getXMaxValueColumnCount() {
    return xMaxValueColumns.size();
  }

  public String[] getXMaxValueColumn() {
    return xMaxValueColumns.toArray( new String[ xMaxValueColumns.size() ] );
  }

  public void setXMaxValueColumn( final String[] fields ) {
    this.xMaxValueColumns.clear();
    this.xMaxValueColumns.addAll( Arrays.asList( fields ) );
  }

  public String getYMaxValueColumn( final int index ) {
    return yMaxValueColumns.get( index );
  }

  public int getYMaxValueColumnCount() {
    return yMaxValueColumns.size();
  }

  public String[] getYMaxValueColumn() {
    return yMaxValueColumns.toArray( new String[ yMaxValueColumns.size() ] );
  }

  public void setYMaxValueColumn( final String[] fields ) {
    this.yMaxValueColumns.clear();
    this.yMaxValueColumns.addAll( Arrays.asList( fields ) );
  }

  /**
   * Return a completly separated copy of this function. The copy does no longer share any changeable objects with the
   * original function.
   *
   * @return a copy of this function.
   */
  public Expression getInstance() {
    final IntervalXYSeriesCollector expression = (IntervalXYSeriesCollector) super.getInstance();
    expression.xMinValueColumns = (ArrayList<String>) xMinValueColumns.clone();
    expression.yMinValueColumns = (ArrayList<String>) yMinValueColumns.clone();
    expression.xMaxValueColumns = (ArrayList<String>) xMaxValueColumns.clone();
    expression.yMaxValueColumns = (ArrayList<String>) yMaxValueColumns.clone();
    return expression;
  }


  protected void buildDataset() {
    final XYIntervalSeriesCollection xyIntervalxySeriesDataset = (XYIntervalSeriesCollection) getDataSet();

    final int maxIndex = Math.min( this.yMaxValueColumns.size(),
      Math.min( this.xMinValueColumns.size(),
        Math.min( this.yMinValueColumns.size(), this.xMaxValueColumns.size() ) ) );
    for ( int i = 0; i < maxIndex; i++ ) {
      final Comparable seriesName = querySeriesValue( i );
      final Object xValueObject = getDataRow().get( xMinValueColumns.get( i ) );
      final Object yValueObject = getDataRow().get( yMinValueColumns.get( i ) );
      final Object xMaxValueObject = getDataRow().get( xMaxValueColumns.get( i ) );
      final Object yMaxValueObject = getDataRow().get( yMaxValueColumns.get( i ) );

      final Number xValue = ( xValueObject instanceof Number ) ? (Number) xValueObject : null;
      final Number yValue = ( yValueObject instanceof Number ) ? (Number) yValueObject : null;
      final Number xMaxValue = ( xMaxValueObject instanceof Number ) ? (Number) xMaxValueObject : null;
      final Number yMaxValue = ( yMaxValueObject instanceof Number ) ? (Number) yMaxValueObject : null;

      if ( xValue == null || yValue == null || xMaxValue == null || yMaxValue == null ) {
        continue;
      }


      //find series
      final XYIntervalSeries xyIntervalSeries;
      final int index = xyIntervalxySeriesDataset.indexOf( seriesName );
      if ( index == -1 ) {
        xyIntervalSeries = new XYIntervalSeries( seriesName );
        xyIntervalxySeriesDataset.addSeries( xyIntervalSeries );
      } else {
        xyIntervalSeries = xyIntervalxySeriesDataset.getSeries( index );
      }


      xyIntervalSeries.add( xValue.doubleValue(), xValue.doubleValue(), xMaxValue.doubleValue(),
        yValue.doubleValue(), yValue.doubleValue(), yMaxValue.doubleValue() );


    }
  }

}
