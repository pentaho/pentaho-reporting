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

package org.pentaho.reporting.engine.classic.core.util;

import org.pentaho.reporting.engine.classic.core.MetaTableModel;
import org.pentaho.reporting.engine.classic.core.modules.misc.tablemodel.DefaultTableMetaData;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.EmptyDataAttributes;

public class TypedMetaTableModel extends TypedTableModel implements MetaTableModel {
  private DefaultTableMetaData tableMetaData;

  public TypedMetaTableModel() {
    tableMetaData = new DefaultTableMetaData( 0 );
  }

  public TypedMetaTableModel( final int rowIncrement, final int columnIncrement ) {
    super( rowIncrement, columnIncrement );
    tableMetaData = new DefaultTableMetaData( 0 );
  }

  public TypedMetaTableModel( final String[] columnNames ) {
    super( columnNames );
    tableMetaData = new DefaultTableMetaData( columnNames.length );
  }

  public TypedMetaTableModel( final String[] columnNames, final Class[] columnClasses ) {
    super( columnNames, columnClasses );
    tableMetaData = new DefaultTableMetaData( columnNames.length );
  }

  public TypedMetaTableModel( final String[] columnNames, final Class[] columnClasses, final int rowCount ) {
    super( columnNames, columnClasses, rowCount );
    tableMetaData = new DefaultTableMetaData( columnNames.length );
  }

  public void addColumn( final String name, final Class<?> type ) {
    super.addColumn( name, type );
    tableMetaData.addColumn();
  }

  public DataAttributes getCellDataAttributes( final int row, final int column ) {
    return EmptyDataAttributes.INSTANCE;
  }

  public boolean isCellDataAttributesSupported() {
    return false;
  }

  public void setColumnAttribute( final int column, final String metaAttributeDomain, final String metaAttributeId,
      final Object value ) {
    tableMetaData.setColumnAttribute( column, metaAttributeDomain, metaAttributeId, value );
  }

  public void setTableAttribute( final String namespace, final String name, final Object value ) {
    tableMetaData.setTableAttribute( namespace, name, value );
  }

  public DataAttributes getColumnAttributes( final int column ) {
    return tableMetaData.getColumnAttribute( column );
  }

  public DataAttributes getTableAttributes() {
    return tableMetaData.getTableAttribute();
  }
}
