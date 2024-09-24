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
