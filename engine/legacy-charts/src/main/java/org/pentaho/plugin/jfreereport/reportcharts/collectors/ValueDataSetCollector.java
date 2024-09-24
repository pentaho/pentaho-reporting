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
