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
