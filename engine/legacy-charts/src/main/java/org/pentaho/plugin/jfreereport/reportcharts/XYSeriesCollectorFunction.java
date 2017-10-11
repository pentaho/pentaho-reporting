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
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.FunctionUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Creation-Date: 02.07.2007
 *
 * @author Gretchen Moran
 * @deprecated
 */
public class XYSeriesCollectorFunction extends BaseCollectorFunction {
  private static final long serialVersionUID = -8138304452870844825L;

  private ArrayList xValueColumns;
  private ArrayList yValueColumns;

  public XYSeriesCollectorFunction() {
    this.xValueColumns = new ArrayList();
    this.yValueColumns = new ArrayList();
  }

  public void setxValueColumn( final int index, final String field ) {
    if ( xValueColumns.size() == index ) {
      xValueColumns.add( field );
    } else {
      xValueColumns.set( index, field );
    }
  }

  public void setyValueColumn( final int index, final String field ) {
    if ( yValueColumns.size() == index ) {
      yValueColumns.add( field );
    } else {
      yValueColumns.set( index, field );
    }
  }

  public String getxValueColumn( final int index ) {
    return (String) xValueColumns.get( index );
  }

  public int getxValueColumnCount() {
    return xValueColumns.size();
  }

  public String[] getxValueColumn() {
    return (String[]) xValueColumns.toArray( new String[ xValueColumns.size() ] );
  }

  public void setxValueColumn( final String[] fields ) {
    this.xValueColumns.clear();
    this.xValueColumns.addAll( Arrays.asList( fields ) );
  }

  public String getyValueColumn( final int index ) {
    return (String) yValueColumns.get( index );
  }

  public int getyValueColumnCount() {
    return yValueColumns.size();
  }

  public String[] getyValueColumn() {
    return (String[]) yValueColumns.toArray( new String[ yValueColumns.size() ] );
  }

  public void setyValueColumn( final String[] fields ) {
    this.yValueColumns.clear();
    this.yValueColumns.addAll( Arrays.asList( fields ) );
  }

  /* ---------------------------------------------------------------- Now the function implementation ...
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

  protected void buildDataset() {

    final XYSeriesCollection xySeriesDataset = (XYSeriesCollection) getDatasourceValue();
    final List seriesList = xySeriesDataset.getSeries();
    final HashMap seriesMap = new HashMap();
    for ( int i = 0; i < seriesList.size(); i++ ) {
      final XYSeries series = (XYSeries) seriesList.get( i );
      seriesMap.put( series.getKey(), series );
    }

    final String[] seriesNames = getSeriesName();
    final int maxIndex =
      Math.min( seriesNames.length, Math.min( this.xValueColumns.size(), this.yValueColumns.size() ) );
    for ( int i = 0; i < maxIndex; i++ ) {
      String seriesName = seriesNames[ i ];
      final String xColumn = (String) xValueColumns.get( i );
      final String yColumn = (String) yValueColumns.get( i );
      final Object xValueObject = getDataRow().get( xColumn );
      final Object yValueObject = getDataRow().get( yColumn );

      if ( isSeriesColumn() ) {
        final Object tmp = getDataRow().get( seriesName );
        if ( tmp != null ) {
          seriesName = tmp.toString();
        }
      }

      if ( xValueObject instanceof Number == false ) {
        continue;
      }
      final Number xValue = (Number) xValueObject;
      final Number yValue = ( yValueObject instanceof Number ) ? (Number) yValueObject : null;

      XYSeries series = (XYSeries) seriesMap.get( seriesName );
      if ( series == null ) {
        series = new XYSeries( seriesName );
        xySeriesDataset.addSeries( series );
        seriesMap.put( seriesName, series );
      }
      series.add( xValue, yValue );
    }
  }

  public Dataset createNewDataset() {
    return new XYSeriesCollection();
  }

  /**
   * Return a completly separated copy of this function. The copy no longer shares any changeable objects with the
   * original function. Also from Thomas: Should retain data from the report definition, but clear calculated data.
   *
   * @return a copy of this function.
   */
  public Expression getInstance() {
    final XYSeriesCollectorFunction fn = (XYSeriesCollectorFunction) super.getInstance();
    fn.xValueColumns = (ArrayList) xValueColumns.clone();
    fn.yValueColumns = (ArrayList) yValueColumns.clone();
    return fn;
  }
}
