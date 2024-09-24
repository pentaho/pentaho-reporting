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
import org.pentaho.plugin.jfreereport.reportcharts.ExtendedXYZDataset;
import org.pentaho.reporting.engine.classic.core.function.Expression;

import java.util.ArrayList;
import java.util.Arrays;

public class XYZSeriesCollector extends AbstractCollectorFunction {
  private ArrayList<String> xValueColumns;
  private ArrayList<String> yValueColumns;
  private ArrayList<String> zValueColumns;

  public XYZSeriesCollector() {
    xValueColumns = new ArrayList<String>();
    yValueColumns = new ArrayList<String>();
    zValueColumns = new ArrayList<String>();
  }

  protected Dataset createNewDataset() {
    return new ExtendedXYZDataset();
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

  public void setZValueColumn( final int index, final String field ) {
    if ( zValueColumns.size() == index ) {
      zValueColumns.add( field );
    } else {
      zValueColumns.set( index, field );
    }
  }


  public String getZValueColumn( final int index ) {
    return zValueColumns.get( index );
  }

  public int getZValueColumnCount() {
    return zValueColumns.size();
  }

  public String[] getZValueColumn() {
    return zValueColumns.toArray( new String[ zValueColumns.size() ] );
  }

  public void setZValueColumn( final String[] fields ) {
    this.zValueColumns.clear();
    this.zValueColumns.addAll( Arrays.asList( fields ) );
  }

  /**
   * Return a completly separated copy of this function. The copy does no longer share any changeable objects with the
   * original function.
   *
   * @return a copy of this function.
   */
  public Expression getInstance() {
    final XYZSeriesCollector expression = (XYZSeriesCollector) super.getInstance();
    expression.xValueColumns = (ArrayList<String>) xValueColumns.clone();
    expression.yValueColumns = (ArrayList<String>) yValueColumns.clone();
    expression.zValueColumns = (ArrayList<String>) zValueColumns.clone();
    return expression;
  }


  protected void buildDataset() {
    final ExtendedXYZDataset xySeriesDataset = (ExtendedXYZDataset) getDataSet();

    final int maxIndex = Math.min( this.xValueColumns.size(),
      Math.min( this.yValueColumns.size(), this.zValueColumns.size() ) );
    for ( int i = 0; i < maxIndex; i++ ) {
      final Comparable seriesName = querySeriesValue( i );
      final Object xValueObject = getDataRow().get( xValueColumns.get( i ) );
      final Object yValueObject = getDataRow().get( yValueColumns.get( i ) );
      final Object zValueObject = getDataRow().get( zValueColumns.get( i ) );
      final Number xValue = ( xValueObject instanceof Number ) ? (Number) xValueObject : null;
      final Number yValue = ( yValueObject instanceof Number ) ? (Number) yValueObject : null;
      final Number zValue = ( zValueObject instanceof Number ) ? (Number) zValueObject : null;

      if ( xValue == null || yValue == null || zValue == null ) {
        continue;
      }

      if ( zValue.doubleValue() > xySeriesDataset.getMaxZValue() ) {
        xySeriesDataset.setMaxZValue( zValue.doubleValue() );
      }

      final double[][] seriesValues = new double[ 3 ][ 1 ];
      seriesValues[ 0 ][ 0 ] = xValue.doubleValue();
      seriesValues[ 1 ][ 0 ] = yValue.doubleValue();
      seriesValues[ 2 ][ 0 ] = zValue.doubleValue();

      xySeriesDataset.addSeries( seriesName, seriesValues );
    }
  }

}
