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

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.util.ObjectUtilities;
import org.pentaho.reporting.engine.classic.core.function.Expression;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @deprecated This class is totally f'd up.
 */
public class PivotCategorySetCollectorFunction extends CategorySetCollectorFunction {
  private static final long serialVersionUID = -1661805548096343053L;

  private ArrayList categoryNames;
  private String seriesColumnHeader;

  public PivotCategorySetCollectorFunction() {
    this.categoryNames = new ArrayList();
  }

  /*
   * ------- Standard accessors - we use indexed properties for the series config.
   */

  public String getSeriesColumnHeader() {
    return seriesColumnHeader;
  }

  public void setSeriesColumnHeader( final String seriesColumnHeader ) {
    this.seriesColumnHeader = seriesColumnHeader;
  }


  public void setCategoryName( final int index, final String field ) {
    if ( categoryNames.size() == index ) {
      categoryNames.add( field );
    } else {
      categoryNames.set( index, field );
    }
  }

  public String getCategoryName( final int index ) {
    return (String) this.categoryNames.get( index );
  }

  public int getCategoryNameCount() {
    return this.categoryNames.size();
  }

  public String[] getCategoryName() {
    return (String[]) this.categoryNames.toArray( new String[ this.categoryNames.size() ] );
  }

  public void setCategoryName( final String[] fields ) {
    this.categoryNames.clear();
    this.categoryNames.addAll( Arrays.asList( fields ) );
  }

  protected void buildDataset() {
    final DefaultCategoryDataset dataset = (DefaultCategoryDataset) getDatasourceValue();

    //get value in column that identifies the rows        
    final Object currentRowHeader = getDataRow().get( seriesColumnHeader );

    //Check if row is part of the graph series
    final int catCount = getCategoryNameCount();
    final int seriesNameCount = getSeriesNameCount();

    for ( int i = 0; i < seriesNameCount; i++ ) {
      final String seriesName = getSeriesName( i );
      if ( ObjectUtilities.equal( seriesName, currentRowHeader ) == false ) {
        // filters out all series except the one marked as seriesColumnHeader.
        continue;
      }

      for ( int j = 0; j < catCount; j++ ) {

        final String newRowHeader = getCategoryName( j );

        //if not rowHeader Column
        if ( ObjectUtilities.equal( newRowHeader, seriesColumnHeader ) ) {
          continue;
        }

        final Object valueObject = getDataRow().get( newRowHeader );
        Number value = ( valueObject instanceof Number ) ? (Number) valueObject : null;
        final Number existingValue = queryExistingValueFromDataSet( dataset, seriesName, newRowHeader );

        if ( existingValue != null ) {
          final double val = ( value != null ) ? value.doubleValue() : 0;
          value = new Double( val + existingValue.doubleValue() );
          dataset.setValue( value, seriesName, newRowHeader );
        } else {
          dataset.addValue( value, seriesName, newRowHeader );
        }
      }
    }
  }

  /**
   * Return a completly separated copy of this function. The copy no longer shares any changeable objects with the
   * original function. Also from Thomas: Should retain data from the report definition, but clear calculated data.
   *
   * @return a copy of this function.
   */
  public Expression getInstance() {
    final PivotCategorySetCollectorFunction fn = (PivotCategorySetCollectorFunction) super.getInstance();
    fn.categoryNames = (ArrayList) categoryNames.clone();
    return fn;
  }

}
