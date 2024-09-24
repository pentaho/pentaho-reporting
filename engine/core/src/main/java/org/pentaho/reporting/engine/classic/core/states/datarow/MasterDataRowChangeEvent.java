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
