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

package org.pentaho.reporting.engine.classic.core.modules.misc.tablemodel;

import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.EmptyDataAttributes;

import java.util.ArrayList;

public class DefaultTableMetaData implements TableMetaData {
  private DefaultDataAttributes tableAttributes;
  private ArrayList<DefaultDataAttributes> columnAttributes;

  public DefaultTableMetaData( final int colCount ) {
    this.tableAttributes = new DefaultDataAttributes();
    this.columnAttributes = new ArrayList<DefaultDataAttributes>( colCount );
    for ( int i = 0; i < colCount; i++ ) {
      addColumn();
    }
  }

  @Deprecated
  public void addRow() {
    addColumn();
  }

  public void addColumn() {
    columnAttributes.add( new DefaultDataAttributes() );
  }

  /**
   * Returns the meta-attribute as Java-Object. The object type that is expected by the report engine is defined in the
   * TableMetaData property set. It is the responsibility of the implementor to map the native meta-data model into a
   * model suitable for reporting.
   * <p/>
   * Meta-data models that only describe meta-data for columns can ignore the row-parameter.
   *
   * @param row
   *          the row of the cell for which the meta-data is queried.
   * @param column
   *          the index of the column for which the meta-data is queried.
   * @return the meta-data object.
   */
  public DataAttributes getCellDataAttribute( final int row, final int column ) {
    return EmptyDataAttributes.INSTANCE;
  }

  public boolean isCellDataAttributesSupported() {
    return false;
  }

  public DataAttributes getColumnAttribute( final int column ) {
    return columnAttributes.get( column );
  }

  /**
   * Returns table-wide attributes. This usually contain hints about the data-source used to query the data as well as
   * hints on the sort-order of the data.
   *
   * @return
   */
  public DataAttributes getTableAttribute() {
    return tableAttributes;
  }

  public void setColumnAttribute( final int column, final String metaAttributeDomain, final String metaAttributeId,
      final Object value ) {
    final DefaultDataAttributes colAtts = columnAttributes.get( column );
    colAtts.setMetaAttribute( metaAttributeDomain, metaAttributeId, DefaultConceptQueryMapper.INSTANCE, value );
  }

  public void setTableAttribute( final String namespace, final String name, final Object value ) {
    tableAttributes.setMetaAttribute( namespace, name, DefaultConceptQueryMapper.INSTANCE, value );
  }

}
