/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.plugin.jfreereport.reportcharts.collectors;

import org.jfree.data.general.Dataset;
import org.jfree.data.general.DefaultValueDataset;

public class ValueDataSetCollector extends AbstractCollectorFunction {
  private String valueColumn;

  public ValueDataSetCollector() {
  }

  public String getValueColumn() {
    return valueColumn;
  }

  public void setValueColumn( final String valueColumn ) {
    this.valueColumn = valueColumn;
  }

  protected Dataset createNewDataset() {
    return new DefaultValueDataset();
  }

  protected void buildDataset() {
    final DefaultValueDataset localValueDataset = (DefaultValueDataset) getDataSet();

    final Object valueObject = getDataRow().get( getValueColumn() );
    final Number value = ( valueObject instanceof Number ) ? (Number) valueObject : null;
    final Number existingValue = CollectorFunctionUtil.queryExistingValueFromDataSet( localValueDataset );
    if ( existingValue != null ) {
      if ( value != null ) {
        localValueDataset.setValue( CollectorFunctionUtil.add( existingValue, value ) );
      }
    } else {
      localValueDataset.setValue( value );
    }
  }
}
