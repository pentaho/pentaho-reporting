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


package org.pentaho.reporting.engine.classic.core.states.datarow;

public final class MasterDataRowChangeEvent {
  public static final int COLUMN_ADDED = 1;
  public static final int COLUMN_REMOVED = 2;
  public static final int COLUMN_UPDATED = 3;

  private int type;
  private String columnName;
  private Object columnValue;
  private boolean optional;

  public MasterDataRowChangeEvent() {
  }

  public void reuse( final int type, final String columnName, final Object columnValue ) {
    if ( type < 1 || type > 3 ) {
      throw new IllegalArgumentException();
    }
    if ( columnName == null ) {
      throw new NullPointerException();
    }
    this.type = type;
    this.columnName = columnName;
    this.columnValue = columnValue;
  }

  public String getColumnName() {
    return columnName;
  }

  public Object getColumnValue() {
    return columnValue;
  }

  public int getType() {
    return type;
  }

  public void setColumnName( final String columnName ) {
    if ( columnName == null ) {
      throw new NullPointerException();
    }
    this.columnName = columnName;
  }

  public void setColumnValue( final Object columnValue ) {
    this.columnValue = columnValue;
  }

  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append( "MasterDataRowChangeEvent" );
    sb.append( "{type=" ).append( type );
    sb.append( ", columnName='" ).append( columnName ).append( '\'' );
    sb.append( ", columnValue=" ).append( columnValue );
    sb.append( '}' );
    return sb.toString();
  }

  public boolean isOptional() {
    return optional;
  }

  public void setOptional( final boolean optional ) {
    this.optional = optional;
  }
}
