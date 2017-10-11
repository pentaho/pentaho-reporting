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
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.Expression;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @deprecated
 */
public class XYZSeriesCollectorFunction extends XYSeriesCollectorFunction {
  private static final long serialVersionUID = -3612902208762624893L;

  private ArrayList zValueColumns;
  private double maxZValue = 1.0;

  public XYZSeriesCollectorFunction() {
    zValueColumns = new ArrayList();
  }

  public void reportInitialized( final ReportEvent event ) {
    maxZValue = 1.0;
    super.reportInitialized( event );
  }

  public void setzValueColumn( final int index, final String field ) {
    if ( zValueColumns.size() == index ) {
      zValueColumns.add( field );
    } else {
      zValueColumns.set( index, field );
    }
  }

  public String getzValueColumn( final int index ) {
    return (String) zValueColumns.get( index );
  }

  public int getzValueColumnCount() {
    return zValueColumns.size();
  }

  public String[] getzValueColumn() {
    return (String[]) zValueColumns.toArray( new String[ zValueColumns.size() ] );
  }

  public void setzValueColumn( final String[] fields ) {
    this.zValueColumns.clear();
    this.zValueColumns.addAll( Arrays.asList( fields ) );
  }

  public double getMaxZValue() {
    return maxZValue;
  }

  protected void buildDataset() {

    final ExtendedXYZDataset xyzDataset = (ExtendedXYZDataset) getDatasourceValue();

    final int maxIndex = Math.min( this.getSeriesNameCount(), this.getxValueColumnCount() );
    final DataRow dataRow = getDataRow();
    for ( int i = 0; i < maxIndex; i++ ) {
      String seriesName = this.getSeriesName( i );
      final String xColumn = this.getxValueColumn( i );
      final String yColumn = this.getyValueColumn( i );
      final String zColumn = this.getzValueColumn( i );
      final Object xValueObject = dataRow.get( xColumn );
      final Object yValueObject = dataRow.get( yColumn );
      final Object zValueObject = dataRow.get( zColumn );

      if ( isSeriesColumn() ) {
        final Object tmp = dataRow.get( seriesName );
        if ( tmp != null ) {
          seriesName = tmp.toString();
        }
      }

      if ( xValueObject instanceof Number == false ) {
        continue;
      }
      if ( yValueObject instanceof Number == false ) {
        continue;
      }
      if ( zValueObject instanceof Number == false ) {
        continue;
      }
      final Number xValue = (Number) xValueObject;
      final Number yValue = (Number) yValueObject;
      final Number zValue = (Number) zValueObject;
      if ( zValue.doubleValue() > maxZValue ) {
        maxZValue = zValue.doubleValue();
      }

      final double[][] seriesValues = new double[ 3 ][ 1 ];
      seriesValues[ 0 ][ 0 ] = xValue.doubleValue();
      seriesValues[ 1 ][ 0 ] = yValue.doubleValue();
      seriesValues[ 2 ][ 0 ] = zValue.doubleValue();

      // The way this is coded, it prevents it from
      // ever being more than one point in a data series...
      // This sounds vaguely correct for a bubble chart, 
      // and it is the way the bubble charts work in other engines..
      xyzDataset.addSeries( seriesName, seriesValues );
      xyzDataset.setMaxZValue( maxZValue );
    }
  }

  public Dataset createNewDataset() {
    maxZValue = 1.0;
    return new ExtendedXYZDataset();
  }

  /**
   * Return a completly separated copy of this function. The copy no longer shares any changeable objects with the
   * original function. Also from Thomas: Should retain data from the report definition, but clear calculated data.
   *
   * @return a copy of this function.
   */
  public Expression getInstance() {
    final XYZSeriesCollectorFunction fn = (XYZSeriesCollectorFunction) super.getInstance();
    fn.zValueColumns = (ArrayList) zValueColumns.clone();
    fn.maxZValue = 1.0; // Reset this to the default value as it's a calculated value.
    return fn;
  }

}
