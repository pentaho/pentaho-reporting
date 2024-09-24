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
import org.jfree.data.general.DefaultPieDataset;

public class PieDataSetCollector extends AbstractCollectorFunction {
  private String valueColumn;

  public PieDataSetCollector() {
  }

  public String getValueColumn() {
    return valueColumn;
  }

  public void setValueColumn( final String valueColumn ) {
    this.valueColumn = valueColumn;
  }

  protected Dataset createNewDataset() {
    return new DefaultPieDataset();
  }

  protected void buildDataset() {
    final DefaultPieDataset localPieDataset = (DefaultPieDataset) getDataSet();
    final Comparable seriesComparable = querySeriesValue( 0 );
    if ( seriesComparable == null ) {
      return;
    }

    final Object valueObject = getDataRow().get( getValueColumn() );
    final Number value = ( valueObject instanceof Number ) ? (Number) valueObject : null;
    final Number existingValue =
      CollectorFunctionUtil.queryExistingValueFromDataSet( localPieDataset, seriesComparable );
    if ( existingValue != null ) {
      if ( value != null ) {
        localPieDataset.setValue( seriesComparable, CollectorFunctionUtil.add( existingValue, value ) );
      }
    } else {
      localPieDataset.setValue( seriesComparable, value );
    }
  }
}
