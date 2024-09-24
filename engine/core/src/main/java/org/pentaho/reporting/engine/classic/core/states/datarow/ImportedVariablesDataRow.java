/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.states.datarow;

import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;

import java.util.HashMap;

public class ImportedVariablesDataRow extends StaticDataRow {
  private HashMap<String, DataAttributes> dataAttributes;
  private String[] outerNames;
  private String[] innerNames;

  public ImportedVariablesDataRow( final MasterDataRow innerRow ) {
    if ( innerRow == null ) {
      throw new NullPointerException();
    }

    final DataRow globalView = innerRow.getGlobalView();
    final String[] names = globalView.getColumnNames();
    final int cols = names.length;
    this.dataAttributes = new HashMap<String, DataAttributes>();
    this.outerNames = new String[cols];
    this.innerNames = outerNames;
    final Object[] values = new Object[cols];
    final DataSchema dataSchema = innerRow.getDataSchema();
    for ( int i = 0; i < cols; i++ ) {
      final String name = names[i];
      if ( name == null ) {
        throw new IllegalStateException( "Every column must have a name." );
      }
      outerNames[i] = name;
      values[i] = globalView.get( name );

      dataAttributes.put( name, dataSchema.getAttributes( name ) );
    }
    setData( outerNames, values );
  }

  /**
   * Maps the inner-row into the outer data row. The parameter mapping's name represents the *outer* name and the
   * innernames.
   * <p/>
   * Note: This does not import actual values. You have to call "refresh" before you can use this data-row.
   *
   * @param innerRow
   * @param parameterMappings
   */
  public ImportedVariablesDataRow( final MasterDataRow innerRow, final ParameterMapping[] parameterMappings ) {
    if ( innerRow == null ) {
      throw new NullPointerException();
    }
    if ( parameterMappings == null ) {
      throw new NullPointerException();
    }

    // final DataRow globalView = innerRow.getGlobalView();
    final int cols = parameterMappings.length;
    this.dataAttributes = new HashMap<String, DataAttributes>();
    this.outerNames = new String[cols];
    this.innerNames = outerNames;
    final Object[] values = new Object[cols];
    setData( outerNames, values );
    for ( int i = 0; i < cols; i++ ) {
      final ParameterMapping mapping = parameterMappings[i];
      final String name = mapping.getAlias();
      if ( name == null ) {
        throw new IllegalStateException( "Every column must have a name." );
      }
      outerNames[i] = name;
    }
    setData( outerNames, values );
  }

  protected ImportedVariablesDataRow( final ImportedVariablesDataRow dataRow ) {
    super( dataRow );
    outerNames = dataRow.outerNames;
    innerNames = dataRow.innerNames;
    dataAttributes = (HashMap<String, DataAttributes>) dataRow.dataAttributes.clone();
  }

  public ImportedVariablesDataRow refresh( final DataRow globalView, final DataSchema dataSchema ) {
    if ( globalView == null ) {
      throw new NullPointerException();
    }
    if ( dataSchema == null ) {
      throw new NullPointerException();
    }

    final int length = innerNames.length;
    final Object[] values = new Object[length];
    for ( int i = 0; i < length; i++ ) {
      final String name = innerNames[i];
      values[i] = globalView.get( name );
      dataAttributes.put( outerNames[i], dataSchema.getAttributes( name ) );
    }
    final ImportedVariablesDataRow idr = new ImportedVariablesDataRow( this );
    idr.setData( outerNames, values );
    return idr;
  }

  public DataAttributes getAttributes( final String name ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    return dataAttributes.get( name );
  }

}
