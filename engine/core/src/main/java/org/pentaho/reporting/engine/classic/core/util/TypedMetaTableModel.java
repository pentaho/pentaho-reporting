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
