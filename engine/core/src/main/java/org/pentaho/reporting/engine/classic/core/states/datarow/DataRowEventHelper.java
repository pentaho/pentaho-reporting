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

package org.pentaho.reporting.engine.classic.core.states.datarow;

import org.pentaho.reporting.engine.classic.core.DataRow;

public class DataRowEventHelper {
  public static void addColumns( final DataRow dataRow, final MasterDataRowChangeHandler globalView ) {
    final MasterDataRowChangeEvent event = globalView.getReusableEvent();
    event.reuse( MasterDataRowChangeEvent.COLUMN_ADDED, "", null );
    final String[] columnNames = dataRow.getColumnNames();
    for ( int i = 0; i < columnNames.length; i++ ) {
      final String columnName = columnNames[i];
      if ( columnName != null ) {
        final Object columnValue = dataRow.get( columnName );
        event.setColumnName( columnName );
        event.setColumnValue( columnValue );
        globalView.dataRowChanged( event );
      }
    }
  }

  public static void removeAllColumns( final DataRow dataRow, final MasterDataRowChangeHandler globalView ) {
    final MasterDataRowChangeEvent event = globalView.getReusableEvent();
    event.reuse( MasterDataRowChangeEvent.COLUMN_REMOVED, "", null );
    final String[] columnNames = dataRow.getColumnNames();
    for ( int i = 0; i < columnNames.length; i++ ) {
      final String columnName = columnNames[i];
      if ( columnName != null ) {
        event.setColumnName( columnName );
        globalView.dataRowChanged( event );
      }
    }
  }

  public static void refreshDataRow( final DataRow dataRow, final MasterDataRowChangeHandler changeHandler ) {
    final String[] columnNames = dataRow.getColumnNames();
    final MasterDataRowChangeEvent event = changeHandler.getReusableEvent();
    event.reuse( MasterDataRowChangeEvent.COLUMN_UPDATED, "", null );
    for ( int i = 0; i < columnNames.length; i++ ) {
      final String columnName = columnNames[i];
      if ( columnName != null ) {
        final Object columnValue = dataRow.get( columnName );
        event.setColumnName( columnName );
        event.setColumnValue( columnValue );
        changeHandler.dataRowChanged( event );
      }
    }
  }

}
